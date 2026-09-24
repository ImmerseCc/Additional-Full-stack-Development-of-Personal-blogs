package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.TagVO;
import com.example.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签接口（契约 §四 · 7）。
 *
 * <p>标签管理（POST / PUT / DELETE）属阶段 8 可选项，阶段 2 不实现。
 */
@Tag(name = "标签", description = "标签列表（契约 §四 · 7）")
@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @Operation(summary = "标签列表", description = "返回所有标签及各自的文章数，按文章数倒序；供前端标签过滤器使用。")
    @GetMapping
    public ApiResponse<List<TagVO>> list() {
        return ApiResponse.ok(tagService.listTags());
    }
}
