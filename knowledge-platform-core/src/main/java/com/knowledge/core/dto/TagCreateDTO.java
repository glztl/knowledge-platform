package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 创建标签DTO
 * @author nuts_tian
 */
@Data
@Schema(description = "创建标签请求")
public class TagCreateDTO {

    @Schema(description = "标签名称", example = "Java", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "标签名称不能为空")
    private String name;
}
