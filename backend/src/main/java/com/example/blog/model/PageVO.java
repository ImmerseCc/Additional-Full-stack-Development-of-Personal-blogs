package com.example.blog.model;

import java.util.List;

/**
 * 分页响应体（契约 §一「分页响应」）：{@code { items, page, size, total, totalPages }}。
 *
 * @param <T> 列表项类型
 */
public record PageVO<T>(List<T> items, int page, int size, long total, int totalPages) {

    /**
     * 由查询结果构造分页对象，自动计算总页数。
     *
     * @param items 当前页数据（null 视为空列表）
     * @param page  当前页码（从 1 开始）
     * @param size  每页条数
     * @param total 记录总数
     */
    public static <T> PageVO<T> of(List<T> items, int page, int size, long total) {
        List<T> safeItems = (items == null) ? List.of() : items;
        int totalPages = (size > 0) ? (int) ((total + size - 1) / size) : 0;
        return new PageVO<>(safeItems, page, size, total, totalPages);
    }
}
