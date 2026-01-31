package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.config.VideoParseConfig;
import com.naruto.wangyiyunmusic.exception.FileValidationException;
import com.naruto.wangyiyunmusic.exception.StorageCapacityException;
import com.naruto.wangyiyunmusic.service.FileValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * 文件验证服务实现类
 *
 * <p>验证文件大小、格式和存储空间</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class FileValidationServiceImpl implements FileValidationService {

    @Autowired
    private VideoParseConfig config;

    /**
     * 支持的音频格式列表
     */
    private static final List<String> SUPPORTED_AUDIO_FORMATS = Arrays.asList(
            "mp3", "m4a", "opus", "aac", "flac", "wav"
    );

    @Override
    public void validateFileSize(long fileSize) {
        log.debug("验证文件大小: {} 字节", fileSize);

        if (fileSize <= 0) {
            throw new FileValidationException("文件大小无效: " + fileSize);
        }

        long maxFileSize = config.getMaxFileSizeBytes();
        if (fileSize > maxFileSize) {
            String fileSizeMB = String.format("%.2f", fileSize / 1024.0 / 1024.0);
            String maxSizeMB = String.format("%.2f", maxFileSize / 1024.0 / 1024.0);
            throw new FileValidationException(
                    String.format("文件大小超过限制：%s MB，最大允许 %s MB", fileSizeMB, maxSizeMB)
            );
        }

        log.debug("文件大小验证通过");
    }

    @Override
    public void validateAudioFormat(String audioFormat) {
        log.debug("验证音频格式: {}", audioFormat);

        if (audioFormat == null || audioFormat.trim().isEmpty()) {
            throw new FileValidationException("音频格式不能为空");
        }

        String format = audioFormat.toLowerCase().trim();
        if (!SUPPORTED_AUDIO_FORMATS.contains(format)) {
            throw new FileValidationException(
                    String.format("不支持的音频格式: %s，支持的格式: %s",
                            audioFormat, String.join(", ", SUPPORTED_AUDIO_FORMATS))
            );
        }

        log.debug("音频格式验证通过");
    }

    @Override
    public void validateStorageCapacity(long requiredSize) {
        log.debug("验证存储容量，需要: {} 字节", requiredSize);

        // 获取当前已使用空间
        long usedSpace = getTempDirectoryUsedSpace();
        long totalCapacity = config.getTotalCapacityBytes();
        long availableSpace = totalCapacity - usedSpace;

        log.info("存储空间统计 - 总容量: {} MB, 已使用: {} MB, 可用: {} MB, 需要: {} MB",
                totalCapacity / 1024 / 1024,
                usedSpace / 1024 / 1024,
                availableSpace / 1024 / 1024,
                requiredSize / 1024 / 1024);

        if (requiredSize > availableSpace) {
            throw new StorageCapacityException(
                    String.format("存储空间不足，需要 %.2f MB，剩余 %.2f MB",
                            requiredSize / 1024.0 / 1024.0,
                            availableSpace / 1024.0 / 1024.0)
            );
        }

        log.debug("存储容量验证通过");
    }

    @Override
    public long getTempDirectoryUsedSpace() {
        File tempDir = new File(config.getTempPath());

        if (!tempDir.exists() || !tempDir.isDirectory()) {
            log.warn("临时目录不存在: {}", config.getTempPath());
            return 0;
        }

        return calculateDirectorySize(tempDir);
    }

    /**
     * 计算目录大小
     *
     * @param directory 目录
     * @return 目录大小（字节）
     */
    private long calculateDirectorySize(File directory) {
        long size = 0;

        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        }

        for (File file : files) {
            if (file.isFile()) {
                size += file.length();
            } else if (file.isDirectory()) {
                size += calculateDirectorySize(file);
            }
        }

        return size;
    }
}
