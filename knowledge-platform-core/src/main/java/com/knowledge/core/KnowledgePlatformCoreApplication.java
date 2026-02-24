package com.knowledge.core;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author: nuts_tian
 */
@MapperScan("com.knowledge.core.mapper")
@SpringBootApplication
public class KnowledgePlatformCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(KnowledgePlatformCoreApplication.class, args);
    }
}
