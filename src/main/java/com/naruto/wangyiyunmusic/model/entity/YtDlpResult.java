package com.naruto.wangyiyunmusic.model.entity;

import lombok.Data;

/**
 * yt-dlp 解析结果实体
 *
 * <p>封装 yt-dlp 工具解析视频后返回的元数据和文件信息</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Data
public class YtDlpResult {

    /**
     * 视频标题
     */
    private String title;

    /**
     * 视频时长（秒）
     */
    private Integer duration;

    /**
     * 封面图URL
     */
    private String thumbnail;

    /**
     * 音频文件路径（本地绝对路径）
     */
    private String audioFilePath;

    /**
     * 音频文件大小（字节）
     */
    private Long fileSize;

    /**
     * 音频格式
     */
    private String audioFormat;

    /**
     * 原始视频ID（如 BV 号）
     */
    private String videoId;

    /**
     * 平台标识
     */
    private String platform;
}
