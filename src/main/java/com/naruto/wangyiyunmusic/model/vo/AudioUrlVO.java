package com.naruto.wangyiyunmusic.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 音频URL响应对象
 *
 * <p>用于返回音频文件的访问URL信息</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-28
 */
@Data
@Schema(description = "音频URL信息")
public class AudioUrlVO {

    /**
     * 音频访问URL
     */
    @Schema(description = "音频访问URL（可直接用于<audio>标签的src属性）",
            example = "http://localhost:8910/audio/jay/晴天.mp3")
    private String audioUrl;

    /**
     * 音乐ID
     */
    @Schema(description = "音乐ID", example = "1")
    private Long musicId;

    /**
     * 音乐标题
     */
    @Schema(description = "音乐标题", example = "晴天")
    private String title;

    /**
     * 文件名
     */
    @Schema(description = "音频文件名", example = "晴天.mp3")
    private String fileName;

    /**
     * 时长(秒)
     */
    @Schema(description = "音乐时长(秒)", example = "269")
    private Integer duration;
}
