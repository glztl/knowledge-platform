package com.knowledge.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档实体类
 * @author: nuts_tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("document")
public class DocumentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 文档ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档标题
     */
    @TableField("title")
    private String title;

    /**
     * 文档内容（Markdown）
     */
    @TableField("content")
    private String content;

    /**
     * 渲染后的HTML（缓存）
     */
    @TableField("content_html")
    private String contentHtml;

    /**
     * 分类ID，0表示未分类
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 作者ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否公开：0-私有，1-公开
     */
    @TableField("is_public")
    private Integer isPublic;

    /**
     * 浏览次数
     */
    @TableField("view_count")
    private Integer viewCount;

    /**
     * 状态：0-删除，1-正常
     */
    @TableField("status")
    @TableLogic(value = "1", delval = "0")
    private Integer status;

    /**
     * 分享令牌
     */
    @TableField("share_token")
    private String shareToken;

    /**
     * 分享密码（BCrypt）
     */
    @TableField("share_password")
    private String sharePassword;

    /**
     * 分享过期时间
     */
    @TableField("share_expire_at")
    private LocalDateTime shareExpireAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}