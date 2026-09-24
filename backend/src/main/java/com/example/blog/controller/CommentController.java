package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.CommentCreateRequest;
import com.example.blog.model.CommentVO;
import com.example.blog.model.PageVO;
import com.example.blog.model.VisitorIdRequest;
import com.example.blog.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论接口（契约 §四 · 8、9、12）。
 *
 * <p>列表与发表挂在文章下（{@code /api/articles/{id}/comments}），删除挂在 {@code /api/comments/{id}}；
 * 单条评论的查询与修改属阶段 8 可选项，阶段 2 不实现。
 */
@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /** 某文章的评论列表（分页，创建时间倒序）。 */
    @GetMapping("/articles/{id}/comments")
    public ApiResponse<PageVO<CommentVO>> list(@PathVariable long id,
                                               @RequestParam(required = false) Integer page,
                                               @RequestParam(required = false) Integer size) {
        return ApiResponse.ok(commentService.listComments(id, page, size));
    }

    /** 发表评论（允许匿名），成功返回 HTTP 201。 */
    @PostMapping("/articles/{id}/comments")
    public ResponseEntity<ApiResponse<CommentVO>> create(@PathVariable long id,
                                                         @Valid @RequestBody CommentCreateRequest request) {
        CommentVO created = commentService.createComment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    /** 删除评论：请求体需带创建时使用的 visitorId（归属校验）。 */
    @DeleteMapping("/comments/{id}")
    public ApiResponse<Void> delete(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        commentService.deleteComment(id, request.getVisitorId());
        return ApiResponse.ok();
    }
}
