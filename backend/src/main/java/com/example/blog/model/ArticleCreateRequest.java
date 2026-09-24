package com.example.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 创建文章请求体（契约 §四 · 4）。
 *
 * <p>必填：{@code title}、{@code content}；其余可选。{@code status} 省略时由 service 取默认值
 * {@code PUBLISHED}，{@code summary} 留空时由 service 截取正文前 120 字。
 */
public class ArticleCreateRequest {

    @NotBlank(message = "标题不能为空")
    @Size(max = 100, message = "标题长度需在 1-100 之间")
    private String title;

    @Size(max = 200, message = "摘要长度不能超过 200 字")
    private String summary;

    @NotBlank(message = "内容不能为空")
    @Size(max = 50000, message = "内容长度需在 1-50000 之间")
    private String content;

    @Size(max = 200, message = "封面地址长度不能超过 200 字")
    private String coverUrl;

    @Pattern(regexp = "PUBLISHED|DRAFT", message = "状态只能是 PUBLISHED 或 DRAFT")
    private String status;

    @Size(max = 5, message = "标签最多 5 个")
    private List<String> tags;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
