package com.knowledge.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（开发环境）
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 授权规则 - 关键：放行所有测试和 Swagger 路径
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                "/test/**",                // 测试接口
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/doc.html",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()
                        .anyRequest().authenticated()  // 其他请求需要认证
                )

                // 3. 禁用表单登录（避免自动跳转到 /login）
                .formLogin(AbstractHttpConfigurer::disable)

                // 4. 禁用 HTTP Basic（避免浏览器弹出认证框）
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}