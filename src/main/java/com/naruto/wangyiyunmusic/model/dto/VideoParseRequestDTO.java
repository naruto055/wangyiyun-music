package com.naruto.wangyiyunmusic.model.dto;

import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 视频解析请求DTO
 *
 * <p>用于接收前端视频解析请求参数</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Data
@Schema(description = "视频解析请求参数")
public class VideoParseRequestDTO {

    /**
     * 视频链接（完整URL）
     */
    @NotBlank(message = "视频链接不能为空")
    @Schema(description = "视频链接", example = "https://www.bilibili.com/video/BV1Yv6EBkEJ3")
    private String videoUrl;

    /**
     * 平台类型（大写枚举）
     */
    @NotNull(message = "平台类型不能为空")
    @Schema(description = "平台类型（必须大写）", example = "BILIBILI", allowableValues = {"BILIBILI", "YOUTUBE", "DOUYIN"})
    private VideoPlatform platform;

    /**
     * 音频格式（预留字段，默认mp3）
     */
    @Schema(description = "音频格式（可选，默认mp3）", example = "mp3")
    private String audioFormat;
}
