package com.naruto.wangyiyunmusic.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 *
 * <p>功能：</p>
 * <ul>
 *   <li>配置静态资源处理</li>
 *   <li>启用 Swagger UI 静态资源访问</li>
 *   <li>配置音频文件静态资源映射</li>
 * </ul>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-26
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 音频文件存储路径
     */
    @Value("${audio.storage-path}")
    private String audioStoragePath;

    /**
     * 配置静态资源处理
     *
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Swagger UI 静态资源映射
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springdoc-openapi/");

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");

        // 音频文件静态资源映射（支持HTTP Range请求，实现拖拽播放）
        registry.addResourceHandler("/audio/**")
                .addResourceLocations(audioStoragePath)
                .setCachePeriod(3600)  // 浏览器缓存1小时，提升性能
                .resourceChain(true);   // 启用资源链，支持Range请求
    }
}
