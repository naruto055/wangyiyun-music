package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.entity.Favorite;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 收藏表 服务类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
public interface FavoriteService extends IService<Favorite> {

    /**
     * 收藏音乐
     *
     * @param musicId 音乐ID
     */
    void addFavorite(Long musicId);

    /**
     * 取消收藏
     *
     * @param musicId 音乐ID
     */
    void removeFavorite(Long musicId);

    /**
     * 查询收藏列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    IPage<Favorite> getFavoriteList(Long userId, Integer pageNum, Integer pageSize);
}
