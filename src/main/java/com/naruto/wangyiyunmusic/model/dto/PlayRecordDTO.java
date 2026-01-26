package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 播放记录请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
@Schema(description = "播放记录信息")
public class PlayRecordDTO {
    /**
     * 音乐ID
     */
    @Schema(description = "音乐ID", example = "1", required = true)
    @NotNull(message = "音乐ID不能为空")
    private Long musicId;

    /**
     * 播放时长(秒)
     */
    @Schema(description = "播放时长（秒）", example = "180")
    private Integer playDuration;

    /**
     * 播放来源
     */
    @Schema(description = "播放来源", example = "web")
    private String playSource = "web";
}
