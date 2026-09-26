package com.example.blog.repository;

import com.example.blog.common.TimeFormats;
import com.example.blog.model.AdjacentVO;
import com.example.blog.model.Article;
import com.example.blog.model.ArticleSummaryVO;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * 文章数据访问层：只用 JdbcTemplate 执行 SQL 与结果映射，不写业务判断。
 *
 * <p>统计口径见 docs/data-model.md 第三节「方案 A」：点赞数与评论数在查询时用子查询实时 COUNT，
 * 不落冗余列，因此不存在计数漂移问题。
 *
 * <p>读方法（列表 / 详情 / 标签批量查询）与写方法（插入 / 更新 / 删除 / 标签关联重建）都在本类；
 * 评论与点赞的数据访问在批 3 单独实现。
 */
@Repository
public class ArticleRepository {

    /** 列表与详情共用的列清单（含两个实时计数子查询；view_count 为表自带列）。 */
    private static final String SUMMARY_COLUMNS = """
            a.id, a.title, a.summary, a.cover_url, a.status, a.view_count, a.created_at, a.updated_at,
            (SELECT COUNT(*) FROM like_record l WHERE l.article_id = a.id) AS like_count,
            (SELECT COUNT(*) FROM comment c WHERE c.article_id = a.id) AS comment_count
            """;

    private static final RowMapper<ArticleSummaryVO> SUMMARY_ROW_MAPPER = (rs, rowNum) -> new ArticleSummaryVO(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("summary"),
            rs.getString("cover_url"),
            rs.getString("status"),
            List.of(),
            rs.getInt("like_count"),
            rs.getInt("comment_count"),
            rs.getInt("view_count"),
            TimeFormats.parse(rs.getString("created_at")),
            TimeFormats.parse(rs.getString("updated_at")));

    private final JdbcTemplate jdbcTemplate;

