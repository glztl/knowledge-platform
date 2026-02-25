package com.knowledge.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文件实体类
 * @author: nuts_tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("file")
public class FileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 原始文件名
     */
    @TableField("filename")
    private String filename;

    /**
     * 存储键（MinIO路径）
     */
    @TableField("file_key")
    private String fileKey;

    /**
     * 文件大小（字节）
     */
    @TableField("file_size")
    private Long fileSize;

    /**
     * MIME类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 访问URL
     */
    @TableField("url")
    private String url;

    /**
     * 上传用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}