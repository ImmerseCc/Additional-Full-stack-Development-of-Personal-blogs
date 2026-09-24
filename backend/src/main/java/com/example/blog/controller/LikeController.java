package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.LikeStateVO;
import com.example.blog.model.VisitorIdRequest;
import com.example.blog.service.LikeService;
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
@RestController
@RequestMapping("/api/articles/{id}/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /** 查询点赞状态与总数；visitorId 由 query 传入（缺失或格式不符返回 40001）。 */
    @GetMapping
    public ApiResponse<LikeStateVO> state(@PathVariable long id,
                                          @RequestParam(required = false) String visitorId) {
        return ApiResponse.ok(likeService.getLikeState(id, visitorId));
    }

    /** 点赞（幂等：重复调用不重复计数）。 */
    @PostMapping
    public ApiResponse<LikeStateVO> like(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        return ApiResponse.ok(likeService.like(id, request.getVisitorId()));
    }

    /** 取消点赞（幂等：未点赞时调用也返回成功，liked 为 false）。 */
    @DeleteMapping
    public ApiResponse<LikeStateVO> unlike(@PathVariable long id, @Valid @RequestBody VisitorIdRequest request) {
        return ApiResponse.ok(likeService.unlike(id, request.getVisitorId()));
    }
}
