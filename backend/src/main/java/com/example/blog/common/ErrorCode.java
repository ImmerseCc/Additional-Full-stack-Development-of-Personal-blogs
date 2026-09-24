package com.example.blog.common;

import org.springframework.http.HttpStatus;

/**
 * 业务错误码（与 docs/api-contract.md v1.0「错误码表」逐条对应）。
 *
 * <p>约定：HTTP 状态码与业务 code 同时返回，前端先看 HTTP 是否 2xx，再看 code 是否为 0。
 */
public enum ErrorCode {

    /** 成功：HTTP 200 / 201 */
    SUCCESS(0, HttpStatus.OK, "ok"),

    /** 参数校验失败（data.fields 给出字段级原因） */
    PARAM_INVALID(40001, HttpStatus.BAD_REQUEST, "参数校验失败"),

    /** 参数格式错误（如 page=abc、非法 JSON） */
    PARAM_FORMAT_ERROR(40002, HttpStatus.BAD_REQUEST, "参数格式错误"),

    /** 资源不存在（文章 / 评论 ID 不存在） */
    NOT_FOUND(40004, HttpStatus.NOT_FOUND, "资源不存在"),

    /** 资源冲突（如标签名重复） */
    CONFLICT(40009, HttpStatus.CONFLICT, "资源冲突"),

    /** 服务端未预期异常 */
    SERVER_ERROR(50000, HttpStatus.INTERNAL_SERVER_ERROR, "服务端未预期异常"),

    /** 数据库操作失败 */
    DB_ERROR(50001, HttpStatus.INTERNAL_SERVER_ERROR, "数据库操作失败");

    private final int code;
    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(int code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
