package com.knowledge.core.service;

import com.knowledge.core.entity.FileEntity;
import com.knowledge.core.vo.FileVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件服务接口
 * @author: nuts_tian
 */
public interface FileService {

    /**
     * 上传文件
     */
    FileVO upload(MultipartFile file, String type, Long userId);

    /**
     * 批量上传文件
     */
    List<FileVO> uploadBatch(List<MultipartFile> files, String type, Long userId);

    /**
     * 删除文件
     */
    void delete(Long id, Long userId);

    /**
     * 根据ID获取文件
     */
    FileVO getById(Long id);

    /**
     * 获取用户文件列表
     */
    List<FileVO> listByUser(Long userId, String type, Integer page, Integer size);

    /**
     * 根据文件键获取文件
     */
    FileEntity getByFileKey(String fileKey);
}
