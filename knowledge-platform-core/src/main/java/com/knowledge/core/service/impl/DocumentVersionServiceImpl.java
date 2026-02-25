package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.dto.DocumentRollbackDTO;
import com.knowledge.core.entity.CategoryEntity;
import com.knowledge.core.entity.DocumentEntity;
import com.knowledge.core.entity.DocumentVersionEntity;
import com.knowledge.core.entity.UserEntity;
import com.knowledge.core.mapper.CategoryMapper;
import com.knowledge.core.mapper.DocumentMapper;
import com.knowledge.core.mapper.DocumentVersionMapper;
import com.knowledge.core.mapper.UserMapper;
import com.knowledge.core.service.DocumentVersionService;
import com.knowledge.core.vo.DocumentVersionDetailVO;
import com.knowledge.core.vo.DocumentVersionVO;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档版本服务实现类
 * @author: nuts_tian
 */
@Service
@RequiredArgsConstructor
public class DocumentVersionServiceImpl extends ServiceImpl<DocumentVersionMapper, DocumentVersionEntity> implements DocumentVersionService {


    @Resource
    private DocumentMapper documentMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private CategoryMapper categoryMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveVersion(Long documentId, Long userId, String changeSummary) {
        // 查询文档
        DocumentEntity document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 查询当前最大版本号
        LambdaQueryWrapper<DocumentVersionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersionEntity::getDocumentId, documentId)
                .orderByDesc(DocumentVersionEntity::getVersionNumber)
                .last("LIMIT 1");

        DocumentVersionEntity latestVersion = baseMapper.selectOne(wrapper);
        int nextVersionNumber = latestVersion != null ? latestVersion.getVersionNumber() + 1 : 1;

        // 保存旧版本为非当前版本
        if (latestVersion != null) {
            latestVersion.setIsCurrent(0);
            baseMapper.updateById(latestVersion);
        }

        // 创建新版本
        DocumentVersionEntity version = new DocumentVersionEntity();
        version.setDocumentId(documentId);
        version.setVersionNumber(nextVersionNumber);
        version.setTitle(document.getTitle());
        version.setContent(document.getContent());
        version.setContentHtml(document.getContentHtml());
        version.setCategoryId(document.getCategoryId());
        version.setModifiedBy(userId);
        version.setModifiedAt(LocalDateTime.now());
        version.setChangeSummary(changeSummary != null ? changeSummary : "版本保存");
        version.setIsCurrent(1);

        baseMapper.insert(version);
    }

    @Override
    public List<DocumentVersionVO> listVersions(Long documentId) {
        // 查询文档是否存在
        DocumentEntity document = documentMapper.selectById(documentId);
        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 查询版本列表（按版本号降序）
        LambdaQueryWrapper<DocumentVersionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersionEntity::getDocumentId, documentId)
                .orderByDesc(DocumentVersionEntity::getVersionNumber);

        List<DocumentVersionEntity> versions = baseMapper.selectList(wrapper);

        // 3. 转换为VO并补充用户昵称
        return versions.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }


    @Override
    public DocumentVersionDetailVO getVersionDetail(Long versionId) {
        // 1. 查询版本
        DocumentVersionEntity version = baseMapper.selectById(versionId);
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "版本不存在");
        }

        // 2. 转换为详情VO
        DocumentVersionDetailVO vo = new DocumentVersionDetailVO();
        vo.setId(version.getId());
        vo.setDocumentId(version.getDocumentId());
        vo.setVersionNumber(version.getVersionNumber());
        vo.setTitle(version.getTitle());
        vo.setContent(version.getContent());
        vo.setChangeSummary(version.getChangeSummary());
        vo.setModifiedAt(version.getModifiedAt());
        vo.setCreatedAt(version.getCreatedAt());

        // 3. 查询修改人昵称
        UserEntity user = userMapper.selectById(version.getModifiedBy());
        if (user != null) {
            vo.setModifiedByNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        // 4. 查询分类名称
        if (version.getCategoryId() != null && version.getCategoryId() != 0) {
            CategoryEntity category = categoryMapper.selectById(version.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void rollback(DocumentRollbackDTO dto, Long userId) {
        // 验证文档是否存在
        DocumentEntity document = documentMapper.selectById(dto.getDocumentId());
        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 验证版本是否存在
        LambdaQueryWrapper<DocumentVersionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersionEntity::getDocumentId, dto.getDocumentId())
                .eq(DocumentVersionEntity::getVersionNumber, dto.getVersionNumber());

        DocumentVersionEntity version = baseMapper.selectOne(wrapper);
        if (version == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "版本不存在");
        }


        // 恢复文档内容到指定版本
        document.setTitle(version.getTitle());
        document.setContent(version.getContent());
        document.setContentHtml(version.getContentHtml());
        document.setCategoryId(version.getCategoryId());
        document.setUpdatedAt(LocalDateTime.now());

        documentMapper.updateById(document);


        // 保存当前版本
        this.saveVersion(dto.getDocumentId(), userId, dto.getReason() != null ? dto.getReason() : "回滚到指定版本" + dto.getVersionNumber());


    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteVersionsByDocumentId(Long documentId) {
        LambdaQueryWrapper<DocumentVersionEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocumentVersionEntity::getDocumentId, documentId);
        baseMapper.delete(wrapper);
    }

    /**
     * 转换为VO
     */
    private DocumentVersionVO convertToVO(DocumentVersionEntity entity) {
        DocumentVersionVO vo = new DocumentVersionVO();
        vo.setId(entity.getId());
        vo.setDocumentId(entity.getDocumentId());
        vo.setVersionNumber(entity.getVersionNumber());
        vo.setTitle(entity.getTitle());
        vo.setChangeSummary(entity.getChangeSummary());
        vo.setModifiedAt(entity.getModifiedAt());
        vo.setIsCurrent(entity.getIsCurrent() == 1);

        // 查询修改人昵称
        UserEntity user = userMapper.selectById(entity.getModifiedBy());
        if (user != null) {
            vo.setModifiedByNickname(user.getNickname() != null ? user.getNickname() : user.getUsername());
        }

        return vo;
    }
}