    public ArticleRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 按过滤条件分页查询文章列表。
     *
     * @param keyword      标题关键词，null 表示不过滤
     * @param tagNames     标签名（已在 service 去重），空集合表示不过滤
     * @param matchAllTags true = 需同时包含全部标签（and），false = 任一标签即可（or）
     * @param status       状态过滤，null 表示不限状态
     * @param limit        每页条数
     * @param offset       偏移量
     */
    public List<ArticleSummaryVO> findSummaries(String keyword, List<String> tagNames, boolean matchAllTags,
                                                String status, int limit, int offset) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT " + SUMMARY_COLUMNS + " FROM article a"
                + buildWhere(keyword, tagNames, matchAllTags, status, params)
                + " ORDER BY a.created_at DESC, a.id DESC LIMIT ? OFFSET ?";
        params.add(limit);
        params.add(offset);
        return attachTags(jdbcTemplate.query(sql, SUMMARY_ROW_MAPPER, params.toArray()));
    }

    /** 与 {@link #findSummaries} 相同过滤条件下的总条数。 */
    public long countSummaries(String keyword, List<String> tagNames, boolean matchAllTags, String status) {
        List<Object> params = new ArrayList<>();
        String sql = "SELECT COUNT(*) FROM article a" + buildWhere(keyword, tagNames, matchAllTags, status, params);
        Long total = jdbcTemplate.queryForObject(sql, Long.class, params.toArray());
        return (total == null) ? 0L : total;
    }

    /** 单篇文章的列表级信息（含实时计数、含标签，不含正文），供详情接口使用。 */
    public Optional<ArticleSummaryVO> findSummaryById(long id) {
        List<ArticleSummaryVO> rows = jdbcTemplate.query(
                "SELECT " + SUMMARY_COLUMNS + " FROM article a WHERE a.id = ?", SUMMARY_ROW_MAPPER, id);
        if (rows.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(attachTags(rows).get(0));
    }

    /** 单篇文章的正文（Markdown 原文），供详情接口使用。 */
    public Optional<String> findContentById(long id) {
        List<String> rows = jdbcTemplate.query(
                "SELECT a.content FROM article a WHERE a.id = ?", (rs, rowNum) -> rs.getString("content"), id);
        return rows.stream().findFirst();
    }

    /** 相邻文章的行映射（只有 id 与标题）。 */
    private static final RowMapper<AdjacentVO> ADJACENT_ROW_MAPPER =
            (rs, rowNum) -> new AdjacentVO(rs.getLong("id"), rs.getString("title"));

    /**
     * 相邻文章（契约 §四 · 16，决策 BT）：before = 更早的一篇（prev）。
     *
     * <p>候选只含 {@code PUBLISHED}；排序口径为 {@code (created_at, id)}，用行值比较避免同秒歧义；
     * 当前文章已是最早一篇时返回空（调用方转成 null）。
     */
    public Optional<AdjacentVO> findAdjacentBefore(long id) {
        String sql = "SELECT a.id, a.title FROM article a WHERE a.status = ?"
                + " AND (a.created_at, a.id) < (SELECT b.created_at, b.id FROM article b WHERE b.id = ?)"
                + " ORDER BY a.created_at DESC, a.id DESC LIMIT 1";
        return jdbcTemplate.query(sql, ADJACENT_ROW_MAPPER, Article.STATUS_PUBLISHED, id).stream().findFirst();
    }

    /** 相邻文章的后一篇（更新的一篇），口径同 {@link #findAdjacentBefore(long)}。 */
    public Optional<AdjacentVO> findAdjacentAfter(long id) {
        String sql = "SELECT a.id, a.title FROM article a WHERE a.status = ?"
                + " AND (a.created_at, a.id) > (SELECT b.created_at, b.id FROM article b WHERE b.id = ?)"
                + " ORDER BY a.created_at ASC, a.id ASC LIMIT 1";
        return jdbcTemplate.query(sql, ADJACENT_ROW_MAPPER, Article.STATUS_PUBLISHED, id).stream().findFirst();
    }

    /** 阅读数 +1（契约 §四 · 18）：不做去重；返回受影响行数（0 表示文章不存在）。 */
    public int incrementViewCount(long id) {
        return jdbcTemplate.update("UPDATE article SET view_count = view_count + 1 WHERE id = ?", id);
    }

    /** 读取当前阅读数（自增后回读用）。 */
    public Optional<Integer> findViewCountById(long id) {
        List<Integer> rows = jdbcTemplate.query("SELECT a.view_count FROM article a WHERE a.id = ?",
                (rs, rowNum) -> rs.getInt("view_count"), id);
        return rows.stream().findFirst();
    }

    /**
     * 批量取标签名（避免列表页 N+1 查询）。
     *
     * @return articleId → 标签名列表，顺序按 tag.id 升序
     */
    public Map<Long, List<String>> findTagNamesByArticleIds(List<Long> articleIds) {
        if (articleIds == null || articleIds.isEmpty()) {
            return Map.of();
        }
        String placeholders = String.join(", ", Collections.nCopies(articleIds.size(), "?"));
        String sql = "SELECT at.article_id, t.name FROM article_tag at JOIN tag t ON t.id = at.tag_id"
                + " WHERE at.article_id IN (" + placeholders + ") ORDER BY at.article_id, t.id";
        Map<Long, List<String>> tagsByArticle = new LinkedHashMap<>();
        jdbcTemplate.query(sql, rs -> {
            tagsByArticle.computeIfAbsent(rs.getLong("article_id"), key -> new ArrayList<>())
                    .add(rs.getString("name"));
        }, articleIds.toArray());
        return tagsByArticle;
    }

    /** 文章是否存在（评论 / 点赞接口的存在性校验用）。 */
    public boolean existsById(long id) {
        Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM article WHERE id = ?", Integer.class, id);
        return count != null && count > 0;
    }

    /**
     * 插入文章，返回数据库生成的主键。
     *
     * <p>{@code view_count} 不写入，交给建表默认值 0（契约不对外暴露阅读数）。
     */
    public long insert(Article article) {
        String sql = "INSERT INTO article (title, summary, content, cover_url, status, created_at, updated_at)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, article.getTitle());
            statement.setString(2, article.getSummary());
            statement.setString(3, article.getContent());
            statement.setString(4, article.getCoverUrl());
            statement.setString(5, article.getStatus());
            statement.setString(6, TimeFormats.format(article.getCreatedAt()));
            statement.setString(7, TimeFormats.format(article.getUpdatedAt()));
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("插入文章后未取到自增主键");
        }
        return key.longValue();
    }

    /** 全量更新文章字段（不改 created_at），返回受影响行数，0 表示文章不存在。 */
    public int update(long id, Article article) {
        String sql = "UPDATE article SET title = ?, summary = ?, content = ?, cover_url = ?, status = ?,"
                + " updated_at = ? WHERE id = ?";
        return jdbcTemplate.update(sql, article.getTitle(), article.getSummary(), article.getContent(),
                article.getCoverUrl(), article.getStatus(), TimeFormats.format(article.getUpdatedAt()), id);
    }

    /** 删除文章，返回受影响行数，0 表示文章不存在；评论 / 点赞 / 标签关联由外键 ON DELETE CASCADE 清理。 */
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM article WHERE id = ?", id);
    }

    /** 覆盖式重建文章标签关联：先清空，再按 tagIds 写入（空列表 = 清空标签）。 */
    public void replaceTags(long articleId, List<Long> tagIds) {
        jdbcTemplate.update("DELETE FROM article_tag WHERE article_id = ?", articleId);
        if (tagIds == null || tagIds.isEmpty()) {
            return;
        }
        List<Object[]> batch = tagIds.stream()
                .map(tagId -> new Object[] {articleId, tagId})
                .toList();
        jdbcTemplate.batchUpdate("INSERT INTO article_tag (article_id, tag_id) VALUES (?, ?)", batch);
    }

    /** 拼 WHERE 子句，并把占位符对应的参数按顺序追加进 params。 */
    private static String buildWhere(String keyword, List<String> tagNames, boolean matchAllTags,
                                     String status, List<Object> params) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");
        if (status != null) {
            where.append(" AND a.status = ?");
            params.add(status);
        }
        if (keyword != null && !keyword.isBlank()) {
            where.append(" AND a.title LIKE ? ESCAPE '\\'");
            params.add("%" + escapeLike(keyword.trim()) + "%");
        }
        if (tagNames != null && !tagNames.isEmpty()) {
            String placeholders = String.join(", ", Collections.nCopies(tagNames.size(), "?"));
            where.append(" AND a.id IN (SELECT at.article_id FROM article_tag at JOIN tag t ON t.id = at.tag_id")
                    .append(" WHERE t.name IN (").append(placeholders).append(")");
            if (matchAllTags) {
                where.append(" GROUP BY at.article_id HAVING COUNT(DISTINCT at.tag_id) = ")
                        .append(tagNames.size());
            }
            where.append(")");
            params.addAll(tagNames);
        }
        return where.toString();
    }

    /** 转义 LIKE 通配符，配合 {@code ESCAPE '\'} 使用，避免用户输入的 % / _ 被当作通配符。 */
    private static String escapeLike(String keyword) {
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    /** 给一批列表项补上标签名（一次 IN 查询批量取回）。 */
    private List<ArticleSummaryVO> attachTags(List<ArticleSummaryVO> summaries) {
        if (summaries.isEmpty()) {
            return summaries;
        }
        List<Long> ids = summaries.stream().map(ArticleSummaryVO::id).toList();
        Map<Long, List<String>> tagsByArticle = findTagNamesByArticleIds(ids);
        return summaries.stream()
                .map(summary -> withTags(summary, tagsByArticle.getOrDefault(summary.id(), List.of())))
                .toList();
    }

    /** 用标签名重建列表项（record 不可变，只能新建）。 */
    private static ArticleSummaryVO withTags(ArticleSummaryVO summary, List<String> tags) {
        return new ArticleSummaryVO(
                summary.id(), summary.title(), summary.summary(), summary.coverUrl(), summary.status(),
                tags, summary.likeCount(), summary.commentCount(), summary.viewCount(),
                summary.createdAt(), summary.updatedAt());
    }
}
