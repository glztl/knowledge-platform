package com.knowledge.core.service;

import com.knowledge.core.dto.DocumentRollbackDTO;
import com.knowledge.core.vo.DocumentVersionDetailVO;
import com.knowledge.core.vo.DocumentVersionVO;

import java.util.List;

/**
 * 文档版本服务接口
 * @author: nuts_tian
 */
public interface DocumentVersionService {

    /**
     * 保存文档版本
     * @param documentId 文档ID
     * @param userId 操作用户ID
     * @param changeSummary 变更摘要
     */
    void saveVersion(Long documentId, Long userId, String changeSummary);

    /**
     * 获取文档版本列表
     * @param documentId 文档ID
     * @return 版本列表
     */
    List<DocumentVersionVO> listVersions(Long documentId);

    /**
     * 获取版本详情
     * @param versionId 版本ID
     * @return 版本详情
     */
    DocumentVersionDetailVO getVersionDetail(Long versionId);

    /**
     * 回滚到指定版本
     * @param dto 回滚请求
     * @param userId 操作用户ID
     */
    void rollback(DocumentRollbackDTO dto, Long userId);

    /**
     * 删除文档的所有版本（文档删除时调用）
     * @param documentId 文档ID
     */
    void deleteVersionsByDocumentId(Long documentId);
}
