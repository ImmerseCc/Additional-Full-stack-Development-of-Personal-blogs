package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.LikeStateVO;
import com.example.blog.model.VisitorIdRequest;
import com.example.blog.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 点赞接口（契约 §四 · 13–15）：查询状态、点赞、取消点赞；点赞与取消都幂等。
 */
@Tag(name = "点赞", description = "点赞状态 / 点赞 / 取消点赞，两端都幂等（契约 §四 · 13–15）")
@RestController
@RequestMapping("/api/articles/{id}/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Operation(summary = "查询点赞状态", description = "返回指定访客是否已点赞，以及该文章的总点赞数。")
    @GetMapping
    public ApiResponse<LikeStateVO> state(
            @PathVariable long id,
            @Parameter(description = "访客标识，8-64 位字母 / 数字 / 下划线 / 连字符；缺失或格式不符返回 40001")
            @RequestParam(required = false) String visitorId) {
        return ApiResponse.ok(likeService.getLikeState(id, visitorId));
    }

    @Operation(summary = "点赞（幂等）", description = "已点赞再次调用仍返回 liked=true，点赞数不变。")
    @PostMapping
    public ApiResponse<LikeStateVO> like(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        return ApiResponse.ok(likeService.like(id, request.getVisitorId()));
    }

    @Operation(summary = "取消点赞（幂等）", description = "未点赞时调用也返回成功，liked=false。")
    @DeleteMapping
    public ApiResponse<LikeStateVO> unlike(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        return ApiResponse.ok(likeService.unlike(id, request.getVisitorId()));
    }
}
