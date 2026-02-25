package com.knowledge.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.core.entity.TagEntity;

import java.util.List;

/**
 * 标签服务接口
 * @author nuts_tian
 */
public interface TagService extends IService<TagEntity> {

    /**
     * 获取用户的所有标签
     */
    List<TagEntity> listByUser(Long userId);

    /**
     * 创建标签
     */
    TagEntity create(String name, Long userId);

    /**
     * 删除标签
     */
    void delete(Long id, Long userId);

}
