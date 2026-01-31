package com.naruto.wangyiyunmusic.service.strategy;

import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;
import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;

/**
 * 视频平台解析策略接口
 *
 * <p>定义不同平台的视频解析策略，支持多平台扩展</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public interface VideoPlatformStrategy {

    /**
     * 获取支持的平台类型
     *
     * @return 平台类型
     */
    VideoPlatform getSupportedPlatform();

    /**
     * 解析视频并提取音频
     *
     * @param videoUrl 视频链接
     * @return 解析结果
     */
    YtDlpResult parseVideo(String videoUrl);

    /**
     * 验证视频URL格式
     *
     * @param videoUrl 视频链接
     * @return 是否有效
     */
    boolean validateVideoUrl(String videoUrl);

    /**
     * 提取视频ID
     *
     * @param videoUrl 视频链接
     * @return 视频ID
     */
    String extractVideoId(String videoUrl);
}
