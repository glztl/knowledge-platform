package com.knowledge.core.service;

import com.knowledge.core.dto.DocumentAccessDTO;
import com.knowledge.core.dto.DocumentShareDTO;
import com.knowledge.core.vo.DocumentShareVO;
import com.knowledge.core.vo.DocumentVO;

/**
 * 文档分享服务接口
 * @author: nuts_tian
 */
public interface DocumentShareService {

    /**
     * 生成分享链接
     */
    DocumentShareVO generateShareLink(DocumentShareDTO dto, Long userId);

    /**
     * 访问分享文档
     */
    DocumentVO accessSharedDocument(DocumentAccessDTO dto);

    /**
     * 取消分享
     */
    void cancelShare(Long documentId, Long userId);

    /**
     * 更新分享设置
     */
    DocumentShareVO updateShareSettings(DocumentShareDTO dto, Long userId);
}
