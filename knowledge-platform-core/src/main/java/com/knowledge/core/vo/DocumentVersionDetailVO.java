package com.knowledge.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文档版本详情VO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文档版本详情")
public class DocumentVersionDetailVO {

    @Schema(description = "版本ID", example = "1")
    private Long id;

    @Schema(description = "文档ID", example = "1")
    private Long documentId;

    @Schema(description = "版本号", example = "3")
    private Integer versionNumber;

    @Schema(description = "文档标题", example = "Spring Boot 快速入门")
    private String title;

    @Schema(description = "文档内容（Markdown）")
    private String content;

    @Schema(description = "分类名称", example = "技术笔记")
    private String categoryName;

    @Schema(description = "变更摘要", example = "添加了依赖注入章节")
    private String changeSummary;

    @Schema(description = "修改人昵称", example = "张三")
    private String modifiedByNickname;

    @Schema(description = "修改时间")
    private LocalDateTime modifiedAt;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
