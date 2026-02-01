package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.mapper.AlbumMapper;
import com.naruto.wangyiyunmusic.model.dto.AlbumCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumUpdateDTO;
import com.naruto.wangyiyunmusic.model.entity.Album;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.vo.AlbumDetailVO;
import com.naruto.wangyiyunmusic.model.vo.AlbumListVO;
import com.naruto.wangyiyunmusic.service.AlbumService;
import com.naruto.wangyiyunmusic.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 专辑表 服务实现类
 *
 * <p>提供专辑的增删改查业务逻辑实现</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Slf4j
@Service
public class AlbumServiceImpl extends ServiceImpl<AlbumMapper, Album> implements AlbumService {

    @Autowired
    private MusicService musicService;

    @Override
    public IPage<AlbumListVO> pageQuery(AlbumQueryDTO queryDTO) {
        log.info("分页查询专辑列表, 查询参数: {}", queryDTO);

        // 1. 构建分页对象
        Page<Album> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Album> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索（专辑名称）
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.like(Album::getName, queryDTO.getKeyword());
        }

        // 排序
        if ("release_date".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Album::getReleaseDate);
        } else if ("create_time".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Album::getCreateTime);
        } else {
            // 默认按发行日期降序
            wrapper.orderByDesc(Album::getReleaseDate);
        }

        // 3. 查询专辑分页数据
        IPage<Album> albumPage = this.page(page, wrapper);

        // 4. 转换为 AlbumListVO 并填充歌曲数量
        List<AlbumListVO> voList = albumPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        // 5. 构造返回结果
        Page<AlbumListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(voList);

        log.info("分页查询专辑列表成功, 总记录数: {}", page.getTotal());
        return result;
    }

    @Override
    public AlbumDetailVO getAlbumDetail(Long id) {
        log.info("查询专辑详情, id: {}", id);

        // 1. 查询专辑基本信息
        Album album = this.getById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在，ID: " + id);
        }

        // 2. 转换为 AlbumDetailVO
        AlbumDetailVO detailVO = new AlbumDetailVO();
        BeanUtils.copyProperties(album, detailVO);

        // 3. 统计专辑歌曲数量
        Integer songCount = countSongsByAlbumId(id);
        detailVO.setSongCount(songCount);

        log.info("查询专辑详情成功, id: {}, name: {}, songCount: {}", id, album.getName(), songCount);
        return detailVO;
    }

    @Override
    public Long createAlbum(AlbumCreateDTO createDTO) {
        log.info("创建专辑, 请求参数: {}", createDTO);

        // 1. 参数校验（由 @Valid 注解完成）
        // 2. 构建 Album 实体
        Album album = new Album();
        BeanUtils.copyProperties(createDTO, album);

        // 3. 保存到数据库
        boolean success = this.save(album);
        if (!success) {
            throw new BusinessException("创建专辑失败");
        }

        log.info("创建专辑成功, id: {}, name: {}", album.getId(), album.getName());
        return album.getId();
    }

    @Override
    public Boolean updateAlbum(Long id, AlbumUpdateDTO updateDTO) {
        log.info("更新专辑, id: {}, 请求参数: {}", id, updateDTO);

        // 1. 检查专辑是否存在
        Album existingAlbum = this.getById(id);
        if (existingAlbum == null) {
            throw new BusinessException("专辑不存在，ID: " + id);
        }

        // 2. 更新专辑信息
        Album album = new Album();
        BeanUtils.copyProperties(updateDTO, album);
        album.setId(id);

        // 3. 执行更新
        boolean success = this.updateById(album);
        if (!success) {
            throw new BusinessException("更新专辑失败");
        }

        log.info("更新专辑成功, id: {}, name: {}", id, updateDTO.getName());
        return true;
    }

    @Override
    public Boolean deleteAlbum(Long id) {
        log.info("删除专辑, id: {}", id);

        // 1. 检查专辑是否存在
        Album album = this.getById(id);
        if (album == null) {
            throw new BusinessException("专辑不存在，ID: " + id);
        }

        // 2. 检查是否有关联歌曲（根据技术决策：方案 A - 删除前检查）
        Integer songCount = countSongsByAlbumId(id);
        if (songCount > 0) {
            throw new BusinessException("该专辑下存在 " + songCount + " 首歌曲，无法删除");
        }

        // 3. 执行逻辑删除（MyBatis-Plus 的 @TableLogic 注解会自动处理）
        boolean success = this.removeById(id);
        if (!success) {
            throw new BusinessException("删除专辑失败");
        }

        log.info("删除专辑成功, id: {}, name: {}", id, album.getName());
        return true;
    }

    /**
     * 统计专辑歌曲数量
     *
     * @param albumId 专辑ID
     * @return 歌曲数量
     */
    private Integer countSongsByAlbumId(Long albumId) {
        LambdaQueryWrapper<Music> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Music::getAlbumId, albumId);
        long count = musicService.count(wrapper);
        return (int) count;
    }

    /**
     * 转换为列表视图对象
     *
     * @param album 专辑实体
     * @return 列表视图对象
     */
    private AlbumListVO convertToListVO(Album album) {
        AlbumListVO vo = new AlbumListVO();
        BeanUtils.copyProperties(album, vo);

        // 统计歌曲数量
        Integer songCount = countSongsByAlbumId(album.getId());
        vo.setSongCount(songCount);

        return vo;
    }
}

