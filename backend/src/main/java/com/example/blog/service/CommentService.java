package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCode;
import com.example.blog.common.PageParams;
import com.example.blog.common.Texts;
import com.example.blog.common.TimeFormats;
import com.example.blog.model.Comment;
import com.example.blog.model.CommentCreateRequest;
import com.example.blog.model.CommentUpdateRequest;
import com.example.blog.model.CommentVO;
import com.example.blog.model.PageVO;
import com.example.blog.repository.CommentRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 评论业务层（契约 §四 · 8–12）：列表分页、发表、单条查询、按访客标识修改 / 删除。
 *
 * <p>本项目不做登录，修改与删除采用"访客标识（visitorId）匹配"作为最轻量的归属校验——这是**演示级**方案，
 * 公网部署必须替换成真正的鉴权（见契约 §四 · 12 的说明）。
 */
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleService articleService;

    public CommentService(CommentRepository commentRepository, ArticleService articleService) {
        this.commentRepository = commentRepository;
        this.articleService = articleService;
    }

    /** 某文章的评论列表（分页，创建时间倒序）；文章不存在返回 40004。 */
    public PageVO<CommentVO> listComments(long articleId, Integer pageParam, Integer sizeParam) {
        articleService.requireArticleExists(articleId);
        PageParams params = PageParams.of(pageParam, sizeParam);
        long total = commentRepository.countByArticleId(articleId);
        if (total == 0) {
            return PageVO.of(List.of(), params.page(), params.size(), 0);
        }
        List<CommentVO> items = commentRepository.findPageByArticleId(articleId, params.size(), params.offset());
        return PageVO.of(items, params.page(), params.size(), total);
    }

    /** 发表评论（允许匿名；邮箱只存不返回）。 */
    @Transactional
    public CommentVO createComment(long articleId, CommentCreateRequest request) {
        articleService.requireArticleExists(articleId);
        Comment comment = new Comment();
        comment.setArticleId(articleId);
        comment.setAuthorName(request.getAuthorName().trim());
        comment.setAuthorEmail(Texts.blankToNull(request.getAuthorEmail()));
        comment.setContent(request.getContent());
        comment.setVisitorId(request.getVisitorId());
        comment.setCreatedAt(TimeFormats.parse(TimeFormats.now()));

        long id = commentRepository.insert(comment);
        return new CommentVO(id, articleId, comment.getAuthorName(), comment.getContent(), comment.getCreatedAt());
    }

    /** 单条评论（契约 §四 · 10）；评论不存在返回 40004。 */
    public CommentVO getComment(long id) {
        return commentRepository.findVoById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "评论不存在：id=" + id));
    }

    /**
     * 修改评论内容（契约 §四 · 11）：只改 {@code content}，昵称 / 邮箱 / 创建时间均不变；
     * 评论不存在、或 visitorId 与创建时不一致，都返回 40004（与删除同一套"归属校验"语义）。
     */
    @Transactional
    public CommentVO updateComment(long id, CommentUpdateRequest request) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, notFoundMessage(id)));
        if (!comment.getVisitorId().equals(request.getVisitorId())) {
            throw new BizException(ErrorCode.NOT_FOUND, notFoundMessage(id));
        }
        commentRepository.updateContent(id, request.getContent());
        return new CommentVO(id, comment.getArticleId(), comment.getAuthorName(),
                request.getContent(), comment.getCreatedAt());
    }

    /**
     * 删除评论（契约 §四 · 12）：评论不存在、或 visitorId 与创建时不一致，都返回 40004。
     *
     * <p>两种情况刻意用同一个错误码与文案，避免向调用方泄露"评论是否存在"。
     */
    @Transactional
    public void deleteComment(long id, String visitorId) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, notFoundMessage(id)));
        if (!comment.getVisitorId().equals(visitorId)) {
            throw new BizException(ErrorCode.NOT_FOUND, notFoundMessage(id));
        }
        commentRepository.deleteById(id);
    }

    private static String notFoundMessage(long id) {
        return "评论不存在或访客标识不匹配：id=" + id;
    }
}
