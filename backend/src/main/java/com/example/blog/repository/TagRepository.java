package com.example.blog.repository;

import com.example.blog.common.TimeFormats;
import com.example.blog.model.Tag;
import com.example.blog.model.TagVO;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * 标签数据访问层（契约 §四 · 7、17）：列表查询、文章保存时的"按名取 / 自动创建"，以及
 * 标签管理（新建 / 改名 / 删除）所需的读写。
 */
@Repository
public class TagRepository {

    private final JdbcTemplate jdbcTemplate;

    public TagRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 标签列表 + 每个标签关联的文章数（契约 §四 · 7）。
     *
     * <p>用 LEFT JOIN 保证"还没有文章的标签"也能出现（计数为 0）；排序为文章数倒序、名称升序。
     */
    public List<TagVO> findAllWithArticleCount() {
        String sql = """
                SELECT t.id, t.name, COUNT(at.article_id) AS article_count
                FROM tag t
                LEFT JOIN article_tag at ON at.tag_id = t.id
                GROUP BY t.id, t.name
                ORDER BY article_count DESC, t.name ASC
                """;
        return jdbcTemplate.query(sql,
                (rs, rowNum) -> new TagVO(rs.getLong("id"), rs.getString("name"), rs.getInt("article_count")));
    }

    /** 按名称精确查标签（表上有 UNIQUE 约束，最多一条）。 */
    public Optional<Tag> findByName(String name) {
        List<Tag> rows = jdbcTemplate.query(
                "SELECT id, name, created_at FROM tag WHERE name = ?",
                (rs, rowNum) -> {
                    Tag tag = new Tag();
                    tag.setId(rs.getLong("id"));
                    tag.setName(rs.getString("name"));
                    tag.setCreatedAt(TimeFormats.parse(rs.getString("created_at")));
                    return tag;
                },
                name);
        return rows.stream().findFirst();
    }

    /** 插入标签，返回数据库生成的主键。 */
    public long insert(Tag tag) {
        String sql = "INSERT INTO tag (name, created_at) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, tag.getName());
            statement.setString(2, TimeFormats.format(tag.getCreatedAt()));
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("插入标签后未取到自增主键");
        }
        return key.longValue();
    }

    /** 按 id 取标签响应体（含实时 articleCount），供新建 / 改名回读使用。 */
    public Optional<TagVO> findVoById(long id) {
        String sql = """
                SELECT t.id, t.name, COUNT(at.article_id) AS article_count
                FROM tag t
                LEFT JOIN article_tag at ON at.tag_id = t.id
                WHERE t.id = ?
                GROUP BY t.id, t.name
                """;
        List<TagVO> rows = jdbcTemplate.query(sql,
                (rs, rowNum) -> new TagVO(rs.getLong("id"), rs.getString("name"), rs.getInt("article_count")), id);
        return rows.stream().findFirst();
    }

    /** 标签改名，返回受影响行数（0 表示标签不存在）。 */
    public int rename(long id, String name) {
        return jdbcTemplate.update("UPDATE tag SET name = ? WHERE id = ?", name, id);
    }

    /** 删除标签，返回受影响行数（0 表示标签不存在）；article_tag 由外键 ON DELETE CASCADE 解除。 */
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM tag WHERE id = ?", id);
    }
}
