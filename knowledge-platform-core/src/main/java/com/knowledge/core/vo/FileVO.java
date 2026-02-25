package com.knowledge.core.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件VO
 * @author nuts_tian
 */
@Data
@Schema(description = "文件信息")
public class FileVO {

    @Schema(description = "文件ID", example = "1")
    private Long id;

    @Schema(description = "原始文件名", example = "example.jpg")
    private String filename;

    @Schema(description = "文件大小（字节）", example = "102400")
    private Long fileSize;

    @Schema(description = "文件大小（格式化）", example = "100KB")
    private String fileSizeFormatted;

    @Schema(description = "MIME类型", example = "image/jpeg")
    private String fileType;

    @Schema(description = "访问URL", example = "http://localhost:9000/knowledge-files/...")
    private String url;

    @Schema(description = "上传时间")
    private LocalDateTime createdAt;

    /**
     * 格式化文件大小
     */
    public String getFileSizeFormatted() {
        if (fileSize == null) {
            return "0B";
        }
        long size = fileSize;
        if (size < 1024) {
            return size + "B";
        }
        if (size < 1024 * 1024) {
            return String.format("%.2fKB", size / 1024.0);
        }
        if (size < 1024 * 1024 * 1024) {
            return String.format("%.2fMB", size / (1024.0 * 1024));
        }
        return String.format("%.2fGB", size / (1024.0 * 1024 * 1024));
    }
}
