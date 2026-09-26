package com.example.blog.model;

/**
 * 上一篇 / 下一篇的摘要信息（契约 §二 · 2.3、§四 · 16）：只有 id 与标题。
 */
public record AdjacentVO(Long id, String title) {
}
