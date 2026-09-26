package com.example.blog.service;

import com.example.blog.common.BizException;
import com.example.blog.common.ErrorCode;
import com.example.blog.common.TimeFormats;
import com.example.blog.model.Tag;
import com.example.blog.model.TagVO;
import com.example.blog.repository.TagRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 标签业务层（契约 §四 · 4、7、17）：列表查询、保存文章时的标签解析与自动创建、标签管理（新建 / 改名 / 删除）。
 *
 * <p>重名一律返回 40009（资源冲突）；新建 / 改名 / 删除都带事务边界。
 */
@Service
public class TagService {

    private static final int MAX_NAME_LENGTH = 20;

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /** 标签列表，按关联文章数倒序（前端模块四的标签过滤器数据源）。 */
    public List<TagVO> listTags() {
        return tagRepository.findAllWithArticleCount();
    }

    /**
     * 新建标签（契约 §四 · 17）：名称 1-20 字（DTO 已校验）；重名返回 40009。
     *
     * <p>并发下仍有极小概率撞上 {@code tag.name} 的 UNIQUE 约束，此时把数据库异常翻译成同一个 40009。
     */
    @Transactional
    public TagVO createTag(String name) {
        String trimmed = name.trim();
        if (tagRepository.findByName(trimmed).isPresent()) {
            throw new BizException(ErrorCode.CONFLICT, "标签名已存在：" + trimmed);
        }
        Tag tag = new Tag();
        tag.setName(trimmed);
        tag.setCreatedAt(TimeFormats.parse(TimeFormats.now()));
        try {
            long id = tagRepository.insert(tag);
            return new TagVO(id, trimmed, 0);
        } catch (DuplicateKeyException ex) {
            throw new BizException(ErrorCode.CONFLICT, "标签名已存在：" + trimmed);
        }
    }

    /** 标签改名（契约 §四 · 17）：标签不存在 40004；名称与他人重复 40009（改成自己原名不算冲突）。 */
    @Transactional
    public TagVO renameTag(long id, String name) {
        TagVO current = tagRepository.findVoById(id)
                .orElseThrow(() -> new BizException(ErrorCode.NOT_FOUND, "标签不存在：id=" + id));
        String trimmed = name.trim();
        tagRepository.findByName(trimmed)
                .filter(tag -> !tag.getId().equals(id))
                .ifPresent(tag -> {
                    throw new BizException(ErrorCode.CONFLICT, "标签名已存在：" + trimmed);
                });
        tagRepository.rename(id, trimmed);
        return new TagVO(id, trimmed, current.articleCount());
    }

    /** 删除标签（契约 §四 · 17）：article_tag 由外键级联解除；标签不存在 40004。 */
    @Transactional
    public void deleteTag(long id) {
        if (tagRepository.deleteById(id) == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "标签不存在：id=" + id);
        }
    }

    /**
     * 把标签名解析成标签 id：已存在则复用，不存在则新建；名称长度非法一律抛 40001（带字段级原因）。
     *
     * <p>调用方 {@link ArticleService} 的写方法带 {@code @Transactional}，所以"先查后建"的多步操作
     * 要么全部生效、要么全部回滚。
     *
     * @param tagNames 原始标签名，null / 空表示不挂标签
     */
    public List<Long> resolveTagIds(List<String> tagNames) {
        List<String> names = normalize(tagNames);
        if (names.isEmpty()) {
            return List.of();
        }
        Map<String, String> fields = new LinkedHashMap<>();
        for (String name : names) {
            if (name.length() > MAX_NAME_LENGTH) {
                fields.put("tags", "标签名长度需在 1-" + MAX_NAME_LENGTH + " 之间：" + name);
            }
        }
        if (!fields.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_INVALID, ErrorCode.PARAM_INVALID.getMessage(),
                    Map.of("fields", fields));
        }

        String now = TimeFormats.now();
        List<Long> tagIds = new ArrayList<>(names.size());
        for (String name : names) {
            tagIds.add(tagRepository.findByName(name).map(Tag::getId).orElseGet(() -> {
                Tag tag = new Tag();
                tag.setName(name);
                tag.setCreatedAt(TimeFormats.parse(now));
                return tagRepository.insert(tag);
            }));
        }
        return tagIds;
    }

    /** 去首尾空白、丢空项、去重（保持用户输入顺序）。 */
    private static List<String> normalize(List<String> tagNames) {
        if (tagNames == null) {
            return List.of();
        }
        return tagNames.stream()
                .filter(name -> name != null && !name.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
    }
}
