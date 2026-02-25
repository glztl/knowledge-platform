package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.entity.DocumentTagEntity;
import com.knowledge.core.entity.TagEntity;
import com.knowledge.core.mapper.DocumentTagMapper;
import com.knowledge.core.mapper.TagMapper;
import com.knowledge.core.service.TagService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 标签服务实现类
 * @author nuts_tian
 */
@Service
@RequiredArgsConstructor
public class TagServiceImpl extends ServiceImpl<TagMapper, TagEntity> implements TagService {

    @Resource
    private DocumentTagMapper documentTagMapper;

    @Override
    public List<TagEntity> listByUser(Long userId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<TagEntity>()
                        .eq(TagEntity::getUserId, userId)
                        .orderByDesc(TagEntity::getCreatedAt)
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public TagEntity create(String name, Long userId) {
        // 检查标签是否已存在
        TagEntity existingTag = baseMapper.selectOne(
                new LambdaQueryWrapper<TagEntity>()
                        .eq(TagEntity::getName, name)
                        .eq(TagEntity::getUserId, userId)
        );

        if (existingTag != null) {
            return existingTag;
        }

        TagEntity tag = new TagEntity();
        tag.setName(name);
        tag.setUserId(userId);
        baseMapper.insert(tag);

        return tag;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        // 删除标签
        baseMapper.deleteById(id);

        // 删除文档标签关联
        LambdaQueryWrapper<DocumentTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTagEntity::getTagId, id);
        documentTagMapper.delete(wrapper);
    }
}
