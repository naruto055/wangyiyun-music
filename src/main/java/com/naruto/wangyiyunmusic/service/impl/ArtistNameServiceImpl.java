package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.naruto.wangyiyunmusic.model.entity.Artist;
import com.naruto.wangyiyunmusic.model.entity.MusicArtist;
import com.naruto.wangyiyunmusic.service.ArtistNameFillable;
import com.naruto.wangyiyunmusic.service.ArtistNameService;
import com.naruto.wangyiyunmusic.service.ArtistService;
import com.naruto.wangyiyunmusic.service.MusicArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 歌手名称填充服务实现类
 *
 * <p>统一处理歌手名称批量填充逻辑，避免代码重复</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-27
 */
@Service
public class ArtistNameServiceImpl implements ArtistNameService {

    /**
     * 歌手名称分隔符
     */
    private static final String ARTIST_NAME_SEPARATOR = "/";

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MusicArtistService musicArtistService;

    @Override
    public <T extends ArtistNameFillable> void fillArtistNames(List<T> voList) {
        if (voList == null || voList.isEmpty()) {
            return;
        }

        // 1. 收集所有音乐ID
        List<Long> musicIds = voList.stream()
                .map(ArtistNameFillable::getId)
                .collect(Collectors.toList());

        // 2. 批量查询音乐-歌手关联关系
        List<MusicArtist> musicArtists = musicArtistService.list(
                new LambdaQueryWrapper<MusicArtist>().in(MusicArtist::getMusicId, musicIds)
        );

        if (musicArtists.isEmpty()) {
            return;
        }

        // 3. 批量查询歌手信息
        List<Long> artistIds = musicArtists.stream()
                .map(MusicArtist::getArtistId)
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Artist> artistMap = artistService.listByIds(artistIds).stream()
                .collect(Collectors.toMap(Artist::getId, Function.identity()));

        // 4. 按音乐ID分组并拼接歌手名称
        Map<Long, String> musicArtistNamesMap = musicArtists.stream()
                .collect(Collectors.groupingBy(
                        MusicArtist::getMusicId,
                        Collectors.mapping(
                                ma -> Optional.ofNullable(artistMap.get(ma.getArtistId()))
                                        .map(Artist::getName)
                                        .orElse(""),
                                Collectors.joining(ARTIST_NAME_SEPARATOR)
                        )
                ));

        // 5. 填充到VO
        voList.forEach(vo -> vo.setArtistNames(
                musicArtistNamesMap.getOrDefault(vo.getId(), "")
        ));
    }
}
