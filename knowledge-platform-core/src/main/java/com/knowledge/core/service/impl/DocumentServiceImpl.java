package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.dto.DocumentCreateDTO;
import com.knowledge.core.dto.DocumentUpdateDTO;
import com.knowledge.core.entity.CategoryEntity;
import com.knowledge.core.entity.DocumentEntity;
import com.knowledge.core.entity.DocumentTagEntity;
import com.knowledge.core.entity.TagEntity;
import com.knowledge.core.mapper.CategoryMapper;
import com.knowledge.core.mapper.DocumentMapper;
import com.knowledge.core.mapper.DocumentTagMapper;
import com.knowledge.core.mapper.TagMapper;
import com.knowledge.core.service.DocumentService;
import com.knowledge.core.service.DocumentVersionService;
import com.knowledge.core.vo.DocumentVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档服务实现类
 * @author: nuts_tian
 */
@Service
@RequiredArgsConstructor
public class DocumentServiceImpl extends ServiceImpl<DocumentMapper, DocumentEntity> implements DocumentService {

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private TagMapper tagMapper;

    @Resource
    private DocumentTagMapper documentTagMapper;

    @Resource
    private DocumentVersionService documentVersionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO create(DocumentCreateDTO dto, Long userId) {
        // 1. 验证分类是否存在（如果指定了分类）
        if (dto.getCategoryId() != null && dto.getCategoryId() != 0) {
            CategoryEntity category = categoryMapper.selectById(dto.getCategoryId());
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BusinessException(ResultCode.VALIDATION_FAILED, "分类不存在");
            }
        }

        // 2. 创建文档
        DocumentEntity document = new DocumentEntity();
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        document.setCategoryId(dto.getCategoryId() != null ? dto.getCategoryId() : 0L);
        document.setUserId(userId);
        document.setIsPublic(dto.getIsPublic() != null ? dto.getIsPublic() : 0);
        document.setViewCount(0);
        document.setStatus(1);
        baseMapper.insert(document);

        // 保存新版本
        documentVersionService.saveVersion(document.getId(), userId, "文档创建");

        // 3. 处理标签
        if (!CollectionUtils.isEmpty(dto.getTagIds())) {
            saveDocumentTags(document.getId(), dto.getTagIds());
        }

        // 4. 转换为VO返回
        return convertToVO(document);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentVO update(DocumentUpdateDTO dto, Long userId) {
        // 1. 验证文档是否存在且属于当前用户
        DocumentEntity document = baseMapper.selectById(dto.getId());
        if (document == null || !document.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 2. 验证分类是否存在（如果指定了分类）
        if (dto.getCategoryId() != null && dto.getCategoryId() != 0) {
            CategoryEntity category = categoryMapper.selectById(dto.getCategoryId());
            if (category == null || !category.getUserId().equals(userId)) {
                throw new BusinessException(ResultCode.VALIDATION_FAILED, "分类不存在");
            }
        }

        // 3. 更新文档
        document.setTitle(dto.getTitle());
        document.setContent(dto.getContent());
        if (dto.getCategoryId() != null) {
            document.setCategoryId(dto.getCategoryId());
        }
        if (dto.getIsPublic() != null) {
            document.setIsPublic(dto.getIsPublic());
        }
        baseMapper.updateById(document);

        // 保存新版本
        documentVersionService.saveVersion(document.getId(), userId, "文档更新");

        // 4. 更新标签（先删除旧标签，再添加新标签）
        if (dto.getTagIds() != null) {
            LambdaQueryWrapper<DocumentTagEntity> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(DocumentTagEntity::getDocumentId, document.getId());
            documentTagMapper.delete(wrapper);

            if (!CollectionUtils.isEmpty(dto.getTagIds())) {
                saveDocumentTags(document.getId(), dto.getTagIds());
            }
        }

        // 5. 转换为VO返回
        return convertToVO(document);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        // 1. 验证文档是否存在且属于当前用户
        DocumentEntity document = baseMapper.selectById(id);
        if (document == null || !document.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 2. 逻辑删除（设置status=0）
        document.setStatus(0);
        baseMapper.updateById(document);

        // 3. 删除文档标签关联
        LambdaQueryWrapper<DocumentTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTagEntity::getDocumentId, id);
        documentTagMapper.delete(wrapper);
    }

    @Override
    public DocumentVO getById(Long id) {
        // 1. 查询文档
        DocumentEntity document = baseMapper.selectById(id);
        if (document == null || document.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 2. 增加浏览次数
        document.setViewCount(document.getViewCount() + 1);
        baseMapper.updateById(document);

        // 3. 转换为VO
        return convertToVO(document);
    }

    @Override
    public List<DocumentVO> listByUser(Long userId, Integer page, Integer size) {
        Page<DocumentEntity> pageInfo = new Page<>(page, size);
        Page<DocumentEntity> result = baseMapper.selectPage(pageInfo,
                new LambdaQueryWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getUserId, userId)
                        .eq(DocumentEntity::getStatus, 1)
                        .orderByDesc(DocumentEntity::getCreatedAt));

        return convertToVOList(result.getRecords());
    }

    @Override
    public List<DocumentVO> listByCategory(Long categoryId, Long userId) {
        List<DocumentEntity> documents = baseMapper.selectList(
                new LambdaQueryWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getCategoryId, categoryId)
                        .eq(DocumentEntity::getUserId, userId)
                        .eq(DocumentEntity::getStatus, 1)
                        .orderByDesc(DocumentEntity::getCreatedAt));

        return convertToVOList(documents);
    }

