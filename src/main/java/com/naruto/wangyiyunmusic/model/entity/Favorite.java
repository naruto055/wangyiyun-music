package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 收藏表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("favorite")
public class Favorite implements Serializable {

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
     * 逻辑删除(0-收藏中 1-已取消)
     */
    @TableField("is_deleted")
    @TableLogic
    private Integer isDeleted;

    /**
     * 收藏时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
}
