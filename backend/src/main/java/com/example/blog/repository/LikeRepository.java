package com.example.blog.repository;

import com.example.blog.common.TimeFormats;
import com.example.blog.model.LikeRecord;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * 点赞数据访问层（契约 §四 · 13–15）。
 *
 * <p>{@code like_record} 上有 {@code UNIQUE (article_id, visitor_id)}，同一访客对同一文章最多一条记录，
 * 因此点赞 / 取消点赞接口天然幂等。
 */
@Repository
public class LikeRepository {

    private final JdbcTemplate jdbcTemplate;

    public LikeRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /** 查某访客对某文章的点赞记录：存在即表示"已点赞"。 */
    public Optional<LikeRecord> findByArticleIdAndVisitorId(long articleId, String visitorId) {
        String sql = "SELECT id, article_id, visitor_id, created_at FROM like_record"
                + " WHERE article_id = ? AND visitor_id = ?";
        List<LikeRecord> rows = jdbcTemplate.query(sql, (rs, rowNum) -> {
            LikeRecord record = new LikeRecord();
            record.setId(rs.getLong("id"));
            record.setArticleId(rs.getLong("article_id"));
            record.setVisitorId(rs.getString("visitor_id"));
            record.setCreatedAt(TimeFormats.parse(rs.getString("created_at")));
            return record;
        }, articleId, visitorId);
        return rows.stream().findFirst();
    }

    /** 某文章的点赞总数。 */
    public int countByArticleId(long articleId) {
        Integer total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM like_record WHERE article_id = ?", Integer.class, articleId);
        return (total == null) ? 0 : total;
    }

    /** 写入点赞记录，返回数据库生成的主键。 */
    public long insert(LikeRecord record) {
        String sql = "INSERT INTO like_record (article_id, visitor_id, created_at) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            statement.setLong(1, record.getArticleId());
            statement.setString(2, record.getVisitorId());
            statement.setString(3, TimeFormats.format(record.getCreatedAt()));
            return statement;
        }, keyHolder);
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("插入点赞记录后未取到自增主键");
        }
        return key.longValue();
    }

    /** 取消点赞；未点赞时返回 0，接口据此保持幂等。 */
    public int deleteByArticleIdAndVisitorId(long articleId, String visitorId) {
        return jdbcTemplate.update("DELETE FROM like_record WHERE article_id = ? AND visitor_id = ?",
                articleId, visitorId);
    }
}
