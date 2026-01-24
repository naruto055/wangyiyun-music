package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 专辑表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("album")
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 专辑名称
     */
    @TableField("name")
    private String name;

    /**
     * 封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 专辑简介
     */
    @TableField("description")
    private String description;

    /**
     * 发行日期
     */
    @TableField("release_date")
    private LocalDate releaseDate;

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
