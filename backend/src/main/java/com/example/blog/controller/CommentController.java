package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.CommentCreateRequest;
import com.example.blog.model.CommentUpdateRequest;
import com.example.blog.model.CommentVO;
import com.example.blog.model.PageVO;
import com.example.blog.model.VisitorIdRequest;
import com.example.blog.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论接口（契约 §四 · 8–12）。
 *
 * <p>列表与发表挂在文章下（{@code /api/articles/{id}/comments}），单条查询 / 修改 / 删除挂在
 * {@code /api/comments/{id}}；修改与删除都要求 visitorId 与创建时一致。
 */
@Tag(name = "评论", description = "评论的列表 / 发表 / 单条查询 / 修改 / 删除（契约 §四 · 8–12）")
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @Operation(summary = "评论列表", description = "按创建时间倒序分页；文章不存在返回 40004。")
    @GetMapping("/articles/{id}/comments")
    public ApiResponse<PageVO<CommentVO>> list(@PathVariable long id,
                                               @RequestParam(required = false) Integer page,
                                               @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(commentService.listComments(id, page, size));
    }

    @Operation(summary = "发表评论", description = "允许匿名（仅需 authorName 与 content）；authorEmail 只存不返回；成功返回 HTTP 201。")
    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<ApiResponse<CommentVO>> create(@PathVariable long id,
                                                         @Valid @RequestBody CommentCreateRequest request) {
        CommentVO created = commentService.createComment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @Operation(summary = "单条评论", description = "按 id 查询单条评论；不存在返回 40004。")
    @GetMapping("/comments/{id}")
    public ApiResponse<CommentVO> get(@PathVariable long id) {
        return ApiResponse.ok(commentService.getComment(id));
    }

    @Operation(summary = "修改评论", description = "仅修改 content；visitorId 须与创建时一致，不匹配或评论不存在都返回 40004。")
    @PutMapping("/comments/{id}")
    public ApiResponse<CommentVO> update(@PathVariable long id, @Valid @RequestBody CommentUpdateRequest request) {
        return ApiResponse.ok(commentService.updateComment(id, request));
    }

    @Operation(summary = "删除评论", description = "请求体需带创建时使用的 visitorId；评论不存在或 visitorId 不匹配都返回 40004。")
    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> delete(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        commentService.deleteComment(id, request.getVisitorId());
        return ApiResponse.ok();
    }
}
