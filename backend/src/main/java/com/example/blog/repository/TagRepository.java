package com.example.blog.repository;

import com.example.blog.model.TagVO;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 标签数据访问层（阶段 2 批 2a：只含列表查询；标签管理写方法属阶段 8 可选项）。
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
}
