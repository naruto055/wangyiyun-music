package com.naruto.wangyiyunmusic.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 音频安全配置属性类
 *
 * <p>绑定 application.yaml 中的音频安全配置</p>
 * <p>包含限流配置、防盗链配置、黑名单配置</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Data
@Component
@ConfigurationProperties(prefix = "audio.security")
public class AudioSecurityProperties {

    /**
     * 限流配置
     */
    private RateLimitConfig rateLimit = new RateLimitConfig();

    /**
     * 防盗链配置
     */
    private AntiLeechConfig antiLeech = new AntiLeechConfig();

    /**
     * 黑名单配置
     */
    private BlacklistConfig blacklist = new BlacklistConfig();

    /**
     * 限流配置内部类
     */
    @Data
    public static class RateLimitConfig {
        /**
         * 是否启用限流
         */
        private Boolean enabled = true;

        /**
         * Range请求限流：次/分钟/IP
         */
        private Integer rangeRequestsPerMinute = 100;

        /**
         * 完整下载限流：次/分钟/IP
         */
        private Integer fullDownloadsPerMinute = 5;

        /**
         * 不同文件数限流：个/分钟/IP
         */
        private Integer uniqueFilesPerMinute = 10;

        /**
         * 并发连接数限制：个/IP
         */
        private Integer maxConcurrentConnections = 3;

        /**
         * 触发黑名单的限流次数阈值
         */
        private Integer blacklistThreshold = 20;

        /**
         * 统计窗口时长（分钟）
         */
        private Integer windowMinutes = 5;
    }

    /**
     * 防盗链配置内部类
     */
    @Data
    public static class AntiLeechConfig {
        /**
         * 是否启用防盗链
         */
        private Boolean enabled = true;

        /**
         * Referer 白名单（域名列表）
         */
        private List<String> allowedReferers = new ArrayList<>();

        /**
         * 是否允许空 Referer
         */
        private Boolean allowEmptyReferer = true;

        /**
         * User-Agent 黑名单（正则表达式列表）
         */
        private List<String> blockedUserAgents = new ArrayList<>();
    }

    /**
     * 黑名单配置内部类
     */
    @Data
    public static class BlacklistConfig {
        /**
         * 黑名单 IP 列表（手动配置）
         */
        private List<String> ips = new ArrayList<>();

        /**
         * 动态黑名单封禁时长（分钟）
         */
        private Integer banDurationMinutes = 30;

        /**
         * 清理过期数据的间隔（分钟）
         */
        private Integer cleanupIntervalMinutes = 10;
    }
}
