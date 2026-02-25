package com.knowledge.core.service;



import com.knowledge.core.dto.DocumentCreateDTO;
import com.knowledge.core.dto.DocumentUpdateDTO;
import com.knowledge.core.vo.DocumentVO;

import java.util.List;

/**
 * 文档服务接口
 * @author: nuts_tian
 */
public interface DocumentService {


    /**
     * 创建文档
     */
    DocumentVO create(DocumentCreateDTO dto, Long userId);

    /**
     * 更新文档
     */
    DocumentVO update(DocumentUpdateDTO dto, Long userId);

    /**
     * 删除文档
     */
    void delete(Long id, Long userId);

    /**
     * 获取文档详情
     */
    DocumentVO getById(Long id);

    /**
     * 获取用户文档列表（分页）
     */
    List<DocumentVO> listByUser(Long userId, Integer page, Integer size);

    /**
     * 按分类查询文档
     */
    List<DocumentVO> listByCategory(Long categoryId, Long userId);

    /**
     * 按标签查询文档
     */
    List<DocumentVO> listByTag(Long tagId, Long userId);

    /**
     * 搜索文档
     */
    List<DocumentVO> search(String keyword, Long userId);
}