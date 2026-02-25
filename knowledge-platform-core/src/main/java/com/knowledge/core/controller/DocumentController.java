package com.knowledge.core.controller;

import com.knowledge.core.common.result.Result;
import com.knowledge.core.dto.DocumentCreateDTO;
import com.knowledge.core.dto.DocumentUpdateDTO;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.DocumentService;
import com.knowledge.core.vo.DocumentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 文档控制器
 * @author nuts_tian
 */
@Tag(name = "文档管理", description = "文档管理")
@RestController
@RequestMapping("/document")
@RequiredArgsConstructor
public class DocumentController {

    @Resource
    private DocumentService documentService;

    @Operation(summary = "创建文档", description = "创建新的文档")
    @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @PostMapping("/create")
    public Result<DocumentVO> create(
            @Parameter(description = "文档名称", required = true)
            @Valid @RequestBody DocumentCreateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        Long userId = ((UserPrincipal) userDetails).getId();
        DocumentVO vo = documentService.create(dto, userId);
        return Result.success(vo);
    }


    @Operation(summary = "更新文档", description = "更新文档")
    @ApiResponse(responseCode = "200", description = "更新成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @PutMapping("/update")
    public Result<DocumentVO> update (
            @Parameter(description = "文档信息", required = true)
            @Valid @RequestBody DocumentUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = ((UserPrincipal) userDetails).getId();
        DocumentVO vo = documentService.update(dto, userId);
        return Result.success(vo);
    }

    @Operation(summary = "删除文档", description = "删除文档")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete (
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Long userId = ((UserPrincipal) userDetails).getId();
        documentService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取文档详情", description = "根据ID获取文档详情")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/{id}")
    public Result<DocumentVO> getById(
            @Parameter(description = "文档ID", required = true, example = "1")
            @PathVariable Long id) {
        DocumentVO vo = documentService.getById(id);
        return Result.success(vo);
    }

    @Operation(summary = "获取用户文档列表", description = "分页获取当前用户的文档列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/list")
    public Result<List<DocumentVO>> listByUser(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<DocumentVO> list = documentService.listByUser(userId, page, size);
        return Result.success(list);
    }

    @Operation(summary = "按分类查询文档", description = "根据分类查询文档")
    @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/category/{categoryId}")
    public Result<List<DocumentVO>> listByCategory(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long categoryId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<DocumentVO> list = documentService.listByCategory(categoryId, userId);
        return Result.success(list);
    }

    @Operation(summary = "按标签查询文档", description = "根据标签查询文档")
    @ApiResponse(responseCode = "200", description = "查询成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/tag/{tagId}")
    public Result<List<DocumentVO>> listByTag(
            @Parameter(description = "标签ID", required = true, example = "1")
            @PathVariable Long tagId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<DocumentVO> list = documentService.listByTag(tagId, userId);
        return Result.success(list);
    }

    @Operation(summary = "搜索文档", description = "根据关键词搜索文档")
    @ApiResponse(responseCode = "200", description = "搜索成功",
            content = @Content(schema = @Schema(implementation = DocumentVO.class)))
    @GetMapping("/search")
    public Result<List<DocumentVO>> search(
            @Parameter(description = "搜索关键词", required = true, example = "Spring")
            @RequestParam String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<DocumentVO> list = documentService.search(keyword, userId);
        return Result.success(list);
    }

}
