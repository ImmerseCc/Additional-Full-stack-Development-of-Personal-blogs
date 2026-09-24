package com.example.blog.model;

/**
 * 点赞状态响应体（契约 §二 · 2.5）：{@code { articleId, liked, likeCount }}。
 *
 * <p>前端用它初始化点赞按钮：{@code liked} 表示"本机访客是否已赞"，{@code likeCount} 是后端实时总数。
 */
public record LikeStateVO(Long articleId, boolean liked, int likeCount) {
}
