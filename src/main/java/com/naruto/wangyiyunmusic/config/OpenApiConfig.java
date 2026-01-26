package com.naruto.wangyiyunmusic.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 配置类
 * 用于配置 Swagger 文档的基本信息
 *
 * @Author: naruto
 * @CreateTime: 2026-01-25
 */
@Configuration
public class OpenApiConfig {

    /**
     * 配置 OpenAPI 基本信息
     *
     * @return OpenAPI 对象
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("网易云音乐 API 接口文档")
                        .version("1.0.0")
                        .description("基于 Spring Boot 的网易云音乐后端服务系统 API 文档")
                        .contact(new Contact()
                                .name("naruto")
                                .email("naruto@example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
