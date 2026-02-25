package com.knowledge.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.core.entity.TagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 标签Mapper
 * @author: nuts_tian
 */
@Mapper
public interface TagMapper extends BaseMapper<TagEntity> {
}