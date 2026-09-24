package com.example.blog.model;

import java.time.LocalDateTime;

/**
 * 评论响应体（契约 §二 · 2.4）。
 *
 * <p>刻意不含 {@code authorEmail} 与 {@code visitorId}：前者是隐私，后者是归属凭证，都不回传前端。
 */
public record CommentVO(
        Long id,
        Long articleId,
        String authorName,
        String content,
        LocalDateTime createdAt) {
}
