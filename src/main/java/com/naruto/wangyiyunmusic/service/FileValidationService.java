package com.naruto.wangyiyunmusic.service;

/**
 * 文件验证服务接口
 *
 * <p>负责验证音频文件的大小、格式和存储空间</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public interface FileValidationService {

    /**
     * 验证单个文件大小
     *
     * @param fileSize 文件大小（字节）
     * @throws com.naruto.wangyiyunmusic.exception.FileValidationException 文件大小超限
     */
    void validateFileSize(long fileSize);

    /**
     * 验证音频格式
     *
     * @param audioFormat 音频格式（如 mp3, m4a, opus）
     * @throws com.naruto.wangyiyunmusic.exception.FileValidationException 格式不支持
     */
    void validateAudioFormat(String audioFormat);

    /**
     * 验证存储容量
     *
     * @param requiredSize 需要的存储空间（字节）
     * @throws com.naruto.wangyiyunmusic.exception.StorageCapacityException 存储空间不足
     */
    void validateStorageCapacity(long requiredSize);

    /**
     * 获取临时目录已使用空间
     *
     * @return 已使用空间（字节）
     */
    long getTempDirectoryUsedSpace();
}
