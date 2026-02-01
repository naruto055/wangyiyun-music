package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专辑列表视图对象
 *
 * <p>用于专辑列表展示，包含基本信息和歌曲数量</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "专辑列表信息")
public class AlbumListVO {

    /**
     * 专辑ID
     */
    @Schema(description = "专辑ID", example = "1")
    private Long id;

    /**
     * 专辑名称
     */
    @Schema(description = "专辑名称", example = "范特西")
    private String name;

    /**
     * 封面URL
     */
    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    /**
     * 发行日期
     */
    @Schema(description = "发行日期", example = "2001-09-14")
    private LocalDate releaseDate;

    /**
     * 专辑歌曲数量
     */
    @Schema(description = "专辑歌曲数量", example = "10")
    private Integer songCount;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
