package com.example.blog.service;

import com.example.blog.model.TagVO;
import com.example.blog.repository.TagRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 标签业务层（阶段 2 批 2a：只含列表；标签增删改属阶段 8 可选项）。
 */
@Service
public class TagService {

    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /** 标签列表，按关联文章数倒序（前端模块四的标签过滤器数据源）。 */
    public List<TagVO> listTags() {
        return tagRepository.findAllWithArticleCount();
    }
}
