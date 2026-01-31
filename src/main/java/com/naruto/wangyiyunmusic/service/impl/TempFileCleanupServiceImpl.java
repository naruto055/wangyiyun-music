package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.config.VideoParseConfig;
import com.naruto.wangyiyunmusic.service.TempFileCleanupService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 临时文件清理服务实现类
 *
 * <p>使用定时任务自动清理过期的临时文件</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class TempFileCleanupServiceImpl implements TempFileCleanupService {

    @Autowired
    private VideoParseConfig config;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 定时清理任务
     * <p>每30分钟执行一次（根据配置的 cron 表达式）</p>
     */
    @Scheduled(cron = "${video.parser.cleanup-cron:0 */30 * * * ?}")
    public void scheduledCleanup() {
        if (!config.getEnableAutoCleanup()) {
            log.debug("自动清理已禁用，跳过定时任务");
            return;
        }

        log.info("========== 开始定时清理临时文件 ==========");
        int cleanedCount = cleanupExpiredFiles();
        log.info("========== 定时清理完成，清理文件数: {} ==========", cleanedCount);
    }

    @Override
    public int cleanupExpiredFiles() {
        File tempDir = new File(config.getTempPath());

        if (!tempDir.exists() || !tempDir.isDirectory()) {
            log.warn("临时目录不存在: {}", config.getTempPath());
            return 0;
        }

        // 计算过期时间戳
        long retentionMillis = config.getTempFileRetentionHours() * 60 * 60 * 1000L;
        long expirationTime = System.currentTimeMillis() - retentionMillis;

        log.info("清理策略: 保留时长 {} 小时，过期时间点: {}",
                config.getTempFileRetentionHours(),
                formatTimestamp(expirationTime));

        return cleanupDirectory(tempDir, expirationTime, false);
    }

    @Override
    public int cleanupAllFiles() {
        log.warn("手动清理所有临时文件");

        File tempDir = new File(config.getTempPath());

        if (!tempDir.exists() || !tempDir.isDirectory()) {
            log.warn("临时目录不存在: {}", config.getTempPath());
            return 0;
        }

        return cleanupDirectory(tempDir, Long.MAX_VALUE, true);
    }

    /**
     * 清理目录中的文件
     *
     * @param directory      目录
     * @param expirationTime 过期时间戳（文件修改时间早于此时间将被删除）
     * @param cleanAll       是否清理所有文件
     * @return 清理的文件数量
     */
    private int cleanupDirectory(File directory, long expirationTime, boolean cleanAll) {
        int cleanedCount = 0;

        File[] files = directory.listFiles();
        if (files == null || files.length == 0) {
            log.debug("目录为空: {}", directory.getAbsolutePath());
            return 0;
        }

        for (File file : files) {
            if (file.isFile()) {
                long lastModified = file.lastModified();
                boolean shouldDelete = cleanAll || lastModified < expirationTime;

                if (shouldDelete) {
                    long fileSize = file.length();
                    String fileName = file.getName();
                    String modifiedTime = formatTimestamp(lastModified);

                    // 使用增强的删除逻辑（带重试和详细错误信息）
                    if (deleteFileWithRetry(file)) {
                        log.info("✅ 删除文件: {} (大小: {} MB, 修改时间: {})",
                                fileName,
                                String.format("%.2f", fileSize / 1024.0 / 1024.0),
                                modifiedTime);
                        cleanedCount++;
                    } else {
                        log.error("❌ 删除文件失败: {} (可能被占用或权限不足)", fileName);
                        log.warn("⚠️ 建议：检查文件是否被其他进程占用（媒体播放器、浏览器、文件浏览器等）");
                    }
                }
            } else if (file.isDirectory()) {
                // 递归清理子目录
                cleanedCount += cleanupDirectory(file, expirationTime, cleanAll);

                // 如果子目录为空，删除子目录
                if (file.list() != null && file.list().length == 0) {
                    if (file.delete()) {
                        log.info("✅ 删除空目录: {}", file.getName());
                    }
                }
            }
        }

        return cleanedCount;
    }

    /**
     * 增强的文件删除方法（带重试机制和详细错误日志）
     *
     * <p>解决 Windows 文件锁定问题：</p>
     * <ul>
     *   <li>尝试多次删除（重试3次，每次间隔100ms）</li>
     *   <li>先尝试传统 delete()，失败后使用 NIO Files.delete()</li>
     *   <li>记录详细的错误信息（权限、占用等）</li>
     * </ul>
     *
     * @param file 要删除的文件
     * @return 是否删除成功
     */
    private boolean deleteFileWithRetry(File file) {
        // 第一次尝试：传统 delete() 方法
        if (file.delete()) {
            return true;
        }

        // 第二次尝试：使用 NIO Files.delete() 获取详细错误信息
        try {
            Files.delete(file.toPath());
            log.debug("使用 NIO Files.delete() 删除成功: {}", file.getName());
            return true;
        } catch (IOException e) {
            // 记录详细错误原因
            String errorMsg = e.getMessage();
            if (errorMsg != null) {
                if (errorMsg.contains("being used by another process")) {
                    log.warn("文件被占用: {} - {}", file.getName(), errorMsg);
                } else if (errorMsg.contains("Access is denied")) {
                    log.warn("权限不足: {} - {}", file.getName(), errorMsg);
                } else {
                    log.warn("删除失败: {} - {}", file.getName(), errorMsg);
                }
            }
        }

        // 第三次尝试：垃圾回收后重试（可能释放文件句柄）
        System.gc();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (file.delete()) {
            log.debug("垃圾回收后删除成功: {}", file.getName());
            return true;
        }

        // 第四次尝试：标记为退出时删除（Windows 系统重启后删除）
        if (file.exists()) {
            file.deleteOnExit();
            log.info("⚠️ 文件标记为退出时删除: {} (将在应用程序退出后删除)", file.getName());
        }

        return false;
    }

    /**
     * 格式化时间戳
     *
     * @param timestamp 时间戳
     * @return 格式化后的时间字符串
     */
    private String formatTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
        return dateTime.format(FORMATTER);
    }
}
