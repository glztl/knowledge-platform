package com.knowledge.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 文档版本实体类
 * @author: nuts_tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("document_version")
public class DocumentVersionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    /**
     * 版本ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    @TableField("document_id")
    private Long documentId;

    /**
     * 版本号
     */
    @TableField("version_number")
    private Integer versionNumber;

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
     * 渲染后的HTML
     */
    @TableField("content_html")
    private String contentHtml;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 修改人ID
     */
    @TableField("modified_by")
    private Long modifiedBy;

    /**
     * 修改时间
     */
    @TableField("modified_at")
    private LocalDateTime modifiedAt;

    /**
     * 变更摘要
     */
    @TableField("change_summary")
    private String changeSummary;

    /**
     * 是否为当前版本：0-否，1-是
     */
    @TableField("is_current")
    private Integer isCurrent;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
