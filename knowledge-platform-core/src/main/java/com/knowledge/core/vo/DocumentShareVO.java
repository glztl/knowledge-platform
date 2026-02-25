package com.knowledge.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档分享VO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文档分享信息")
public class DocumentShareVO {

    @Schema(description = "文档标题", example = "Spring Boot 快速入门")
    private String title;

    @Schema(description = "分享链接")
    private String shareUrl;

    @Schema(description = "分享令牌")
    private String shareToken;

    @Schema(description = "是否需要密码", example = "false")
    private Boolean requirePassword;

    @Schema(description = "分享过期时间")
    private LocalDateTime expireAt;

    @Schema(description = "是否永久有效", example = "false")
    private Boolean permanent;
}
