package com.example.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 只带访客标识的请求体：点赞 / 取消点赞、删除评论都用它（契约 §四 · 12、13–15）。
 *
 * <p>{@code visitorId} 由前端生成（{@code crypto.randomUUID()}，36 位）并存放于 localStorage，
 * 作为本项目的轻量"归属标识"，替代登录鉴权。
 */
public class VisitorIdRequest {

    /** 访客标识格式：8–64 位字母、数字、下划线或连字符（契约 §四 · 9）。 */
    public static final String VISITOR_ID_PATTERN = "^[A-Za-z0-9_-]{8,64}$";

    @NotBlank(message = "visitorId 不能为空")
    @Pattern(regexp = VISITOR_ID_PATTERN, message = "visitorId 需为 8-64 位字母、数字、下划线或连字符")
    private String visitorId;

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }
}
