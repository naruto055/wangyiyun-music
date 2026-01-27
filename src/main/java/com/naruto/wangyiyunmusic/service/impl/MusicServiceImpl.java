package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.model.dto.MusicQueryDTO;
import com.naruto.wangyiyunmusic.model.entity.*;
import com.naruto.wangyiyunmusic.mapper.MusicMapper;
import com.naruto.wangyiyunmusic.model.vo.ArtistVO;
import com.naruto.wangyiyunmusic.model.vo.MusicDetailVO;
import com.naruto.wangyiyunmusic.model.vo.MusicListVO;
import com.naruto.wangyiyunmusic.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 音乐表 服务实现类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Service
public class MusicServiceImpl extends ServiceImpl<MusicMapper, Music> implements MusicService {

    @Autowired
    private ArtistService artistService;

    @Autowired
    private MusicArtistService musicArtistService;

    @Autowired
    private ArtistNameService artistNameService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @Autowired
    private MusicTagService musicTagService;

    @Override
    public IPage<MusicListVO> pageQuery(MusicQueryDTO queryDTO) {
        // 1. 构建分页对象
        Page<Music> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Music> wrapper = buildQueryWrapper(queryDTO);

        // 3. 查询音乐分页数据
        IPage<Music> musicPage = this.page(page, wrapper);

        // 4. 转换为 MusicListVO
        List<MusicListVO> voList = musicPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        // 5. 批量填充歌手名称（使用公共服务避免 N+1 问题）
        artistNameService.fillArtistNames(voList);

        // 6. 构造返回结果
        Page<MusicListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(voList);
        return result;
    }

    /**
     * 构建查询条件
     *
     * @param queryDTO 查询条件
     * @return 查询包装器
     */
    private LambdaQueryWrapper<Music> buildQueryWrapper(MusicQueryDTO queryDTO) {
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();

        // 分类筛选
        if (queryDTO.getCategoryId() != null) {
            wrapper.eq(Music::getCategoryId, queryDTO.getCategoryId());
        }

        // 关键词搜索（歌曲名）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Music::getTitle, queryDTO.getKeyword());
        }

        // 排序
        if ("play_count".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Music::getPlayCount);
        } else {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Music::getCreateTime);
        }

        return wrapper;
    }

    /**
     * Music 实体转换为 MusicListVO
     *
     * @param music 音乐实体
     * @return MusicListVO
     */
    private MusicListVO convertToListVO(Music music) {
        MusicListVO vo = new MusicListVO();
        BeanUtils.copyProperties(music, vo);
        // 日期格式化由 @JsonFormat 注解自动处理
        return vo;
    }

    @Override
    public MusicDetailVO getMusicDetail(Long id) {
        // 查询音乐基本信息
        Music music = this.getById(id);
        if (music == null) {
            throw new BusinessException("音乐不存在");
        }

        MusicDetailVO vo = new MusicDetailVO();
        BeanUtils.copyProperties(music, vo);

        // 查询专辑名称
        if (music.getAlbumId() != null) {
            Album album = albumService.getById(music.getAlbumId());
            if (album != null) {
                vo.setAlbumName(album.getName());
            }
        }

        // 查询分类名称
        if (music.getCategoryId() != null) {
            Category category = categoryService.getById(music.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        // 查询歌手列表 - 使用批量查询优化 N+1 问题
        List<MusicArtist> musicArtists = musicArtistService.list(
                new LambdaQueryWrapper<MusicArtist>().eq(MusicArtist::getMusicId, id)
        );
        if (!musicArtists.isEmpty()) {
            // 批量查询所有艺术家
            List<Long> artistIds = musicArtists.stream()
                    .map(MusicArtist::getArtistId)
                    .collect(Collectors.toList());
            Map<Long, Artist> artistMap = artistService.listByIds(artistIds).stream()
                    .collect(Collectors.toMap(Artist::getId, Function.identity()));

            // 使用 Map 查找，避免逐个查询数据库
            List<ArtistVO> artistVOList = musicArtists.stream().map(ma -> {
                Artist artist = artistMap.get(ma.getArtistId());
                if (artist == null) {
                    return null;
                }
                ArtistVO artistVO = new ArtistVO();
                artistVO.setId(artist.getId());
                artistVO.setName(artist.getName());
                artistVO.setRole(ma.getArtistRole());
                return artistVO;
            }).filter(Objects::nonNull).collect(Collectors.toList());
            vo.setArtists(artistVOList);
        }

        // 查询标签列表 - 使用批量查询优化 N+1 问题
        List<MusicTag> musicTags = musicTagService.list(
                new LambdaQueryWrapper<MusicTag>().eq(MusicTag::getMusicId, id)
        );
        if (!musicTags.isEmpty()) {
            // 批量查询所有标签
            List<Long> tagIds = musicTags.stream()
                    .map(MusicTag::getTagId)
                    .collect(Collectors.toList());
            Map<Long, Tag> tagMap = tagService.listByIds(tagIds).stream()
                    .collect(Collectors.toMap(Tag::getId, Function.identity()));

            // 使用 Map 查找，避免逐个查询数据库
            List<String> tagNames = musicTags.stream()
                    .map(mt -> tagMap.get(mt.getTagId()))
                    .filter(Objects::nonNull)
                    .map(Tag::getName)
                    .collect(Collectors.toList());
            vo.setTags(tagNames);
        }

        return vo;
    }
}
