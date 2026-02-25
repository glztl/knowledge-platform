package com.knowledge.core.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传DTO
 * @author: nuts_tian
 */
@Data
@Schema(description = "文件上传请求")
public class FileUploadDTO {

    @Schema(description = "文件", requiredMode = Schema.RequiredMode.REQUIRED)
    private MultipartFile file;

    @Schema(description = "文件类型：image-图片, document-文档, other-其他", example = "image")
    private String type = "other";
}