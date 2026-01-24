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
 * 音乐标签关联表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("music_tag")
public class MusicTag implements Serializable {

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
     * 标签ID
     */
    @TableField("tag_id")
    private Long tagId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
