package com.knowledge.core.controller;

import com.knowledge.core.common.result.Result;
import com.knowledge.core.dto.DocumentRollbackDTO;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.DocumentVersionService;
import com.knowledge.core.vo.DocumentVersionDetailVO;
import com.knowledge.core.vo.DocumentVersionVO;
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

import java.util.List;

/**
 * 文档版本控制器
 * @author: nuts_tian
 */
@Tag(name = "📜 文档版本", description = "文档版本历史管理")
@RestController
@RequestMapping("/document/version")
@RequiredArgsConstructor
public class DocumentVersionController {

    private final DocumentVersionService versionService;

    @Operation(summary = "获取文档版本列表", description = "获取指定文档的所有版本历史")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = DocumentVersionVO.class)))
    @GetMapping("/list/{documentId}")
    public Result<List<DocumentVersionVO>> listVersions(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long documentId) {
        List<DocumentVersionVO> list = versionService.listVersions(documentId);
        return Result.success(list);
    }

    @Operation(summary = "获取版本详情", description = "获取指定版本的详细信息")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = DocumentVersionDetailVO.class)))
    @GetMapping("/detail/{versionId}")
    public Result<DocumentVersionDetailVO> getVersionDetail(
            @Parameter(description = "版本ID", required = true, example = "1")
            @PathVariable Long versionId) {
        DocumentVersionDetailVO vo = versionService.getVersionDetail(versionId);
        return Result.success(vo);
    }

    @Operation(summary = "回滚到指定版本", description = "将文档回滚到指定的历史版本")
    @ApiResponse(responseCode = "200", description = "回滚成功")
    @PostMapping("/rollback")
    public Result<Void> rollback(
            @Parameter(description = "回滚信息", required = true)
            @Valid @RequestBody DocumentRollbackDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        versionService.rollback(dto, userId);
        return Result.success();
    }
}
