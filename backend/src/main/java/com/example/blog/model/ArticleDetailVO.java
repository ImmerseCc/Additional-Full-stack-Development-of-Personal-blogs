package com.example.blog.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情响应体（契约 §二 · 2.3）：{@link ArticleSummaryVO} 的全部字段，外加正文与上一篇 / 下一篇。
 *
 * <p>{@code prev} / {@code next} 自阶段 8 起实装（决策 BT）：只包含已发布文章、按 created_at / id 排序；
 * 首篇的 prev 与尾篇的 next 为 {@code null}。
 */
public record ArticleDetailVO(
        Long id,
        String title,
        String summary,
        String coverUrl,
        String status,
        List<String> tags,
        int likeCount,
        int commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        String content,
        AdjacentVO prev,
        AdjacentVO next) {

    /** 由列表项 + 正文组装详情（保持契约要求的"扁平" JSON 结构）。 */
    public static ArticleDetailVO of(ArticleSummaryVO summary, String content, AdjacentVO prev, AdjacentVO next) {
        return new ArticleDetailVO(
                summary.id(),
                summary.title(),
                summary.summary(),
                summary.coverUrl(),
                summary.status(),
                summary.tags(),
                summary.likeCount(),
                summary.commentCount(),
                summary.createdAt(),
                summary.updatedAt(),
                content,
                prev,
                next);
    }
}
