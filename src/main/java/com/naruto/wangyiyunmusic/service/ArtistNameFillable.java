package com.naruto.wangyiyunmusic.service;

/**
 * 歌手名称填充接口
 *
 * <p>用于统一处理需要填充歌手名称的 VO 对象</p>
 * <p>实现该接口的类可以使用 {@link ArtistNameService} 批量填充歌手名称</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-27
 */
public interface ArtistNameFillable {

    /**
     * 获取音乐ID
     *
     * @return 音乐ID
     */
    Long getId();

    /**
     * 设置歌手名称
     *
     * @param artistNames 歌手名称（多个歌手用"/"分隔）
     */
    void setArtistNames(String artistNames);
}
