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
 * 标签数据访问层：列表查询 + 供"标签自动创建"使用的最小写方法。
 *
 * <p>标签管理接口（改名 / 删除）属阶段 8 可选项，阶段 2 不实现。
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
}
