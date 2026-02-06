package com.naruto.wangyiyunmusic.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate 配置类
 *
 * <p>配置 HTTP 客户端,用于调用 B 站等外部 API</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Configuration
public class RestTemplateConfig {

    /**
     * 配置 RestTemplate Bean
     *
     * @param builder RestTemplateBuilder
     * @return RestTemplate 实例
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                // 设置连接超时时间（10秒）
                .setConnectTimeout(Duration.ofSeconds(10))
                // 设置读取超时时间（30秒）
                .setReadTimeout(Duration.ofSeconds(30))
                // 自定义请求工厂
                .requestFactory(SimpleClientHttpRequestFactory.class)
                .build();
    }
}
