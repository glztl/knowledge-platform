package com.knowledge.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.knowledge.core.entity.DocumentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文档Mapper
 * @author: nuts_tian
 */
@Mapper
public interface DocumentMapper extends BaseMapper<DocumentEntity> {
}