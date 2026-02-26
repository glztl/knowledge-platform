package com.knowledge.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档VO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文档信息")
public class DocumentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;


    @Schema(description = "文档ID", example = "1")
    private Long id;

    @Schema(description = "文档所有者ID")
    private Long userId;

    @Schema(description = "文档标题", example = "Spring Boot 快速入门")
    private String title;

    @Schema(description = "文档内容（Markdown）")
    private String content;

    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    @Schema(description = "分类名称", example = "技术笔记")
    private String categoryName;

    @Schema(description = "标签列表")
    private List<String> tagNames;

    @Schema(description = "是否公开：0-私有，1-公开", example = "0")
    private Integer isPublic;

    @Schema(description = "浏览次数", example = "10")
    private Integer viewCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
