package com.example.blog.model;

/**
 * 阅读数响应体（契约 §四 · 18）：{@code { viewCount }}，为自增后的当前值。
 */
public record ViewCountVO(int viewCount) {
}
