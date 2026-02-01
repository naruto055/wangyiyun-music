package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.mapper.ArtistMapper;
import com.naruto.wangyiyunmusic.model.dto.ArtistCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistUpdateDTO;
import com.naruto.wangyiyunmusic.model.entity.Artist;
import com.naruto.wangyiyunmusic.model.vo.ArtistDetailVO;
import com.naruto.wangyiyunmusic.model.vo.ArtistListVO;
import com.naruto.wangyiyunmusic.service.ArtistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 歌手表 服务实现类
 *
 * <p>提供歌手的增删改查业务逻辑实现</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Slf4j
@Service
public class ArtistServiceImpl extends ServiceImpl<ArtistMapper, Artist> implements ArtistService {

    @Override
    public IPage<ArtistListVO> pageQuery(ArtistQueryDTO queryDTO) {
        log.info("分页查询歌手列表, 查询参数: {}", queryDTO);

        // 1. 构建分页对象
        Page<Artist> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<Artist> wrapper = new LambdaQueryWrapper<>();

        // 歌手名称模糊搜索
        if (StringUtils.hasText(queryDTO.getName())) {
            wrapper.like(Artist::getName, queryDTO.getName());
        }

        // 国家/地区精确匹配
        if (StringUtils.hasText(queryDTO.getCountry())) {
            wrapper.eq(Artist::getCountry, queryDTO.getCountry());
        }

        // 排序
        if ("create_time".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Artist::getCreateTime);
        } else if ("update_time".equals(queryDTO.getSortField())) {
            wrapper.orderBy(true, "asc".equals(queryDTO.getSortOrder()), Artist::getUpdateTime);
        } else {
            // 默认按创建时间降序
            wrapper.orderByDesc(Artist::getCreateTime);
        }

        // 3. 查询歌手分页数据
        IPage<Artist> artistPage = this.page(page, wrapper);

        // 4. 转换为 ArtistListVO
        List<ArtistListVO> voList = artistPage.getRecords().stream()
                .map(this::convertToListVO)
                .collect(Collectors.toList());

        // 5. 构造返回结果
        Page<ArtistListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        result.setRecords(voList);

        log.info("分页查询歌手列表成功, 总记录数: {}", page.getTotal());
        return result;
    }

    @Override
    public ArtistDetailVO getArtistDetail(Long id) {
        log.info("查询歌手详情, id: {}", id);

        // 1. 查询歌手基本信息
        Artist artist = this.getById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在，ID: " + id);
        }

        // 2. 转换为 ArtistDetailVO
        ArtistDetailVO detailVO = new ArtistDetailVO();
        BeanUtils.copyProperties(artist, detailVO);

        log.info("查询歌手详情成功, id: {}, name: {}", id, artist.getName());
        return detailVO;
    }

    @Override
    public Long createArtist(ArtistCreateDTO createDTO) {
        log.info("创建歌手, 请求参数: {}", createDTO);

        // 1. 检查歌手名称是否已存在
        if (existsByName(createDTO.getName())) {
            throw new BusinessException("歌手名称已存在: " + createDTO.getName());
        }

        // 2. 构建 Artist 实体
        Artist artist = new Artist();
        BeanUtils.copyProperties(createDTO, artist);

        // 3. 保存到数据库
        boolean success = this.save(artist);
        if (!success) {
            throw new BusinessException("创建歌手失败");
        }

        log.info("创建歌手成功, id: {}, name: {}", artist.getId(), artist.getName());
        return artist.getId();
    }

    @Override
    public Boolean updateArtist(Long id, ArtistUpdateDTO updateDTO) {
        log.info("更新歌手, id: {}, 请求参数: {}", id, updateDTO);

        // 1. 检查歌手是否存在
        Artist existingArtist = this.getById(id);
        if (existingArtist == null) {
            throw new BusinessException("歌手不存在，ID: " + id);
        }

        // 2. 如果修改了名称，检查新名称是否与其他歌手重复
        if (!existingArtist.getName().equals(updateDTO.getName())) {
            if (existsByNameExcludeId(updateDTO.getName(), id)) {
                throw new BusinessException("歌手名称已存在: " + updateDTO.getName());
            }
        }

        // 3. 更新歌手信息
        Artist artist = new Artist();
        BeanUtils.copyProperties(updateDTO, artist);
        artist.setId(id);

        // 4. 执行更新
        boolean success = this.updateById(artist);
        if (!success) {
            throw new BusinessException("更新歌手失败");
        }

        log.info("更新歌手成功, id: {}, name: {}", id, updateDTO.getName());
        return true;
    }

    @Override
    public Boolean deleteArtist(Long id) {
        log.info("删除歌手, id: {}", id);

        // 1. 检查歌手是否存在
        Artist artist = this.getById(id);
        if (artist == null) {
            throw new BusinessException("歌手不存在，ID: " + id);
        }

        // 2. 执行逻辑删除（MyBatis-Plus 的 @TableLogic 注解会自动处理）
        // 注意：实际项目中应检查 music_artist 关联表，确认是否有关联音乐
        boolean success = this.removeById(id);
        if (!success) {
            throw new BusinessException("删除歌手失败");
        }

        log.info("删除歌手成功, id: {}, name: {}", id, artist.getName());
        return true;
    }

    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<Artist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artist::getName, name);
        long count = this.count(wrapper);
        return count > 0;
    }

    @Override
    public boolean existsByNameExcludeId(String name, Long excludeId) {
        LambdaQueryWrapper<Artist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Artist::getName, name)
                .ne(Artist::getId, excludeId);
        long count = this.count(wrapper);
        return count > 0;
    }

    /**
     * 转换为列表视图对象
     *
     * @param artist 歌手实体
     * @return 列表视图对象
     */
    private ArtistListVO convertToListVO(Artist artist) {
        ArtistListVO vo = new ArtistListVO();
        BeanUtils.copyProperties(artist, vo);
        return vo;
    }
}
