package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.ArticleCreateRequest;
import com.example.blog.model.ArticleDetailVO;
import com.example.blog.model.ArticleSummaryVO;
import com.example.blog.model.ArticleUpdateRequest;
import com.example.blog.model.PageVO;
import com.example.blog.service.ArticleService;
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
 * 文章接口（契约 §四 · 2–6）：列表 / 详情 / 创建 / 更新 / 删除。
 *
 * <p>springdoc 注解在阶段 2 批 4 统一补。
 */
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /** 文章列表：page / size / keyword / tags / tagMode / status，取值与默认值见契约 §四 · 2。 */
    @GetMapping
    public ApiResponse<PageVO<ArticleSummaryVO>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String tags,
            @RequestParam(required = false) String tagMode,
            @RequestParam(required = false) String status) {
        return ApiResponse.ok(articleService.listArticles(page, size, keyword, tags, tagMode, status));
    }

    /** 文章详情，含 Markdown 正文。 */
    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailVO> detail(@PathVariable long id) {
        return ApiResponse.ok(articleService.getArticleDetail(id));
    }

    /** 创建文章（契约 §四 · 4）：成功返回 HTTP 201。 */
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleSummaryVO>> create(
            @Valid @RequestBody ArticleCreateRequest request) {
        ArticleSummaryVO created = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    /** 更新文章（契约 §四 · 5，PUT 全量更新）。 */
    @PutMapping("/{id}")
    public ApiResponse<ArticleSummaryVO> update(@PathVariable long id,
                                                @Valid @RequestBody ArticleUpdateRequest request) {
        return ApiResponse.ok(articleService.updateArticle(id, request));
    }

    /** 删除文章（契约 §四 · 6）：级联删除该文章的评论、点赞与标签关联。 */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        articleService.deleteArticle(id);
        return ApiResponse.ok();
    }
}
