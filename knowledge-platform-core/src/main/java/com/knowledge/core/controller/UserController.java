package com.knowledge.core.controller;



import com.knowledge.core.common.result.Result;
import com.knowledge.core.dto.UserLoginDTO;
import com.knowledge.core.dto.UserRegisterDTO;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.UserService;
import com.knowledge.core.vo.LoginVO;
import com.knowledge.core.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * @author nuts_tian
 */
@Tag(name = " 用户管理", description = "用户注册、登录、信息管理")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "创建新用户账号")
    @ApiResponse(responseCode = "200", description = "注册成功",
            content = @Content(schema = @Schema(implementation = UserVO.class)))
    @PostMapping("/register")
    public Result<UserVO> register(
            @Parameter(description = "用户注册信息", required = true)
            @Valid @RequestBody UserRegisterDTO dto) {
        UserVO userVO = userService.register(dto);
        return Result.success(userVO);
    }

    @Operation(summary = "用户登录", description = "用户登录获取 Token")
    @ApiResponse(responseCode = "200", description = "登录成功",
            content = @Content(schema = @Schema(implementation = LoginVO.class)))
    @PostMapping("/login")
    public Result<LoginVO> login(
            @Parameter(description = "登录信息", required = true)
            @Valid @RequestBody UserLoginDTO dto) {
        LoginVO loginVO = userService.login(dto);
        return Result.success(loginVO);
    }

    @Operation(summary = "获取当前用户信息", description = "需要登录后访问")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = UserVO.class)))
    @GetMapping("/profile")
    public Result<UserVO> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        // 从 SecurityContext 获取当前用户
        String username = userDetails.getUsername();
        UserVO userVO = userService.getById(((UserPrincipal) userDetails).getId());
        return Result.success(userVO);
    }
}
