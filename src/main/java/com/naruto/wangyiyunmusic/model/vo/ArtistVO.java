package com.naruto.wangyiyunmusic.model.vo;

import lombok.Data;

/**
 * 歌手视图对象
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
public class ArtistVO {
    /**
     * 歌手ID
     */
    private Long id;

    /**
     * 歌手名称
     */
    private String name;

    /**
     * 角色(singer/composer/lyricist)
     */
    private String role;
}
