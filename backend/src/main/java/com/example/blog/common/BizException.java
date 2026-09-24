package com.example.blog.common;

/**
 * 业务异常：service 层用它表达"可预期的业务失败"（如文章不存在、标签名重复）。
 *
 * <p>由 {@link GlobalExceptionHandler} 统一转换成 {@code {code, message, data}} 响应；
 * 非预期异常不需要用它包装，交给全局兜底处理即可。
 */
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    /** 附加数据，可为 null；40001 时用于承载字段级错误信息。 */
    private final Object data;

    public BizException(ErrorCode errorCode) {
        this(errorCode, errorCode.getMessage(), null);
    }

    public BizException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public BizException(ErrorCode errorCode, String message, Object data) {
        super(message);
        this.errorCode = errorCode;
        this.data = data;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Object getData() {
        return data;
    }
}
