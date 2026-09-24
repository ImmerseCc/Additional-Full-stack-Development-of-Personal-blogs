package com.example.blog.common;

/**
 * 分页参数的解析与语义校验（契约 §一「分页参数」：page 从 1 开始，size 默认 10、范围 1–20）。
 *
 * <p>文章列表与评论列表共用同一份规则，避免各自复制一遍校验逻辑（含错误文案）。
 */
public record PageParams(int page, int size) {

    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_SIZE = 10;
    public static final int MAX_SIZE = 20;

    /** 解析并校验分页参数；越界或非法一律抛 40002（参数格式错误）。 */
    public static PageParams of(Integer pageParam, Integer sizeParam) {
        int page = (pageParam == null) ? DEFAULT_PAGE : pageParam;
        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "page 必须从 1 开始");
        }
        int size = (sizeParam == null) ? DEFAULT_SIZE : sizeParam;
        if (size < 1 || size > MAX_SIZE) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "size 需在 1-" + MAX_SIZE + " 之间");
        }
        return new PageParams(page, size);
    }

    /** 转成 SQL 的 OFFSET。 */
    public int offset() {
        return (page - 1) * size;
    }
}
