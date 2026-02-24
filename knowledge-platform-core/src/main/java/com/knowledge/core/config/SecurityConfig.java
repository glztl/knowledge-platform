package com.knowledge.core.config;

import com.knowledge.core.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置
 *
 * @author: nuts_tian
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. 禁用 CSRF（JWT 无状态，不需要 CSRF 保护）
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 授权规则
                .authorizeHttpRequests(authz -> authz
                        // 放行 Swagger 相关路径
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/doc.html",
                                "/webjars/**",
                                "/favicon.ico"
                        ).permitAll()

                        // 放行测试接口
                        .requestMatchers("/test/**").permitAll()

                        // 放行用户注册和登录接口
                        .requestMatchers(
                                "/user/register",
                                "/user/login"
                        ).permitAll()

                        // 其他所有请求需要认证
                        .anyRequest().authenticated()
                )

                // 3. 禁用表单登录（使用 JWT）
                .formLogin(AbstractHttpConfigurer::disable)

                // 4. 禁用 HTTP Basic
                .httpBasic(AbstractHttpConfigurer::disable)

                // 5. 添加 JWT 认证过滤器（在 UsernamePasswordAuthenticationFilter 之前）
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}