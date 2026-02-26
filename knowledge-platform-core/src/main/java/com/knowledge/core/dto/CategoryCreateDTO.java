package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 创建分类DTO
 * @author nuts_tian
 */
@Data
@Schema(description = "创建分类请求")
public class CategoryCreateDTO {

    @Schema(description = "分类名称", example = "技术笔记", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分类名称不能为空")
    private String name;

    @Schema(description = "父分类ID，0表示根分类", example = "0")
    @NotNull(message = "父分类ID不能为空")
    private Long parentId = 0L;
}