package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.entity.CategoryEntity;
import com.knowledge.core.mapper.CategoryMapper;
import com.knowledge.core.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 *
 * @author nuts_tian
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public List<CategoryEntity> listByUser(Long userId) {
        List<CategoryEntity> allCategories = baseMapper.selectList(
                new LambdaQueryWrapper<CategoryEntity>()
                        .eq(CategoryEntity::getUserId, userId)
                        .orderByAsc(CategoryEntity::getSort)
                        .orderByAsc(CategoryEntity::getCreatedAt)
        );

        return buildCategoryTree(allCategories, 0L);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryEntity create(String name, Long parentId, Long userId) {
        CategoryEntity category = new CategoryEntity();
        category.setName(name);
        category.setParentId(parentId != null ? parentId : 0L);
        category.setUserId(userId);
        category.setSort(0);
        baseMapper.insert(category);
        return category;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        baseMapper.deleteById(id);

        LambdaQueryWrapper<CategoryEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryEntity::getParentId, id)
                .eq(CategoryEntity::getUserId, userId);
        List<CategoryEntity> children = baseMapper.selectList(wrapper);

        for (CategoryEntity child : children) {
            child.setParentId(0L);
            baseMapper.updateById(child);
        }
    }

    private List<CategoryEntity> buildCategoryTree(List<CategoryEntity> allCategories, Long parentId) {
        return allCategories.stream()
                .filter(category -> category.getParentId().equals(parentId))
                .peek(category -> {
                    List<CategoryEntity> children = buildCategoryTree(allCategories, category.getId());
                    category.setChildren(children);
                })
                .collect(Collectors.toList());
    }


}
