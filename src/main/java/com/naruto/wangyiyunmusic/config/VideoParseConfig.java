package com.naruto.wangyiyunmusic.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

/**
 * 视频解析配置类
 *
 * <p>处理视频解析相关配置，支持跨平台自动检测</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "video.parser")
@Data
public class VideoParseConfig {

    /**
     * yt-dlp可执行文件路径
     */
    private String ytDlpPath;

    /**
     * 音频格式（mp3/aac/ogg等）
     */
    private String audioFormat;

    /**
     * 音频质量（0=最佳，9=最差）
     */
    private Integer audioQuality;

    /**
     * 单文件大小限制
     */
    private String maxFileSize;

    /**
     * 临时文件存储路径
     */
    private String tempPath;

    /**
     * 临时文件保留时间（小时）
     */
    private Integer tempFileRetentionHours;

    /**
     * 是否启用自动清理
     */
    private Boolean enableAutoCleanup;

    /**
     * 清理任务cron表达式
     */
    private String cleanupCron;

    /**
     * 总存储容量限制（如：2GB）
     */
    private String totalCapacity;

    /**
     * 下载超时时间（秒）
     */
    private Integer timeout;

    /**
     * 服务器基础URL
     */
    private String serverBaseUrl;

    /**
     * 临时音频访问URL前缀
     */
    private String tempAudioUrlPrefix;

    /**
     * 支持的平台列表
     */
    private List<String> supportedPlatforms;

    /**
     * 单文件大小限制（字节）- 缓存值
     */
    private Long maxFileSizeBytes;

    /**
     * 总存储容量限制（字节）- 缓存值
     */
    private Long totalCapacityBytes;

    /**
     * 初始化配置，自动检测操作系统并配置yt-dlp路径
     */
    @PostConstruct
    public void init() {
        // 如果未配置路径，自动检测操作系统
        if (ytDlpPath == null || ytDlpPath.isEmpty()) {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                // Windows环境
                ytDlpPath = System.getProperty("user.dir") + "/tools/yt-dlp.exe";
                log.info("🖥️ 检测到Windows环境，使用路径：{}", ytDlpPath);
            } else {
                // Linux/Mac环境
                ytDlpPath = "/usr/local/bin/yt-dlp";

                // 如果系统路径不存在，尝试项目目录
                if (!new File(ytDlpPath).exists()) {
                    ytDlpPath = System.getProperty("user.dir") + "/tools/yt-dlp";
                    log.info("🐧 检测到Linux/Mac环境，使用项目目录：{}", ytDlpPath);
                } else {
                    log.info("🐧 检测到Linux/Mac环境，使用系统路径：{}", ytDlpPath);
                }
            }
        }

        // 验证yt-dlp文件是否存在
        File ytDlpFile = new File(ytDlpPath);
        if (!ytDlpFile.exists()) {
            log.warn("⚠️ yt-dlp工具未找到，路径：{}", ytDlpPath);
            log.warn("⚠️ 请下载yt-dlp并放置到正确位置：https://github.com/yt-dlp/yt-dlp/releases");
        } else {
            log.info("✅ yt-dlp工具路径：{}", ytDlpPath);
        }

        // 验证临时目录
        File tempDir = new File(tempPath);
        if (!tempDir.exists()) {
            log.warn("⚠️ 临时目录不存在，正在创建：{}", tempPath);
            if (tempDir.mkdirs()) {
                log.info("✅ 临时目录创建成功：{}", tempPath);
            } else {
                log.error("❌ 临时目录创建失败：{}", tempPath);
            }
        } else {
            log.info("✅ 临时目录已存在：{}", tempPath);
        }

        // 解析文件大小和容量字符串为字节数
        this.maxFileSizeBytes = parseSize(maxFileSize);
        this.totalCapacityBytes = parseSize(totalCapacity);

        // 输出配置信息
        log.info("📋 视频解析配置加载完成：");
        log.info("  - 音频格式：{}", audioFormat);
        log.info("  - 音频质量：{}", audioQuality);
        log.info("  - 文件大小限制：{} ({} MB)", maxFileSize, maxFileSizeBytes / 1024 / 1024);
        log.info("  - 临时文件保留时间：{}小时", tempFileRetentionHours);
        log.info("  - 自动清理：{}", enableAutoCleanup ? "启用" : "禁用");
        log.info("  - 存储容量限制：{} ({} MB)", totalCapacity, totalCapacityBytes / 1024 / 1024);
        log.info("  - 支持平台：{}", supportedPlatforms);
    }

    /**
     * 解析文件大小字符串为字节数
     * <p>支持格式：20M, 2GB, 1024K等</p>
     *
     * @param sizeStr 文件大小字符串
     * @return 字节数
     */
    private long parseSize(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            return 0;
        }

        sizeStr = sizeStr.trim().toUpperCase();
        long multiplier = 1;

        if (sizeStr.endsWith("GB")) {
            multiplier = 1024L * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB") || sizeStr.endsWith("M")) {
            multiplier = 1024L * 1024;
            sizeStr = sizeStr.replaceAll("(MB|M)$", "");
        } else if (sizeStr.endsWith("KB") || sizeStr.endsWith("K")) {
            multiplier = 1024L;
            sizeStr = sizeStr.replaceAll("(KB|K)$", "");
        }

        try {
            return Long.parseLong(sizeStr.trim()) * multiplier;
        } catch (NumberFormatException e) {
            log.error("❌ 无法解析文件大小: {}", sizeStr, e);
            return 0;
        }
    }
}
