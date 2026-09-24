package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.ArticleDetailVO;
import com.example.blog.model.ArticleSummaryVO;
import com.example.blog.model.PageVO;
import com.example.blog.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章接口（契约 §四 · 2、3）。
 *
 * <p>阶段 2 批 2a 只实现**读路径**；创建 / 更新 / 删除在批 2b 补齐，springdoc 注解在批 4 统一补。
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
}
