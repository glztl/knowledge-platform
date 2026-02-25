package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文档回滚DTO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文档回滚请求")
public class DocumentRollbackDTO {

    @Schema(description = "文档ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文档ID不能为空")
    private Long documentId;

    @Schema(description = "版本号", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "版本号不能为空")
    private Integer versionNumber;

    @Schema(description = "回滚原因", example = "回滚到上一个稳定版本")
    private String reason;
}