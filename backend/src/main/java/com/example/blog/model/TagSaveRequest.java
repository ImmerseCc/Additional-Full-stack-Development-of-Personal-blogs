package com.example.blog.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 标签创建 / 改名请求体（契约 §四 · 17）。
 */
public class TagSaveRequest {

    @NotBlank(message = "标签名不能为空")
    @Size(max = 20, message = "标签名长度需在 1-20 之间")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
