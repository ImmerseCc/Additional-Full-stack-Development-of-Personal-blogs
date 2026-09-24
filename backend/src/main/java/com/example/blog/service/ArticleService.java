package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCode;
import com.example.blog.model.Article;
import com.example.blog.model.ArticleDetailVO;
import com.example.blog.model.ArticleSummaryVO;
import com.example.blog.model.PageVO;
import com.example.blog.repository.ArticleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;

/**
 * 文章业务层：参数语义校验 + 查询编排，SQL 一律下沉到 repository。
 *
 * <p>当前只含**读路径**（阶段 2 批 2a）；创建 / 更新 / 删除与标签维护在批 2b 补齐。
 */
@Service
public class ArticleService {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_SIZE = 20;
    private static final String TAG_MODE_AND = "and";
    private static final String TAG_MODE_OR = "or";
    private static final String STATUS_ALL = "ALL";

    private final ArticleRepository articleRepository;

    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    /**
     * 文章列表（契约 §四 · 2）：分页 + 标题关键词 + 多标签（and / or）+ 状态过滤。
     *
     * <p>取值为空时按契约取默认值；范围或取值非法一律抛 40002。
     */
    public PageVO<ArticleSummaryVO> listArticles(Integer pageParam, Integer sizeParam, String keywordParam,
                                                 String tagsParam, String tagModeParam, String statusParam) {
        int page = (pageParam == null) ? DEFAULT_PAGE : pageParam;
        if (page < 1) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "page 必须从 1 开始");
        }
        int size = (sizeParam == null) ? DEFAULT_SIZE : sizeParam;
        if (size < 1 || size > MAX_SIZE) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "size 需在 1-" + MAX_SIZE + " 之间");
        }
        String tagMode = (tagModeParam == null || tagModeParam.isBlank())
                ? TAG_MODE_AND
                : tagModeParam.trim().toLowerCase(Locale.ROOT);
        if (!TAG_MODE_AND.equals(tagMode) && !TAG_MODE_OR.equals(tagMode)) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "tagMode 只能是 and 或 or");
        }
        String status = (statusParam == null || statusParam.isBlank())
                ? Article.STATUS_PUBLISHED
                : statusParam.trim().toUpperCase(Locale.ROOT);
        if (!Article.STATUS_PUBLISHED.equals(status)
                && !Article.STATUS_DRAFT.equals(status)
                && !STATUS_ALL.equals(status)) {
            throw new BizException(ErrorCode.PARAM_FORMAT_ERROR, "status 只能是 PUBLISHED、DRAFT 或 ALL");
        }

        String keyword = (keywordParam == null || keywordParam.isBlank()) ? null : keywordParam.trim();
        List<String> tagNames = parseTagNames(tagsParam);
        boolean matchAllTags = TAG_MODE_AND.equals(tagMode);
        String statusFilter = STATUS_ALL.equals(status) ? null : status;

        long total = articleRepository.countSummaries(keyword, tagNames, matchAllTags, statusFilter);
        if (total == 0) {
            return PageVO.of(List.of(), page, size, 0);
        }
        List<ArticleSummaryVO> items = articleRepository.findSummaries(
                keyword, tagNames, matchAllTags, statusFilter, size, (page - 1) * size);
        return PageVO.of(items, page, size, total);
    }

    /** 文章详情（契约 §四 · 3）：含正文；prev / next 属阶段 8，阶段 2 恒为 null。 */
    public ArticleDetailVO getArticleDetail(long id) {
        ArticleSummaryVO summary = articleRepository.findSummaryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "文章不存在：id=" + id));
        String content = articleRepository.findContentById(id).orElse("");
        return ArticleDetailVO.of(summary, content, null, null);
    }

    /** 逗号分隔的标签名：去首尾空白、丢空项、去重，并保持用户输入顺序。 */
    private static List<String> parseTagNames(String tagsParam) {
        if (tagsParam == null || tagsParam.isBlank()) {
            return List.of();
        }
        return Arrays.stream(tagsParam.split(","))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .distinct()
                .toList();
    }
}
