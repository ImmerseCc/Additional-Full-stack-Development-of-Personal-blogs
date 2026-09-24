package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCode;
import com.example.blog.common.PageParams;
import com.example.blog.common.TimeFormats;
import com.example.blog.model.Article;
import com.example.blog.model.ArticleCreateRequest;
import com.example.blog.model.ArticleDetailVO;
import com.example.blog.model.ArticleSummaryVO;
import com.example.blog.model.ArticleUpdateRequest;
import com.example.blog.model.PageVO;
import com.example.blog.repository.ArticleRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 文章业务层：参数语义校验 + 读写编排，SQL 一律下沉到 repository。
 *
 * <p>写方法（创建 / 更新 / 删除）带 {@code @Transactional}：文章行与标签关联要么一起生效、要么一起回滚；
 * 标签的"不存在则自动创建"由 {@link TagService} 在同一个事务内完成。
 */
@Service
public class ArticleService {

    private static final int SUMMARY_MAX_LENGTH = 120;
    private static final String TAG_MODE_AND = "and";
    private static final String TAG_MODE_OR = "or";
    private static final String STATUS_ALL = "ALL";

    private final ArticleRepository articleRepository;
    private final TagService tagService;

    public ArticleService(ArticleRepository articleRepository, TagService tagService) {
        this.articleRepository = articleRepository;
        this.tagService = tagService;
    }

    /**
     * 文章列表（契约 §四 · 2）：分页 + 标题关键词 + 多标签（and / or）+ 状态过滤。
     *
     * <p>取值为空时按契约取默认值；范围或取值非法一律抛 40002。
     */
    public PageVO<ArticleSummaryVO> listArticles(Integer pageParam, Integer sizeParam, String keywordParam,
                                                 String tagsParam, String tagModeParam, String statusParam) {
        PageParams params = PageParams.of(pageParam, sizeParam);
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
            return PageVO.of(List.of(), params.page(), params.size(), 0);
        }
        List<ArticleSummaryVO> items = articleRepository.findSummaries(
                keyword, tagNames, matchAllTags, statusFilter, params.size(), params.offset());
        return PageVO.of(items, params.page(), params.size(), total);
    }

    /** 文章是否存在，不存在抛 40004（评论 / 点赞接口共用）。 */
    public void requireArticleExists(long id) {
        if (!articleRepository.existsById(id)) {
            throw new BizException(ErrorCode.NOT_FOUND, "文章不存在：id=" + id);
        }
    }

    /** 文章详情（契约 §四 · 3）：含正文；prev / next 属阶段 8，阶段 2 恒为 null。 */
    public ArticleDetailVO getArticleDetail(long id) {
        ArticleSummaryVO summary = articleRepository.findSummaryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "文章不存在：id=" + id));
        String content = articleRepository.findContentById(id).orElse("");
        return ArticleDetailVO.of(summary, content, null, null);
    }

    /** 创建文章（契约 §四 · 4）：标签不存在则自动创建，返回列表项（HTTP 201 由 controller 决定）。 */
    @Transactional
    public ArticleSummaryVO createArticle(ArticleCreateRequest request) {
        String now = TimeFormats.now();
        Article article = new Article();
        article.setTitle(request.getTitle().trim());
        article.setContent(request.getContent());
        article.setCoverUrl(blankToNull(request.getCoverUrl()));
        article.setStatus(resolveStatus(request.getStatus()));
        article.setSummary(resolveSummary(request.getSummary(), request.getContent()));
        article.setCreatedAt(TimeFormats.parse(now));
        article.setUpdatedAt(TimeFormats.parse(now));

        long id = articleRepository.insert(article);
        articleRepository.replaceTags(id, tagService.resolveTagIds(request.getTags()));
        return requireSummary(id);
    }

    /** 更新文章（契约 §四 · 5，PUT 全量）：tags 省略或传空数组都表示清空标签。 */
    @Transactional
    public ArticleSummaryVO updateArticle(long id, ArticleUpdateRequest request) {
        Article article = new Article();
        article.setTitle(request.getTitle().trim());
        article.setContent(request.getContent());
        article.setCoverUrl(blankToNull(request.getCoverUrl()));
        article.setStatus(resolveStatus(request.getStatus()));
        article.setSummary(resolveSummary(request.getSummary(), request.getContent()));
        article.setUpdatedAt(TimeFormats.parse(TimeFormats.now()));

        int updated = articleRepository.update(id, article);
        if (updated == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "文章不存在：id=" + id);
        }
        articleRepository.replaceTags(id, tagService.resolveTagIds(request.getTags()));
        return requireSummary(id);
    }

    /** 删除文章（契约 §四 · 6）：评论、点赞、标签关联由外键 ON DELETE CASCADE 清理。 */
    @Transactional
    public void deleteArticle(long id) {
        int deleted = articleRepository.deleteById(id);
        if (deleted == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "文章不存在：id=" + id);
        }
    }

    /** 写操作后回读列表项；刚写入的数据理论上必然存在，取不到说明数据异常（50000）。 */
    private ArticleSummaryVO requireSummary(long id) {
        return articleRepository.findSummaryById(id)
                .orElseThrow(() -> new BizException(ErrorCode.SERVER_ERROR, "文章写入后读回失败：id=" + id));
    }

    /** status 省略时按契约默认 PUBLISHED；非法取值由 DTO 的 @Pattern 在校验阶段拦下。 */
    private static String resolveStatus(String status) {
        return (status == null || status.isBlank())
                ? Article.STATUS_PUBLISHED
                : status.trim().toUpperCase(Locale.ROOT);
    }

    /** 摘要留空时截取正文前 120 个字符（按码点截，避免切断 emoji 之类的代理对）。 */
    private static String resolveSummary(String summary, String content) {
        if (summary != null && !summary.isBlank()) {
            return summary.trim();
        }
        String text = (content == null) ? "" : content.trim();
        if (text.codePointCount(0, text.length()) <= SUMMARY_MAX_LENGTH) {
            return text;
        }
        return text.substring(0, text.offsetByCodePoints(0, SUMMARY_MAX_LENGTH));
    }

    /** 空白字符串归一为 null（可选字段存 null 而不是空串）。 */
    private static String blankToNull(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
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
