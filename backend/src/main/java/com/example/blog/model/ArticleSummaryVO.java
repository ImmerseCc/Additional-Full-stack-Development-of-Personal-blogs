package com.example.blog.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章列表项响应体（契约 §二 · 2.2）。
 *
 * <p>{@code tags} 是标签名列表；{@code likeCount} / {@code commentCount} 由 SQL 子查询实时统计，
 * 详见 docs/data-model.md 第三节（方案 A）；{@code viewCount} 为表自带列，阶段 8 起经
 * {@code POST /api/articles/{id}/views} 自增（决策 BU）。
 */
public record ArticleSummaryVO(
        Long id,
        String title,
        String summary,
        String coverUrl,
        String status,
        List<String> tags,
        int likeCount,
        int commentCount,
        int viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
