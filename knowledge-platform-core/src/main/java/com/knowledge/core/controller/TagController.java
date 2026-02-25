package com.knowledge.core.controller;

import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.Result;
import com.knowledge.core.entity.TagEntity;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.TagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 * @author nuts_tian
 */
@Tag(name = "🏷️ 标签管理", description = "文档标签的增删改查")
@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @Operation(summary = "获取标签列表", description = "获取当前用户的所有标签")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = TagEntity.class)))
    @GetMapping("/list")
    public Result<List<TagEntity>> list(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<TagEntity> tags = tagService.listByUser(userId);
        return Result.success(tags);
    }

    @Operation(summary = "创建标签", description = "创建新的标签（如果已存在则返回已有标签）")
    @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = TagEntity.class)))
    @PostMapping
    public Result<TagEntity> create(
            @Parameter(description = "标签名称", required = true, example = "Java")
            @NotBlank(message = "标签名称不能为空") @RequestParam String name,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        TagEntity tag = tagService.create(name, userId);
        return Result.success(tag);
    }

    @Operation(summary = "删除标签", description = "删除标签（同时删除关联的文档标签关系）")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "标签ID", required = true, example = "1")
            @PathVariable Long id,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        tagService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取标签详情", description = "根据ID获取标签详情")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = TagEntity.class)))
    @GetMapping("/{id}")
    public Result<TagEntity> getById(
            @Parameter(description = "标签ID", required = true, example = "1")
            @PathVariable Long id) {
        TagEntity tag = tagService.getById(id);
        if (tag == null) {
            throw new BusinessException(404, "标签不存在");
        }
        return Result.success(tag);
    }
}
