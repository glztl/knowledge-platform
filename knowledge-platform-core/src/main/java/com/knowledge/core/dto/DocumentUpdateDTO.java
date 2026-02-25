package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 更新文档DTO
 * @author: nuts_tian
 */
@Data
@Schema(description = "更新文档请求")
public class DocumentUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "文档ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文档ID不能为空")
    private Long id;

    @Schema(description = "文档标题", example = "Spring Boot 快速入门")
    @NotBlank(message = "标题不能为空")
    private String title;

    @Schema(description = "文档内容（Markdown）")
    private String content;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "标签ID列表", example = "[1, 2, 3]")
    private List<Long> tagIds;

    @Schema(description = "是否公开：0-私有，1-公开", example = "0")
    private Integer isPublic;
}