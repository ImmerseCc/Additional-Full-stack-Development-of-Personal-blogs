package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCode;
import com.example.blog.common.TimeFormats;
import com.example.blog.model.LikeRecord;
import com.example.blog.model.LikeStateVO;
import com.example.blog.model.VisitorIdRequest;
import com.example.blog.repository.LikeRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 点赞业务层（契约 §四 · 13–15）：查询状态、点赞、取消点赞，两个写操作都幂等。
 *
 * <p>去重依赖 {@code like_record} 上的 {@code UNIQUE (article_id, visitor_id)}；计数是查询时的实时 COUNT，
 * 因此重复点赞不会把数字放大。
 */
@Service
public class LikeService {

    private static final String VISITOR_ID_MESSAGE = "visitorId 需为 8-64 位字母、数字、下划线或连字符";

    private final LikeRepository likeRepository;
    private final ArticleService articleService;

    public LikeService(LikeRepository likeRepository, ArticleService articleService) {
        this.likeRepository = likeRepository;
        this.articleService = articleService;
    }

    /** 查询点赞状态与总数（前端初始化点赞按钮用）。 */
    public LikeStateVO getLikeState(long articleId, String visitorId) {
        articleService.requireArticleExists(articleId);
        validateVisitorId(visitorId);
        return currentState(articleId, visitorId);
    }

    /** 点赞（幂等：已点赞再次调用仍返回 {@code liked = true}，总数不变）。 */
    @Transactional
    public LikeStateVO like(long articleId, String visitorId) {
        articleService.requireArticleExists(articleId);
        validateVisitorId(visitorId);
        if (likeRepository.findByArticleIdAndVisitorId(articleId, visitorId).isEmpty()) {
            LikeRecord record = new LikeRecord();
            record.setArticleId(articleId);
            record.setVisitorId(visitorId);
            record.setCreatedAt(TimeFormats.parse(TimeFormats.now()));
            likeRepository.insert(record);
        }
        return currentState(articleId, visitorId);
    }

    /** 取消点赞（幂等：未点赞时调用仍返回 {@code liked = false}）。 */
    @Transactional
    public LikeStateVO unlike(long articleId, String visitorId) {
        articleService.requireArticleExists(articleId);
        validateVisitorId(visitorId);
        likeRepository.deleteByArticleIdAndVisitorId(articleId, visitorId);
        return currentState(articleId, visitorId);
    }

    private LikeStateVO currentState(long articleId, String visitorId) {
        boolean liked = likeRepository.findByArticleIdAndVisitorId(articleId, visitorId).isPresent();
        return new LikeStateVO(articleId, liked, likeRepository.countByArticleId(articleId));
    }

    /**
     * visitorId 必填且需符合格式（8–64 位字母 / 数字 / 下划线 / 连字符），否则 40001。
     *
     * <p>点赞接口的查询参数无法用 Bean Validation 校验（需要 {@code @Validated} + Spring 6.1 的
     * {@code HandlerMethodValidationException} 处理），因此在 service 里显式校验，保证错误码与契约一致。
     */
    private static void validateVisitorId(String visitorId) {
        if (visitorId == null || !visitorId.matches(VisitorIdRequest.VISITOR_ID_PATTERN)) {
            throw new BizException(ErrorCode.PARAM_INVALID, ErrorCode.PARAM_INVALID.getMessage(),
                    Map.of("fields", Map.of("visitorId", VISITOR_ID_MESSAGE)));
        }
    }
}
