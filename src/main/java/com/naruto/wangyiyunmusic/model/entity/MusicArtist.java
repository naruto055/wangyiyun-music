package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 音乐歌手关联表
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Getter
@Setter
@TableName("music_artist")
public class MusicArtist implements Serializable {

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
     * 歌手ID
     */
    @TableField("artist_id")
    private Long artistId;

    /**
     * 角色(singer-歌手/composer-作曲/lyricist-作词)
     */
    @TableField("artist_role")
    private String artistRole;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
