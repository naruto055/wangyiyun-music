package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.entity.Favorite;
import com.naruto.wangyiyunmusic.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 收藏表 前端控制器
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 收藏音乐
     *
     * @param musicId 音乐ID
     * @return 成功响应
     */
    @PostMapping("/{musicId}")
    public Result<Void> addFavorite(@PathVariable Long musicId) {
        favoriteService.addFavorite(musicId);
        return Result.success();
    }

    /**
     * 取消收藏
     *
     * @param musicId 音乐ID
     * @return 成功响应
     */
    @DeleteMapping("/{musicId}")
    public Result<Void> removeFavorite(@PathVariable Long musicId) {
        favoriteService.removeFavorite(musicId);
        return Result.success();
    }

    /**
     * 查询收藏列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表
     */
    @GetMapping("/list")
    public Result<IPage<Favorite>> getFavoriteList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<Favorite> page = favoriteService.getFavoriteList(0L, pageNum, pageSize);
        return Result.success(page);
    }
}
