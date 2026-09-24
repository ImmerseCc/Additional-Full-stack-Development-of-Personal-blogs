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
import org.springframework.stereotype.Service;

/**
 * 标签业务层：列表查询 + 保存文章时的标签解析与自动创建（契约 §四 · 4「不存在的标签自动创建」）。
 *
 * <p>标签增删改（管理接口）属阶段 8 可选项，阶段 2 不实现。
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
