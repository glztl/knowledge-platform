package com.knowledge.core.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Knife4j 增强版 Swagger 配置
 */
@Configuration
public class Knife4jConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("📚 知识管理平台 API")
                        .description("### 个人知识管理平台 API 文档")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("开发者")
                                .email("developer@example.com")
                                .url("https://github.com/yourusername"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
