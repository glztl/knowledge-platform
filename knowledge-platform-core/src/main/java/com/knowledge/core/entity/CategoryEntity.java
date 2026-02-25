package com.knowledge.core.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 分类实体类
 * @author: nuts_tian
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("category")
public class CategoryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 分类ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 父分类ID，0表示根分类
     */
    @TableField("parent_id")
    private Long parentId;

    /**
     * 排序权重
     */
    @TableField("sort")
    private Integer sort;

    /**
     * 所属用户ID
     */
    @TableField("user_id")
    private Long userId;

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

    /**
     * 子分类列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<CategoryEntity> children;
}
