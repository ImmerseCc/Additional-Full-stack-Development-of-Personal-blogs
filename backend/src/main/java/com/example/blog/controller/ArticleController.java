package com.example.blog.controller;

import com.example.blog.common.ApiResponse;
import com.example.blog.model.AdjacentPairVO;
import com.example.blog.model.ArticleCreateRequest;
import com.example.blog.model.ArticleDetailVO;
import com.example.blog.model.ArticleSummaryVO;
import com.example.blog.model.ArticleUpdateRequest;
import com.example.blog.model.PageVO;
import com.example.blog.model.ViewCountVO;
import com.example.blog.service.ArticleService;
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
 * 文章接口（契约 §四 · 2–6）：列表 / 详情 / 创建 / 更新 / 删除。
 */
@Tag(name = "文章", description = "文章的列表 / 详情 / 创建 / 更新 / 删除（契约 §四 · 2–6）")
@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Operation(summary = "文章列表", description = "分页 + 标题关键词 + 多标签（tagMode=and/or）+ 状态过滤；默认只返回已发布文章。")
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

    @Operation(summary = "文章详情", description = "含 Markdown 正文与 prev / next（只含已发布文章，按 created_at、id 排序）。")
    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailVO> detail(@PathVariable long id) {
        return ApiResponse.ok(articleService.getArticleDetail(id));
    }

    @Operation(summary = "上一篇 / 下一篇", description = "只取已发布文章；首篇 prev、尾篇 next 为 null；文章不存在返回 40004。")
    @GetMapping("/{id}/adjacent")
    public ApiResponse<AdjacentPairVO> adjacent(@PathVariable long id) {
        return ApiResponse.ok(articleService.getAdjacentPair(id));
    }

    @Operation(summary = "阅读数 +1", description = "每次调用自增 1（不按访客去重，演示级）；文章不存在返回 40004。")
    @PostMapping("/{id}/views")
    public ApiResponse<ViewCountVO> addView(@PathVariable long id) {
        return ApiResponse.ok(articleService.addView(id));
    }

    @Operation(summary = "创建文章", description = "成功返回 HTTP 201；标签不存在会自动创建；summary 留空时取正文前 120 字。")
    @PostMapping
    public ResponseEntity<ApiResponse<ArticleSummaryVO>> create(
            @Valid @RequestBody ArticleCreateRequest request) {
        ArticleSummaryVO created = articleService.createArticle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(created));
    }

    @Operation(summary = "更新文章（全量）", description = "PUT 全量语义：title / content / status 必填；tags 省略或传空数组都表示清空标签。")
    @PutMapping("/{id}")
    public ApiResponse<ArticleSummaryVO> update(@PathVariable long id,
                                                @Valid @RequestBody ArticleUpdateRequest request) {
        return ApiResponse.ok(articleService.updateArticle(id, request));
    }

    @Operation(summary = "删除文章", description = "级联删除该文章的评论、点赞与标签关联；标签本身保留。")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable long id) {
        articleService.deleteArticle(id);
        return ApiResponse.ok();
    }
}
