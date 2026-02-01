package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.naruto.wangyiyunmusic.model.dto.ArtistCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistUpdateDTO;
import com.naruto.wangyiyunmusic.model.entity.Artist;
import com.naruto.wangyiyunmusic.model.vo.ArtistDetailVO;
import com.naruto.wangyiyunmusic.model.vo.ArtistListVO;

/**
 * 歌手表 服务类
 *
 * <p>提供歌手的增删改查业务逻辑</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
public interface ArtistService extends IService<Artist> {

    /**
     * 分页查询歌手列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<ArtistListVO> pageQuery(ArtistQueryDTO queryDTO);

    /**
     * 获取歌手详情
     *
     * @param id 歌手ID
     * @return 歌手详情
     */
    ArtistDetailVO getArtistDetail(Long id);

    /**
     * 创建歌手
     *
     * @param createDTO 创建请求参数
     * @return 新增歌手的ID
     */
    Long createArtist(ArtistCreateDTO createDTO);

    /**
     * 更新歌手信息
     *
     * @param id        歌手ID
     * @param updateDTO 更新请求参数
     * @return 是否更新成功
     */
    Boolean updateArtist(Long id, ArtistUpdateDTO updateDTO);

    /**
     * 删除歌手（逻辑删除）
     *
     * @param id 歌手ID
     * @return 是否删除成功
     */
    Boolean deleteArtist(Long id);

    /**
     * 检查歌手名称是否存在
     *
     * @param name 歌手名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 检查歌手名称是否存在（排除指定ID）
     *
     * @param name      歌手名称
     * @param excludeId 排除的歌手ID
     * @return 是否存在
     */
    boolean existsByNameExcludeId(String name, Long excludeId);
}
