package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 音乐表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("music")
public class Music implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌曲标题
     */
    @TableField("title")
    private String title;

    /**
     * 专辑ID
     */
    @TableField("album_id")
    private Long albumId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;

    /**
     * 时长(秒)
     */
    @TableField("duration")
    private Integer duration;

    /**
     * 音乐文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 封面图URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 歌词
     */
    @TableField("lyrics")
    private String lyrics;

    /**
     * 播放次数
     */
    @TableField("play_count")
    private Long playCount;

    /**
     * 收藏次数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;

    /**
     * 发行日期
     */
    @TableField("release_date")
    private LocalDate releaseDate;

    /**
     * 逻辑删除(0-未删除 1-已删除)
     */
    @TableField("is_deleted")
    @TableLogic
    private Byte isDeleted;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
