package com.example.blog.common;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.tomcat.util.http.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * 全局异常处理：把各类异常统一转换为契约规定的 {@code {code, message, data}} 响应。
 *
 * <p>覆盖范围：
 * <ul>
 *   <li>{@link BizException} —— service 层主动抛出的业务失败</li>
 *   <li>Bean Validation 失败 —— 40001，data.fields 给出字段级原因</li>
 *   <li>JSON / 参数 / 查询串编码格式错误 —— 40002</li>
 *   <li>接口路径不存在 —— 40004（避免被兜底处理成 50000）</li>
 *   <li>请求方法不支持 —— 40002（HTTP 400）</li>
 *   <li>数据库异常 —— 50001</li>
 *   <li>其他未预期异常 —— 50000</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Object>> handleBizException(BizException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.debug("业务异常：code={}, message={}", errorCode.getCode(), ex.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode, ex.getMessage(), ex.getData()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
        }
        log.debug("参数校验失败：{}", fields);
        return ResponseEntity.status(ErrorCode.PARAM_INVALID.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_INVALID, ErrorCode.PARAM_INVALID.getMessage(),
                        Map.of("fields", fields)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnreadableBody(HttpMessageNotReadableException ex) {
        log.debug("请求体解析失败：{}", ex.getMessage());
        return ResponseEntity.status(ErrorCode.PARAM_FORMAT_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_FORMAT_ERROR, "请求体格式错误，需为合法 JSON"));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.debug("参数类型不匹配：name={}, value={}", ex.getName(), ex.getValue());
        return ResponseEntity.status(ErrorCode.PARAM_FORMAT_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_FORMAT_ERROR, "参数格式错误：" + ex.getName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingParameter(MissingServletRequestParameterException ex) {
        log.debug("缺少必要参数：{}", ex.getParameterName());
        return ResponseEntity.status(ErrorCode.PARAM_FORMAT_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_FORMAT_ERROR, "缺少必要参数：" + ex.getParameterName()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoResourceFound(NoResourceFoundException ex) {
        log.debug("接口不存在：{}", ex.getResourcePath());
        return ResponseEntity.status(ErrorCode.NOT_FOUND.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.NOT_FOUND, "接口不存在：/" + ex.getResourcePath()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.debug("请求方法不支持：{}", ex.getMethod());
        return ResponseEntity.status(ErrorCode.PARAM_FORMAT_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_FORMAT_ERROR, "请求方法不支持：" + ex.getMethod()));
    }

    /**
     * 查询串编码非法：Tomcat 按 UTF-8 解码百分号编码失败时抛出。
     *
     * <p>典型场景：客户端把中文参数按 GBK 等其它编码做了百分号编码（部分 Windows 终端里的 curl 会这样）。
     * 这属于客户端参数问题，返回 40002，而不是被兜底成 50000。本项目使用内嵌 Tomcat，故直接引用其异常类型。
     */
    @ExceptionHandler(InvalidParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidParameter(InvalidParameterException ex) {
        log.debug("查询参数解码失败：{}", ex.getMessage());
        return ResponseEntity.status(ErrorCode.PARAM_FORMAT_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.PARAM_FORMAT_ERROR, "查询参数编码非法，请使用 UTF-8 百分号编码"));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataAccessException(DataAccessException ex) {
        log.error("数据库操作失败", ex);
        return ResponseEntity.status(ErrorCode.DB_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.DB_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUnexpectedException(Exception ex) {
        log.error("服务端未预期异常", ex);
        return ResponseEntity.status(ErrorCode.SERVER_ERROR.getHttpStatus())
                .body(ApiResponse.fail(ErrorCode.SERVER_ERROR));
    }
}
