package com.example.blog.model;

/**
 * 相邻文章响应体（契约 §四 · 16）：{@code GET /api/articles/{id}/adjacent} 的 data。
 *
 * <p>{@code prev} = 更早的一篇、{@code next} = 更晚的一篇（只含已发布文章，按 created_at、id 排序）；
 * 没有相邻文章时为 {@code null}。
 */
public record AdjacentPairVO(AdjacentVO prev, AdjacentVO next) {
}
