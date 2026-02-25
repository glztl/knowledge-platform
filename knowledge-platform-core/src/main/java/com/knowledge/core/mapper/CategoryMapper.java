package com.knowledge.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.core.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 分类Mapper
 * @author: nuts_tian
 */
@Mapper
public interface CategoryMapper extends BaseMapper<CategoryEntity> {
}