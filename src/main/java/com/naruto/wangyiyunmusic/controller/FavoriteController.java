package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.entity.Favorite;
import com.naruto.wangyiyunmusic.model.vo.FavoriteVO;
import com.naruto.wangyiyunmusic.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "收藏管理", description = "提供音乐收藏、取消收藏、收藏列表查询等接口")
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
    @Operation(summary = "收藏音乐", description = "将指定音乐添加到用户的收藏列表")
    @PostMapping("/{musicId}")
    public void addFavorite(
            @Parameter(description = "音乐ID", example = "1", required = true)
            @PathVariable Long musicId) {
        favoriteService.addFavorite(musicId);
    }

    /**
     * 取消收藏
     *
     * @param musicId 音乐ID
     * @return 成功响应
     */
    @Operation(summary = "取消收藏", description = "从用户的收藏列表中移除指定音乐")
    @DeleteMapping("/{musicId}")
    public void removeFavorite(
            @Parameter(description = "音乐ID", example = "1", required = true)
            @PathVariable Long musicId) {
        favoriteService.removeFavorite(musicId);
    }

    /**
     * 查询收藏列表
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 收藏列表（包含音乐详细信息）
     */
    @Operation(summary = "查询收藏列表", description = "分页查询用户的音乐收藏列表，返回包含歌手、封面、专辑等音乐详情")
    @GetMapping("/list")
    public IPage<FavoriteVO> getFavoriteList(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        return favoriteService.getFavoriteList(0L, pageNum, pageSize);
    }
}
