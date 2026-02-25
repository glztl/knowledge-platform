package com.knowledge.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.core.entity.DocumentTagEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档标签关联Mapper
 * @author: nuts_tian
 */
@Mapper
public interface DocumentTagMapper extends BaseMapper<DocumentTagEntity> {
}