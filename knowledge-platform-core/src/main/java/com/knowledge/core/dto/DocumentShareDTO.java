package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 文档分享DTO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文档分享请求")
public class DocumentShareDTO {

    @Schema(description = "文档ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "文档ID不能为空")
    private Long id;

    @Schema(description = "是否需要密码：true-需要，false-不需要", example = "false")
    private Boolean requirePassword = false;

    @Schema(description = "分享密码（如果需要密码）")
    private String password;

    @Schema(description = "分享有效期（小时），0表示永久有效", example = "24")
    private Integer expireHours = 0;

    @Schema(description = "是否公开（设置为公开后，无需分享令牌即可访问）", example = "false")
    private Boolean publicAccess = false;
}