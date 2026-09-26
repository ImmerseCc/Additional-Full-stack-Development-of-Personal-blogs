package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.TagSaveRequest;
import com.example.blog.model.TagVO;
import com.example.blog.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 标签接口（契约 §四 · 7、17）：列表 + 标签管理（新建 / 改名 / 删除）。
 *
 * <p>管理接口不鉴权（演示级，公网部署需补鉴权）；重名冲突返回 40009。
 */
@Tag(name = "标签", description = "标签列表与标签管理（契约 §四 · 7、17）")
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

    @Operation(summary = "新建标签", description = "名称 1-20 字；重名返回 40009（资源冲突）；成功返回 HTTP 201。")
    @PostMapping
    public ResponseEntity<ApiResponse<TagVO>> create(@Valid @RequestBody TagSaveRequest request) {
        TagVO created = tagService.createTag(request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @Operation(summary = "标签改名", description = "名称 1-20 字；标签不存在 40004、重名 40009。")
    @PutMapping("/{id}")
    public ApiResponse<TagVO> rename(@PathVariable long id, @Valid @RequestBody TagSaveRequest request) {
        return ApiResponse.ok(tagService.renameTag(id, request.getName()));
    }

    @Operation(summary = "删除标签", description = "同时解除所有文章关联（article_tag 级联删除）；标签不存在 40004。")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        tagService.deleteTag(id);
        return ApiResponse.ok();
    }
}
