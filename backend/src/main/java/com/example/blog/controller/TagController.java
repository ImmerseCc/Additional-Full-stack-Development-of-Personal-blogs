package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.TagVO;
import com.example.blog.service.TagService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签接口（契约 §四 · 7）。
 *
 * <p>标签管理（POST / PUT / DELETE）属阶段 8 可选项，阶段 2 不实现。
 */
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    /** 标签列表，含每个标签的文章数，按文章数倒序。 */
    @GetMapping
    public ApiResponse<List<TagVO>> list() {
        return ApiResponse.ok(tagService.listTags());
    }
}
