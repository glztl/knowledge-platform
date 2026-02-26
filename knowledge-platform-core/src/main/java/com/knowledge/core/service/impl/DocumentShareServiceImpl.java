package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.common.utils.PasswordEncoderUtil;
import com.knowledge.core.dto.DocumentAccessDTO;
import com.knowledge.core.dto.DocumentShareDTO;
import com.knowledge.core.entity.DocumentEntity;
import com.knowledge.core.mapper.DocumentMapper;
import com.knowledge.core.service.DocumentService;
import com.knowledge.core.service.DocumentShareService;
import com.knowledge.core.vo.DocumentShareVO;
import com.knowledge.core.vo.DocumentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 文档分享服务实现类
 * @author: nuts_tian
 */
@Service
@RequiredArgsConstructor
public class DocumentShareServiceImpl implements DocumentShareService {

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private PasswordEncoderUtil passwordEncoderUtil;


    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Value("${server.port}")
    private int port;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public DocumentShareVO generateShareLink(DocumentShareDTO dto, Long userId) {
        // 验证文档是否存在且属于当前用户
        DocumentEntity document = documentMapper.selectById(dto.getId());
        if (document == null || !document.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 生成分享令牌
        String shareToken = UUID.randomUUID().toString().replace("-", "");

        // 设置分享密码
        String sharePassword = null;
        if (Boolean.TRUE.equals(dto.getRequirePassword()) && dto.getPassword() != null) {
            sharePassword = passwordEncoderUtil.encode(dto.getPassword());
        }

        // 设置过期时间
        LocalDateTime expireAt = null;
        if (dto.getExpireHours() != null && dto.getExpireHours() > 0) {
            expireAt = LocalDateTime.now().plusHours(dto.getExpireHours());
        }

        // 更新文档分享信息
        document.setShareToken(shareToken);
        document.setSharePassword(sharePassword);
        document.setShareExpireAt(expireAt);

//        // 如果设置为公开访问
//        if (Boolean.TRUE.equals(dto.getPublicAccess())) {
//            document.setIsPublic(1);
//        } else {
//            document.setIsPublic(0);
//        }

        documentMapper.updateById(document);

        // 返回
        DocumentShareVO vo = new DocumentShareVO();
        vo.setTitle(document.getTitle());
        vo.setShareToken(shareToken);

//        String shareUrl = String.format("http://localhost:%d%s/share/%s",
//                port, contextPath, shareToken);

        String frontendUrl = "http://localhost:3000/share/" + shareToken;
        vo.setShareUrl(frontendUrl);
        vo.setRequirePassword(Boolean.TRUE.equals(dto.getRequirePassword()));
        vo.setExpireAt(expireAt);
        vo.setPermanent(dto.getExpireHours() == null || dto.getExpireHours() == 0);

        return vo;
    }


    @Override
    public DocumentVO accessSharedDocument(DocumentAccessDTO dto) {
        // 根据分享令牌查询文档
        DocumentEntity document = documentMapper.selectOne(
                new LambdaQueryWrapper<DocumentEntity>()
                        .eq(DocumentEntity::getShareToken, dto.getToken())
                        .eq(DocumentEntity::getStatus, 1)
        );

        if (document == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "分享连接不存在或已失效");
        }

        // 检查是否过期
        if (document.getShareExpireAt() != null && document.getShareExpireAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ResultCode.FORBIDDEN, "分享连接已过期");
        }

        // 检查是否需要密码
        if (document.getSharePassword() != null) {
            if (dto.getPassword() == null) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "请输入分享密码");
            }

            if (!passwordEncoderUtil.matches(dto.getPassword(), document.getSharePassword())) {
                throw new BusinessException(ResultCode.UNAUTHORIZED, "密码错误");
            }
        }

        // 增加浏览次数
        document.setViewCount(document.getViewCount() == null ? 1 : document.getViewCount() + 1);
        documentMapper.updateById(document);

        // vo
        return documentService.getById(document.getId());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelShare(Long documentId, Long userId) {
        DocumentEntity document = documentMapper.selectById(documentId);
        if (document == null || !document.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文档不存在");
        }

        // 清除分享信息
        document.setShareToken(null);
        document.setSharePassword(null);
        document.setShareExpireAt(null);
        document.setIsPublic(0);

        documentMapper.updateById(document);
    }


    @Override
    @Transactional
    public DocumentShareVO updateShareSettings(DocumentShareDTO dto, Long userId) {
        return generateShareLink(dto, userId);
    }









}
