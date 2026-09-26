package com.example.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 修改评论请求体（契约 §四 · 11）。
 *
 * <p>只允许修改正文：{@code authorName} / {@code authorEmail} / {@code visitorId} / {@code createdAt}
 * 均保持不变；{@code visitorId} 须与创建时一致，用于最轻量的归属校验（演示级，口径同删除）。
 */
public class CommentUpdateRequest {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容长度需在 1-1000 之间")
    private String content;

    @NotBlank(message = "visitorId 不能为空")
    @Pattern(regexp = VisitorIdRequest.VISITOR_ID_PATTERN,
            message = "visitorId 需为 8-64 位字母、数字、下划线或连字符")
    private String visitorId;

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
