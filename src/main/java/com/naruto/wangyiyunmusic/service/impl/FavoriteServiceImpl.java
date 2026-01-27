package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.model.entity.Favorite;
import com.naruto.wangyiyunmusic.mapper.FavoriteMapper;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.vo.FavoriteVO;
import com.naruto.wangyiyunmusic.service.FavoriteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.service.MusicService;
import com.naruto.wangyiyunmusic.service.ArtistNameService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Autowired
    private ArtistNameService artistNameService;

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

        // 检查收藏是否存在
        Favorite favorite = this.getOne(
                new LambdaQueryWrapper<Favorite>()
                        .eq(Favorite::getUserId, userId)
                        .eq(Favorite::getMusicId, musicId)
        );

        if (favorite == null || favorite.getIsDeleted() == 1) {
            throw new BusinessException("未收藏该音乐");
        }

        this.removeById(favorite.getId());

        // 更新音乐收藏次数
        Music music = musicService.getById(musicId);
        if (music != null && music.getFavoriteCount() > 0) {
            music.setFavoriteCount(music.getFavoriteCount() - 1);
            musicService.updateById(music);
        }
    }

    @Override
    public IPage<FavoriteVO> getFavoriteList(Long userId, Integer pageNum, Integer pageSize) {
        // 1. 查询收藏分页数据
        Page<Favorite> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Favorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Favorite::getUserId, userId)
                .eq(Favorite::getIsDeleted, 0)
                .orderByDesc(Favorite::getCreateTime);
        IPage<Favorite> favoritePage = this.page(page, wrapper);

        // 2. 如果没有收藏记录，直接返回空结果
        if (favoritePage.getRecords().isEmpty()) {
            Page<FavoriteVO> emptyResult = new Page<>(pageNum, pageSize, 0);
            emptyResult.setRecords(List.of());
            return emptyResult;
        }

        // 3. 提取所有音乐ID
        List<Long> musicIds = favoritePage.getRecords().stream()
                .map(Favorite::getMusicId)
                .collect(Collectors.toList());

        // 4. 批量查询音乐信息
        List<Music> musicList = musicService.listByIds(musicIds);
        Map<Long, Music> musicMap = musicList.stream()
                .collect(Collectors.toMap(Music::getId, Function.identity()));

        // 5. 转换为 FavoriteVO 列表
        List<FavoriteVO> favoriteVOList = favoritePage.getRecords().stream()
                .map(favorite -> {
                    Music music = musicMap.get(favorite.getMusicId());
                    if (music == null) {
                        return null;
                    }

                    // 5.1 复制音乐基本信息到 FavoriteVO
                    FavoriteVO vo = new FavoriteVO();
                    BeanUtils.copyProperties(music, vo);

                    // 5.2 设置收藏特有信息
                    vo.setFavoriteId(favorite.getId());
                    vo.setFavoriteTime(favorite.getCreateTime());

                    return vo;
                })
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        // 6. 批量填充歌手名称（使用公共服务避免 N+1 问题）
        artistNameService.fillArtistNames(favoriteVOList);

        // 7. 构造返回结果
        Page<FavoriteVO> result = new Page<>(favoritePage.getCurrent(), favoritePage.getSize(), favoritePage.getTotal());
        result.setRecords(favoriteVOList);
        return result;
    }
}
