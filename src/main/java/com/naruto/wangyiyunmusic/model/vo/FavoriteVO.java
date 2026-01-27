package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 收藏音乐视图对象
 *
 * <p>包含收藏信息 + 音乐列表详细信息</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "收藏音乐信息")
public class FavoriteVO extends MusicListVO {

    /**
     * 收藏ID
     */
    @Schema(description = "收藏ID", example = "1")
    private Long favoriteId;

    /**
     * 收藏时间
     */
    @Schema(description = "收藏时间", example = "2026-01-27 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime favoriteTime;
}
