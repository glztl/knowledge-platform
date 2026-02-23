package com.knowledge.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author: nuts_tian
 */
@Data
@Schema(description = "分类")
public class CategoryEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -874655287300177784L;

    /**
     * 分类id
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "分类id")
    private Long id;

    @Schema(description = "分类名称")
    private String name;

    @Schema(description = "父级分类id")
    private Long parentId;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
