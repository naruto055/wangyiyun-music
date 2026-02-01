package com.naruto.wangyiyunmusic.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CORS 跨域配置属性类
 *
 * <p>从 application.yaml 中读取 CORS 相关配置</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Component
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    /**
     * 允许的前端域名列表
     */
    private List<String> allowedOrigins;

    /**
     * 预检请求缓存时间（秒）
     */
    private Long maxAge = 3600L;
}
