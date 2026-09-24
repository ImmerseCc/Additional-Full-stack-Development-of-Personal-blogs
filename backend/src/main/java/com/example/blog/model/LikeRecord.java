package com.example.blog.model;

import java.time.LocalDateTime;

/**
 * 点赞记录实体，对应 {@code like_record} 表（见 docs/data-model.md 2.5）。
 *
 * <p>表上有 {@code UNIQUE (article_id, visitor_id)} 约束，因此"同一访客对同一文章"最多一条记录，
 * 点赞 / 取消点赞接口据此实现幂等。
 */
public class LikeRecord {

    private Long id;
    private Long articleId;
    private String visitorId;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
