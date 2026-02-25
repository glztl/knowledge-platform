package com.knowledge.core.controller;

import com.knowledge.core.common.result.Result;
import com.knowledge.core.dto.DocumentAccessDTO;
import com.knowledge.core.dto.DocumentShareDTO;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.DocumentShareService;
import com.knowledge.core.vo.DocumentShareVO;
import com.knowledge.core.vo.DocumentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * 文档分享控制器
 */
@Tag(name = "🔗 文档分享", description = "文档分享功能")
@RestController
@RequestMapping("/document/share")
@RequiredArgsConstructor
public class DocumentShareController {

    private final DocumentShareService shareService;

    @Operation(summary = "生成分享链接", description = "为文档生成分享链接")
    @ApiResponse(responseCode = "200", description = "生成成功",
            content = @Content(schema = @Schema(implementation = DocumentShareVO.class)))
    @PostMapping("/generate")
    public Result<DocumentShareVO> generateShareLink(
            @Parameter(description = "分享设置", required = true)
            @Valid @RequestBody DocumentShareDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        DocumentShareVO vo = shareService.generateShareLink(dto, userId);
        return Result.success(vo);
    }

    @Operation(summary = "访问分享文档", description = "通过分享令牌访问文档")
    @ApiResponse(responseCode = "200", description = "访问成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @PostMapping("/access")
    public Result<DocumentVO> accessSharedDocument(
            @Parameter(description = "访问信息", required = true)
            @Valid @RequestBody DocumentAccessDTO dto) {
        DocumentVO vo = shareService.accessSharedDocument(dto);
        return Result.success(vo);
    }

    @Operation(summary = "取消分享", description = "取消文档的分享")
    @ApiResponse(responseCode = "200", description = "取消成功")
    @DeleteMapping("/cancel/{documentId}")
    public Result<Void> cancelShare(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        shareService.cancelShare(documentId, userId);
        return Result.success();
    }

    @Operation(summary = "更新分享设置", description = "更新文档的分享设置")
    @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = DocumentShareVO.class)))
    @PutMapping("/update")
    public Result<DocumentShareVO> updateShareSettings(
            @Parameter(description = "分享设置", required = true)
            @Valid @RequestBody DocumentShareDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        DocumentShareVO vo = shareService.updateShareSettings(dto, userId);
        return Result.success(vo);
    }

    @Operation(summary = "通过分享链接直接访问", description = "无需密码直接访问公开文档")
    @ApiResponse(responseCode = "200", description = "访问成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/{token}")
    public Result<DocumentVO> accessByToken(
            @Parameter(description = "分享令牌", required = true)
            @PathVariable String token,
            @Parameter(description = "分享密码（如果需要）")
            @RequestParam(required = false) String password) {
        DocumentAccessDTO dto = new DocumentAccessDTO();
        dto.setToken(token);
        dto.setPassword(password);
        DocumentVO vo = shareService.accessSharedDocument(dto);
        return Result.success(vo);
    }
}