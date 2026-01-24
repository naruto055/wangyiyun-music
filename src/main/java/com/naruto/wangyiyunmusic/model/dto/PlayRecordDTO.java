package com.naruto.wangyiyunmusic.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 播放记录请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
public class PlayRecordDTO {
    /**
     * 音乐ID
     */
    @NotNull(message = "音乐ID不能为空")
    private Long musicId;

    /**
     * 播放时长(秒)
     */
    private Integer playDuration;

    /**
     * 播放来源
     */
    private String playSource = "web";
}