    @Override
    public List<DocumentVO> listByTag(Long tagId, Long userId) {
        // 1. 查询该标签下的所有文档ID
        List<DocumentTagEntity> documentTags = documentTagMapper.selectList(
                new LambdaQueryWrapper<DocumentTagEntity>()
                        .eq(DocumentTagEntity::getTagId, tagId));

        if (CollectionUtils.isEmpty(documentTags)) {
            return Collections.emptyList();
        }

        List<Long> documentIds = documentTags.stream()
                .map(DocumentTagEntity::getDocumentId)
                .collect(Collectors.toList());

        // 2. 查询文档
        List<DocumentEntity> documents = baseMapper.selectList(
                new LambdaQueryWrapper<DocumentEntity>()
                        .in(DocumentEntity::getId, documentIds)
                        .eq(DocumentEntity::getUserId, userId)
                        .eq(DocumentEntity::getStatus, 1)
                        .orderByDesc(DocumentEntity::getCreatedAt));

        return convertToVOList(documents);
    }

    @Override
    public List<DocumentVO> search(String keyword, Long userId) {
        List<DocumentEntity> documents = baseMapper.selectList(
                new LambdaQueryWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getUserId, userId)
                        .eq(DocumentEntity::getStatus, 1)
                        .and(wrapper -> wrapper
                                .like(DocumentEntity::getTitle, keyword)
                                .or()
                                .like(DocumentEntity::getContent, keyword))
                        .orderByDesc(DocumentEntity::getCreatedAt));

        return convertToVOList(documents);
    }


    /**
     * 保存文档标签关联
     */
    private void saveDocumentTags(Long documentId, List<Long> tagIds) {
        for (Long tagId : tagIds) {
            TagEntity tag = tagMapper.selectById(tagId);
            if (tag != null) {
                DocumentTagEntity documentTag = new DocumentTagEntity();
                documentTag.setDocumentId(documentId);
                documentTag.setTagId(tagId);
                documentTagMapper.insert(documentTag);
            }
        }
    }

    /**
     * 转换单个实体为VO
     */
    private DocumentVO convertToVO(DocumentEntity document) {
        DocumentVO vo = new DocumentVO();
        vo.setId(document.getId());
        vo.setTitle(document.getTitle());
        vo.setContent(document.getContent());
        vo.setCategoryId(document.getCategoryId());
        vo.setIsPublic(document.getIsPublic());
        vo.setViewCount(document.getViewCount());
        vo.setCreatedAt(document.getCreatedAt());
        vo.setUpdatedAt(document.getUpdatedAt());

        vo.setUserId(document.getUserId());

        // 查询分类名称
        if (document.getCategoryId() != null && document.getCategoryId() != 0) {
            CategoryEntity category = categoryMapper.selectById(document.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 查询标签名称
        LambdaQueryWrapper<DocumentTagEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentTagEntity::getDocumentId, document.getId());
        List<DocumentTagEntity> documentTags = documentTagMapper.selectList(wrapper);

        if (!CollectionUtils.isEmpty(documentTags)) {
            List<Long> tagIds = documentTags.stream()
                    .map(DocumentTagEntity::getTagId)
                    .collect(Collectors.toList());

            List<TagEntity> tags = tagMapper.selectBatchIds(tagIds);
            vo.setTagNames(tags.stream()
                    .map(TagEntity::getName)
                    .collect(Collectors.toList()));
        }

        return vo;
    }

    /**
     * 转换实体列表为VO列表
     */
    private List<DocumentVO> convertToVOList(List<DocumentEntity> documents) {
        return documents.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
}