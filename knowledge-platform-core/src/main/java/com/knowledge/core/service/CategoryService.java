package com.knowledge.core.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.knowledge.core.entity.CategoryEntity;

import java.util.List;

/**
 * 分类服务接口
 * @author nuts_tian
 */
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * 获取用户的所有分类（树形结构）
     */
    List<CategoryEntity> listByUser(Long userId);

    /**
     * 创建分类
     */
    CategoryEntity create(String name, Long parentId, Long userId);

    /**
     * 删除分类
     */
    void delete(Long id, Long userId);

}
