package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 访问分享文档DTO
 * @author: nuts_tian
 */
@Data
@Schema(description = "访问分享文档请求")
public class DocumentAccessDTO {

    @Schema(description = "分享令牌", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "分享令牌不能为空")
    private String token;

    @Schema(description = "分享密码（如果需要）")
    private String password;
}