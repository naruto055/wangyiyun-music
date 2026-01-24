package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 歌手表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("artist")
public class Artist implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 歌手名称
     */
    @TableField("name")
    private String name;

    /**
     * 头像URL
     */
    @TableField("avatar_url")
    private String avatarUrl;

    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 国家/地区
     */
    @TableField("country")
    private String country;

    /**
     * 逻辑删除
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
