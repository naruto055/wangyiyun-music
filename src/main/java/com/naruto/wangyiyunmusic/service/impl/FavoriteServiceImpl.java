package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.model.entity.Favorite;
import com.naruto.wangyiyunmusic.mapper.FavoriteMapper;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.service.MusicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 收藏表 服务实现类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Service
public class FavoriteServiceImpl extends ServiceImpl<FavoriteMapper, Favorite> implements FavoriteService {

    @Autowired
    private MusicService musicService;

    @Override
    @Transactional
    public void addFavorite(Long musicId) {
        Long userId = 0L; // 暂时默认0

        // 验证音乐是否存在
        Music music = musicService.getById(musicId);
        if (music == null) {
            throw new BusinessException("音乐不存在");
        }

        // 检查是否已收藏
        Favorite existing = this.getOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMusicId, musicId)
        );

        if (existing != null && existing.getIsDeleted() == 0) {
            throw new BusinessException("已收藏该音乐");
        }

        if (existing != null) {
            // 恢复收藏
            existing.setIsDeleted(0);
            this.updateById(existing);
        } else {
            // 新增收藏
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setMusicId(musicId);
            this.save(favorite);
        }

        // 更新音乐收藏次数（复用已查询的 music 对象）
        music.setFavoriteCount(music.getFavoriteCount() + 1);
        musicService.updateById(music);
    }

    @Override
    @Transactional
    public void removeFavorite(Long musicId) {
        Long userId = 0L; // 暂时默认0

        Favorite favorite = this.getOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMusicId, musicId)
        );

        if (favorite == null || favorite.getIsDeleted() == 1) {
            throw new BusinessException("未收藏该音乐");
        }

        // 逻辑删除
        favorite.setIsDeleted(1);
        this.updateById(favorite);

        // 更新音乐收藏次数
        Music music = musicService.getById(musicId);
        if (music != null && music.getFavoriteCount() > 0) {
            music.setFavoriteCount(music.getFavoriteCount() - 1);
            musicService.updateById(music);
        }
    }

    @Override
    public IPage<Favorite> getFavoriteList(Long userId, Integer pageNum, Integer pageSize) {
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getIsDeleted, 0)
                .orderByDesc(Favorite::getCreateTime);
        return this.page(page, wrapper);
    }
}
