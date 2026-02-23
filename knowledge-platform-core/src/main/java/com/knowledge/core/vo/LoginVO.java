package com.knowledge.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应
 */
@Data
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "用户信息")
    private UserVO user;

    @Schema(description = "Token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "过期时间（毫秒）", example = "86400000")
    private Long expire;
}