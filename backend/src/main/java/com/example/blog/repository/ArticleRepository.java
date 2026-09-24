package com.example.blog.repository;

import com.example.blog.common.TimeFormats;
import com.example.blog.model.ArticleSummaryVO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * 文章数据访问层：只用 JdbcTemplate 执行 SQL 与结果映射，不写业务判断。
 *
 * <p>统计口径见 docs/data-model.md 第三节「方案 A」：点赞数与评论数在查询时用子查询实时 COUNT，
 * 不落冗余列，因此不存在计数漂移问题。
 *
 * <p>本类当前只含**读方法**（阶段 2 批 2a）；写方法（insert / update / delete 与标签关联维护）在批 2b 补齐。
 */
@Repository
public class ArticleRepository {

    /** 列表与详情共用的列清单（含两个实时计数子查询）。 */
    private static final String SUMMARY_COLUMNS = """
            a.id, a.title, a.summary, a.cover_url, a.status, a.created_at, a.updated_at,
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
                tags, summary.likeCount(), summary.commentCount(), summary.createdAt(), summary.updatedAt());
    }
}
