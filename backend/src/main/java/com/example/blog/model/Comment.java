package com.example.blog.model;

import java.time.LocalDateTime;

/**
 * 评论实体，对应 {@code comment} 表（见 docs/data-model.md 2.4）。
 *
 * <p>{@code authorEmail} 与 {@code visitorId} 只用于服务端保存与归属校验，不通过接口回传。
 */
public class Comment {

    private Long id;
    private Long articleId;
    private String authorName;
    private String authorEmail;
    private String content;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
