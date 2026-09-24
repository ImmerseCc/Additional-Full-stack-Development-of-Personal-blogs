package com.example.blog.model;

/**
 * 标签响应体（契约 §二 · 2.1）：{@code { id, name, articleCount }}。
 *
 * <p>{@code articleCount} 为该标签关联的文章数，前端"标签过滤器"用它展示数量。
 */
public record TagVO(Long id, String name, int articleCount) {
}
