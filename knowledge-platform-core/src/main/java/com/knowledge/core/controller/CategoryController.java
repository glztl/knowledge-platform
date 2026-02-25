package com.knowledge.core.controller;

import com.knowledge.core.common.exception.BusinessException;
import com.knowledge.core.common.result.Result;
import com.knowledge.core.entity.CategoryEntity;
import com.knowledge.core.entity.UserPrincipal;
import com.knowledge.core.service.CategoryService;
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
 * 分类控制器
 */
@Tag(name = "📁 分类管理", description = "文档分类的增删改查")
@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "获取分类树", description = "获取当前用户的所有分类（树形结构）")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = CategoryEntity.class)))
    @GetMapping("/tree")
    public Result<List<CategoryEntity>> getTree(
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        List<CategoryEntity> tree = categoryService.listByUser(userId);
        return Result.success(tree);
    }

    @Operation(summary = "创建分类", description = "创建新的分类")
    @ApiResponse(responseCode = "200", description = "创建成功",
            content = @Content(schema = @Schema(implementation = CategoryEntity.class)))
    @PostMapping
    public Result<CategoryEntity> create(
            @Parameter(description = "分类名称", required = true, example = "技术笔记")
            @NotBlank(message = "分类名称不能为空") @RequestParam String name,

            @Parameter(description = "父分类ID，0表示根分类", example = "0")
            @RequestParam(defaultValue = "0") Long parentId,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        CategoryEntity category = categoryService.create(name, parentId, userId);
        return Result.success(category);
    }

    @Operation(summary = "更新分类", description = "更新分类名称或父分类")
    @ApiResponse(responseCode = "200", description = "更新成功")
    @PutMapping("/{id}")
    public Result<Void> update(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long id,

            @Parameter(description = "分类名称", example = "Java笔记")
            @RequestParam(required = false) String name,

            @Parameter(description = "父分类ID", example = "2")
            @RequestParam(required = false) Long parentId,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();

        // 1. 查询分类
        CategoryEntity category = categoryService.getById(id);
        if (category == null || !category.getUserId().equals(userId)) {
            throw new BusinessException(404, "分类不存在");
        }

        // 2. 更新字段
        if (name != null) {
            category.setName(name);
        }
        if (parentId != null) {
            category.setParentId(parentId);
        }

        // 3. 保存
        categoryService.updateById(category);
        return Result.success();
    }

    @Operation(summary = "删除分类", description = "删除分类（子分类会移动到根分类）")
    @ApiResponse(responseCode = "200", description = "删除成功")
    @DeleteMapping("/{id}")
    public Result<Void> delete(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long id,

            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = ((UserPrincipal) userDetails).getId();
        categoryService.delete(id, userId);
        return Result.success();
    }

    @Operation(summary = "获取分类详情", description = "根据ID获取分类详情")
    @ApiResponse(responseCode = "200", description = "获取成功",
            content = @Content(schema = @Schema(implementation = CategoryEntity.class)))
    @GetMapping("/{id}")
    public Result<CategoryEntity> getById(
            @Parameter(description = "分类ID", required = true, example = "1")
            @PathVariable Long id) {
        CategoryEntity category = categoryService.getById(id);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return Result.success(category);
    }
}