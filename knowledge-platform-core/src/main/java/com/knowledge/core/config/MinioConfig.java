package com.knowledge.core.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {

    /**
     * MinIO 服务地址（阿里云服务器）
     */
    private String endpoint = "http://8.147.131.165:9000";

    /**
     * 访问密钥
     */
    private String accessKey = "minioadmin";

    /**
     * 秘密密钥
     */
    private String secretKey = "minioadmin";

    /**
     * 存储桶名称
     */
    private String bucket = "knowledge-files";

    /**
     * 创建 MinIO 客户端
     */
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}