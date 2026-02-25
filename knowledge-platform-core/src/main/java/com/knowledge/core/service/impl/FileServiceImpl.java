package com.knowledge.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.ResultCode;
import com.knowledge.core.entity.FileEntity;
import com.knowledge.core.mapper.FileMapper;
import com.knowledge.core.service.FileService;
import com.knowledge.core.vo.FileVO;
import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 文件服务实现类
 * @author: nuts_tian
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl extends ServiceImpl<FileMapper, FileEntity> implements FileService {

    @Resource
    private MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    @Getter
    @Setter
    @Value("${minio.endpoint}")
    private String endpoint;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileVO upload(MultipartFile file, String type, Long userId) {
        // 校验文件
        validateFile(file);

        try {
            // 生成文件存储路径
            String fileKey = generateFileKey(file.getOriginalFilename(), type);

            // 确保存储桶存在
            ensureBucketExists();

            // 上传文件到MinIO
            try (InputStream inputStream = file.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucket)
                                .object(fileKey)
                                .stream(inputStream, file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );
            }

            // 生成访问URL
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(fileKey)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );

            // 保存文件记录到数据库
            FileEntity fileEntity = new FileEntity();
            fileEntity.setFilename(file.getOriginalFilename());
            fileEntity.setFileKey(fileKey);
            fileEntity.setFileSize(file.getSize());
            fileEntity.setFileType(file.getContentType());
            fileEntity.setUrl(url);
            fileEntity.setUserId(userId);
            baseMapper.insert(fileEntity);

            // 转换为VO
            FileVO vo = new FileVO();
            BeanUtils.copyProperties(fileEntity, vo);
            return vo;

        } catch (Exception e) {
            throw new BusinessException(ResultCode.ERROR, "上传文件失败" + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<FileVO> uploadBatch(List<MultipartFile> files, String type, Long userId) {
        List<FileVO> result = new ArrayList<>();
        for (MultipartFile file : files) {
            result.add(upload(file, type, userId));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id, Long userId) {
        // 查询文件
        FileEntity file = baseMapper.selectById(id);
        if (file == null || !file.getUserId().equals(userId)) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
        }

        try {
            // 从MinIO删除文件
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(file.getFileKey())
                            .build()
            );

            // 从数据库删除记录
            baseMapper.deleteById(id);
        } catch (Exception e) {
            throw new BusinessException(ResultCode.ERROR, "删除文件失败" + e.getMessage());
        }
    }

    @Override
    public FileVO getById(Long id) {
        FileEntity file = baseMapper.selectById(id);
        if (file == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "文件不存在");
        }
        FileVO vo = new FileVO();
        BeanUtils.copyProperties(file, vo);
        return vo;
    }

    @Override
    public List<FileVO> listByUser(Long userId, String type, Integer page, Integer size) {
        LambdaQueryWrapper<FileEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FileEntity::getUserId, userId);

        if (StringUtils.hasText(type)) {
            if ("image".equals(type)) {
                wrapper.like(FileEntity::getFileType, "image/");
            } else if ("document".equals(type)) {
                wrapper.and(w -> w
                        .like(FileEntity::getFileType, "application/")
                        .or()
                        .like(FileEntity::getFileType, "text/"));
            }
        }

        wrapper.orderByDesc(FileEntity::getCreatedAt);

        Page<FileEntity> pageInfo = new Page<>(page, size);
        Page<FileEntity> result = baseMapper.selectPage(pageInfo, wrapper);

        return result.getRecords().stream()
                .map(file -> {
                    FileVO vo = new FileVO();
                    BeanUtils.copyProperties(file, vo);
                    return vo;
                }).toList();
    }

    @Override
    public FileEntity getByFileKey(String fileKey) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<FileEntity>()
                        .eq(FileEntity::getFileKey, fileKey)
        );
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "文件不能为空");
        }

        // 检查文件大小（最大100MB）
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "文件大小不能超过100MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "无法识别文件类型");
        }

        // 只允许图片、文档、压缩包等常见格式
        if (!contentType.startsWith("image/")
        && !contentType.startsWith("application/")
        && !contentType.startsWith("text/")
        && !contentType.startsWith("audio/")
        && !contentType.startsWith("video/")) {
            throw new BusinessException(ResultCode.VALIDATION_FAILED, "不支持的文件类型");
        }
    }

    /**
     * 生成文件存储路径
     */
    private String generateFileKey(String originalFilename, String type) {
        // 生成UUID作为文件名，避免重名
        String uuid = UUID.randomUUID().toString().replace("-", "");

        // 获取文件扩展名
        String extension = "";
        if (StringUtils.hasText(originalFilename)) {
            int lastDotIndex = originalFilename.lastIndexOf(".");
            if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
                extension = originalFilename.substring(lastDotIndex);
            }
        }

        // 按类型分类存储
        String folder = "other";
        if ("image".equals(type)) {
            folder = "images";
        } else if ("document".equals(type)) {
            folder = "documents";
        }

        return String.format("%s/%s%s", folder, uuid, extension);
    }

    /**
     * 确保存储桶存在
     */
    private void ensureBucketExists() throws Exception {
        boolean found = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucket).build()
        );

        if (!found) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucket).build()
            );
        }
    }

}
