package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 播放记录表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("play_record")
public class PlayRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 音乐ID
     */
    @TableField("music_id")
    private Long musicId;

    /**
     * 用户ID(暂时默认0,后续扩展)
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 播放时长(秒)
     */
    @TableField("play_duration")
    private Integer playDuration;

    /**
     * 播放来源(app/web/h5)
     */
    @TableField("play_source")
    private String playSource;

    /**
     * 播放时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
