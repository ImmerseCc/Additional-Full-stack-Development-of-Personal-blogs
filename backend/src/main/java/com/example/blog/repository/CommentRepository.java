package com.example.blog.repository;

import com.example.blog.common.TimeFormats;
import com.example.blog.model.Comment;
import com.example.blog.model.CommentVO;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * 评论数据访问层（契约 §四 · 8、9、12）。
 *
 * <p>列表查询刻意不选出 {@code author_email} 与 {@code visitor_id}：前者是隐私、后者是归属凭证，
 * 只在删除前的归属校验（{@link #findById(long)}）里读取。
 */
@Repository
public class CommentRepository {

    private static final RowMapper<CommentVO> COMMENT_VO_ROW_MAPPER = (rs, rowNum) -> new CommentVO(
            rs.getLong("id"),
            rs.getLong("article_id"),
            rs.getString("author_name"),
            rs.getString("content"),
            TimeFormats.parse(rs.getString("created_at")));

    private final JdbcTemplate jdbcTemplate;

    public CommentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 某文章的评论分页（创建时间倒序；同秒时按 id 倒序，保证顺序稳定）。 */
    public List<CommentVO> findPageByArticleId(long articleId, int limit, int offset) {
        String sql = "SELECT id, article_id, author_name, content, created_at FROM comment"
                + " WHERE article_id = ? ORDER BY created_at DESC, id DESC LIMIT ? OFFSET ?";
        return jdbcTemplate.query(sql, COMMENT_VO_ROW_MAPPER, articleId, limit, offset);
    }

    /** 某文章的评论总数。 */
    public long countByArticleId(long articleId) {
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM comment WHERE article_id = ?", Long.class, articleId);
        return (total == null) ? 0L : total;
    }

    /** 插入评论，返回数据库生成的主键。 */
    public long insert(Comment comment) {
        String sql = "INSERT INTO comment (article_id, author_name, author_email, content, visitor_id, created_at)"
                + " VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, comment.getArticleId());
            statement.setString(2, comment.getAuthorName());
            statement.setString(3, comment.getAuthorEmail());
            statement.setString(4, comment.getContent());
            statement.setString(5, comment.getVisitorId());
            statement.setString(6, TimeFormats.format(comment.getCreatedAt()));
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("插入评论后未取到自增主键");
        }
        return key.longValue();
    }

    /** 按 id 取完整评论（含 {@code visitor_id} / {@code author_email}），仅供删除时的归属校验使用。 */
    public Optional<Comment> findById(long id) {
        List<Comment> rows = jdbcTemplate.query("SELECT * FROM comment WHERE id = ?", (rs, rowNum) -> {
            Comment comment = new Comment();
            comment.setId(rs.getLong("id"));
            comment.setArticleId(rs.getLong("article_id"));
            comment.setAuthorName(rs.getString("author_name"));
            comment.setAuthorEmail(rs.getString("author_email"));
            comment.setContent(rs.getString("content"));
            comment.setVisitorId(rs.getString("visitor_id"));
            comment.setCreatedAt(TimeFormats.parse(rs.getString("created_at")));
            return comment;
        }, id);
        return rows.stream().findFirst();
    }

    /** 删除评论，返回受影响行数。 */
    public int deleteById(long id) {
        return jdbcTemplate.update("DELETE FROM comment WHERE id = ?", id);
    }
}
