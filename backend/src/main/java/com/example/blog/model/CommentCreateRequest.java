package com.example.blog.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 发表评论请求体（契约 §四 · 9）。
 *
 * <p>本项目允许匿名评论：不要求邮箱，{@code authorEmail} 仅服务端保存、不回传前端。
 */
public class CommentCreateRequest {

    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度需在 1-30 之间")
    private String authorName;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过 100 字")
    private String authorEmail;

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容长度需在 1-1000 之间")
    private String content;

    @NotBlank(message = "visitorId 不能为空")
    @Pattern(regexp = VisitorIdRequest.VISITOR_ID_PATTERN,
            message = "visitorId 需为 8-64 位字母、数字、下划线或连字符")
    private String visitorId;

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorEmail() {
        return authorEmail;
    }

    public void setAuthorEmail(String authorEmail) {
        this.authorEmail = authorEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVisitorId() {
        return visitorId;
    }

    public void setVisitorId(String visitorId) {
        this.visitorId = visitorId;
    }
}
