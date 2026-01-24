package com.naruto.wangyiyunmusic.model.vo;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * 音乐详情视图对象
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
public class MusicDetailVO {
    /**
     * 音乐ID
     */
    private Long id;

    /**
     * 歌曲标题
     */
    private String title;

    /**
     * 时长(秒)
     */
    private Integer duration;

    /**
     * 音乐文件URL
     */
    private String fileUrl;

    /**
     * 封面图URL
     */
    private String coverUrl;

    /**
     * 歌词
     */
    private String lyrics;

    /**
     * 播放次数
     */
    private Long playCount;

    /**
     * 收藏次数
     */
    private Integer favoriteCount;

    /**
     * 发行日期
     */
    private LocalDate releaseDate;

    /**
     * 专辑名称
     */
    private String albumName;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 歌手列表
     */
    private List<ArtistVO> artists;

    /**
     * 标签列表
     */
    private List<String> tags;
}
