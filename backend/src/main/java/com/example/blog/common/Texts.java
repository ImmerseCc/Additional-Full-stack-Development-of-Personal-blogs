package com.example.blog.common;

/**
 * 文本处理小工具（阶段 9 批 2，审计 A 静态层 9-2）。
 *
 * <p>原先 {@code ArticleService} 与 {@code CommentService} 各写了一份 {@code blankToNull}，
 * 这里收敛为唯一定义。
 */
public final class Texts {

    private Texts() {
    }

    /** 空白字符串归一为 null（可选字段存 null 而不是空串）；非空白则去掉首尾空白。 */
    public static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }
}
