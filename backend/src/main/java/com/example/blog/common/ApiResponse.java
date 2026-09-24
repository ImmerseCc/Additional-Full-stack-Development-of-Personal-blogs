package com.example.blog.common;

/**
 * 统一响应体 {@code { "code": 0, "message": "ok", "data": ... }}（契约 §一）。
 *
 * <p>空数据时 {@code data} 输出 {@code null}，不省略字段。
 *
 * @param <T> 业务数据类型
 */
public record ApiResponse<T>(int code, String message, T data) {

    /** 成功响应（data 可为业务对象、集合或 null）。 */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data);
    }

    /** 成功但无数据（如删除接口，data 为 null）。 */
    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), null);
    }

    /** 失败响应，使用错误码的默认文案。 */
    public static ApiResponse<Object> fail(ErrorCode errorCode) {
        return fail(errorCode, errorCode.getMessage(), null);
    }

    /** 失败响应，自定义文案。 */
    public static ApiResponse<Object> fail(ErrorCode errorCode, String message) {
        return fail(errorCode, message, null);
    }

    /** 失败响应，附带 data（如 40001 的 {@code {"fields": {...}}}）。 */
    public static ApiResponse<Object> fail(ErrorCode errorCode, String message, Object data) {
        return new ApiResponse<>(errorCode.getCode(), message, data);
    }
}
