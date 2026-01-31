package com.naruto.wangyiyunmusic.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 视频解析结果VO
 *
 * <p>返回给前端的视频解析结果数据</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Data
@Schema(description = "视频解析结果")
public class VideoParseResultVO {

    /**
     * 音乐ID
     */
    @Schema(description = "音乐ID", example = "123")
    private Long musicId;

    /**
     * 歌曲标题（从视频标题提取）
     */
    @Schema(description = "歌曲标题", example = "【泰拉瑞亚】1.4.5大师模式#1")
    private String title;

    /**
     * 音频访问URL（临时URL，1小时后失效）
     */
    @Schema(description = "音频访问URL", example = "http://localhost:8910/temp-audio/BV1Yv6EBkEJ3.mp3")
    private String audioUrl;

    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL", example = "https://i2.hdslb.com/bfs/archive/xxx.jpg")
    private String coverUrl;

    /**
     * 时长（秒）
     */
    @Schema(description = "时长（秒）", example = "1234")
    private Integer duration;

    /**
     * 文件大小（字节）
     */
    @Schema(description = "文件大小（字节）", example = "15728640")
    private Long fileSize;

    /**
     * 音频格式
     */
    @Schema(description = "音频格式", example = "mp3")
    private String audioFormat;

    /**
     * 原始视频ID（如BV号）
     */
    @Schema(description = "原始视频ID", example = "BV1Yv6EBkEJ3")
    private String sourceVideoId;
}
