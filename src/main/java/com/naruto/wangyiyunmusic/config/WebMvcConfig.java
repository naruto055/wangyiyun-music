package com.naruto.wangyiyunmusic.config;

import com.naruto.wangyiyunmusic.config.properties.CorsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
     * 临时文件存储路径
     */
    @Value("${video.parser.temp-path}")
    private String tempFilePath;

    /**
     * 临时音频访问URL前缀
     */
    @Value("${video.parser.temp-audio-url-prefix}")
    private String tempAudioUrlPrefix;

    /**
     * CORS 配置属性
     */
    @Autowired
    private CorsProperties corsProperties;

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

        // 临时音频文件静态资源映射（视频解析提取的临时文件）
        // 使用配置的URL前缀（末尾需要 /**）
        registry.addResourceHandler(tempAudioUrlPrefix + "**")
                .addResourceLocations("file:" + tempFilePath)
                .setCachePeriod(600)    // 浏览器缓存10分钟（临时文件）
                .resourceChain(true);   // 启用资源链，支持Range请求
    }

    /**
     * 配置 CORS 跨域资源共享
     *
     * <p>功能：</p>
     * <ul>
     *   <li>允许配置的前端域名跨域访问音频资源</li>
     *   <li>支持 HTTP Range 请求（拖拽播放）</li>
     *   <li>设置预检请求缓存，减少网络开销</li>
     * </ul>
     *
     * <p>配置说明：</p>
     * <ul>
     *   <li>域名白名单在 application.yaml 的 cors.allowed-origins 中配置</li>
     *   <li>生产环境需修改配置文件中的域名为真实前端域名</li>
     *   <li>禁止使用 allowedOrigins("*") 模式</li>
     * </ul>
     *
     * @param registry CORS 注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 从配置文件读取允许的域名列表
        String[] allowedOrigins = corsProperties.getAllowedOrigins().toArray(new String[0]);
        long maxAge = corsProperties.getMaxAge();

        // 音频资源 CORS 配置
        registry.addMapping("/audio/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("Range", "Accept", "Content-Type")
                .exposedHeaders("Content-Length", "Content-Range", "Accept-Ranges")
                .allowCredentials(true)
                .maxAge(maxAge);

        // 临时音频资源 CORS 配置（视频解析提取的临时文件）
        registry.addMapping("/temp-audio/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "HEAD", "OPTIONS")
                .allowedHeaders("Range", "Accept", "Content-Type")
                .exposedHeaders("Content-Length", "Content-Range", "Accept-Ranges")
                .allowCredentials(true)
                .maxAge(maxAge);
    }
}
