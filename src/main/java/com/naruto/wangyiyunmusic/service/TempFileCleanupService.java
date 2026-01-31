package com.naruto.wangyiyunmusic.service;

/**
 * 临时文件清理服务接口
 *
 * <p>负责定时清理临时目录中的过期文件</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public interface TempFileCleanupService {

    /**
     * 清理过期的临时文件
     *
     * @return 清理的文件数量
     */
    int cleanupExpiredFiles();

    /**
     * 手动清理所有临时文件
     *
     * @return 清理的文件数量
     */
    int cleanupAllFiles();
}
