package com.naruto.wangyiyunmusic.service;

import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;

/**
 * yt-dlp 工具服务接口
 *
 * <p>负责调用 yt-dlp 工具解析视频并提取音频</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public interface YtDlpService {

    /**
     * 解析视频并提取音频
     *
     * @param videoUrl 视频链接
     * @param platform 平台标识
     * @return yt-dlp 解析结果
     */
    YtDlpResult parseVideoAndExtractAudio(String videoUrl, String platform);

    /**
     * 获取视频元数据（不下载）
     *
     * @param videoUrl 视频链接
     * @return yt-dlp 解析结果
     */
    YtDlpResult getVideoMetadata(String videoUrl);
}
