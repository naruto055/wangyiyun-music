package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 音乐列表视图对象
 *
 * <p>用于音乐列表展示，包含歌手名称等关键信息</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-26
 */
@Data
@Schema(description = "音乐列表信息")
public class MusicListVO {

    /**
     * 音乐ID
     */
    @Schema(description = "音乐ID", example = "1")
    private Long id;

    /**
     * 歌曲标题
     */
    @Schema(description = "歌曲标题", example = "七里香")
    private String title;

    /**
     * 专辑ID
     */
    @Schema(description = "专辑ID", example = "1")
    private Long albumId;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 时长(秒)
     */
    @Schema(description = "时长(秒)", example = "299")
    private Integer duration;

    /**
     * 音乐文件URL
     */
    @Schema(description = "音乐文件URL", example = "https://music.example.com/1.mp3")
    private String fileUrl;

    /**
     * 封面图URL
     */
    @Schema(description = "封面图URL", example = "https://cover.example.com/1.jpg")
    private String coverUrl;

    /**
     * 歌词
     */
    @Schema(description = "歌词", example = "窗外的麻雀...")
    private String lyrics;

    /**
     * 播放次数
     */
    @Schema(description = "播放次数", example = "1000000")
    private Long playCount;

    /**
     * 收藏次数
     */
    @Schema(description = "收藏次数", example = "5000")
    private Integer favoriteCount;

    /**
     * 发行日期
     */
    @Schema(description = "发行日期", example = "2024-09-28")
    private LocalDate releaseDate;

    /**
     * 歌手名称（多个歌手用 "/" 分隔）
     */
    @Schema(description = "歌手名称", example = "周杰伦")
    private String artistNames;

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
