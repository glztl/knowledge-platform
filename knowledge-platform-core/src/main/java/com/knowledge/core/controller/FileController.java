package com.knowledge.core.controller;

import com.knowledge.core.common.result.Result;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.FileService;
import com.knowledge.core.vo.FileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * 文件控制器
 */
@Tag(name = "📁 文件管理", description = "文件上传、下载、删除")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件", description = "上传单个文件到 MinIO")
    @ApiResponse(responseCode = "200", description = "上传成功",
            content = @Content(schema = @Schema(implementation = FileVO.class)))
    @PostMapping("/upload")
    public Result<FileVO> upload(
            @Parameter(description = "文件", required = true)
            @RequestParam("file") MultipartFile file,

            @Parameter(description = "文件类型：image-图片, document-文档, other-其他", example = "image")
            @RequestParam(defaultValue = "other") String type,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        FileVO vo = fileService.upload(file, type, userId);
        return Result.success(vo);
    }

    @Operation(summary = "批量上传文件", description = "批量上传文件到 MinIO")
    @ApiResponse(responseCode = "200", description = "上传成功",
            content = @Content(schema = @Schema(implementation = FileVO.class)))
    @PostMapping("/upload/batch")
    public Result<List<FileVO>> uploadBatch(
            @Parameter(description = "文件列表", required = true)
            @RequestParam("files") MultipartFile[] files,

            @Parameter(description = "文件类型", example = "image")
            @RequestParam(defaultValue = "other") String type,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<FileVO> list = fileService.uploadBatch(Arrays.asList(files), type, userId);
        return Result.success(list);
    }

    @Operation(summary = "删除文件", description = "删除文件")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long id,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        fileService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取文件详情", description = "根据ID获取文件详情")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = FileVO.class)))
    @GetMapping("/{id}")
    public Result<FileVO> getById(
            @Parameter(description = "文件ID", required = true, example = "1")
            @PathVariable Long id) {
        FileVO vo = fileService.getById(id);
        return Result.success(vo);
    }

    @Operation(summary = "获取用户文件列表", description = "分页获取当前用户的文件列表")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = FileVO.class)))
    @GetMapping("/list")
    public Result<List<FileVO>> listByUser(
            @Parameter(description = "文件类型：image/document/other/all", example = "image")
            @RequestParam(required = false) String type,

            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页数量", example = "10")
            @RequestParam(defaultValue = "10") Integer size,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<FileVO> list = fileService.listByUser(userId, type, page, size);
        return Result.success(list);
    }
}