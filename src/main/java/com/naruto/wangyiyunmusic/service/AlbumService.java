package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.naruto.wangyiyunmusic.model.dto.AlbumCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumUpdateDTO;
import com.naruto.wangyiyunmusic.model.entity.Album;
import com.naruto.wangyiyunmusic.model.vo.AlbumDetailVO;
import com.naruto.wangyiyunmusic.model.vo.AlbumListVO;

/**
 * 专辑表 服务类
 *
 * <p>提供专辑的增删改查业务逻辑</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
public interface AlbumService extends IService<Album> {

    /**
     * 分页查询专辑列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果（包含歌曲数量）
     */
    IPage<AlbumListVO> pageQuery(AlbumQueryDTO queryDTO);

    /**
     * 获取专辑详情（包含歌曲数量）
     *
     * @param id 专辑ID
     * @return 专辑详情
     */
    AlbumDetailVO getAlbumDetail(Long id);

    /**
     * 创建专辑
     *
     * @param createDTO 创建请求参数
     * @return 新增专辑的ID
     */
    Long createAlbum(AlbumCreateDTO createDTO);

    /**
     * 更新专辑信息
     *
     * @param id        专辑ID
     * @param updateDTO 更新请求参数
     * @return 是否更新成功
     */
    Boolean updateAlbum(Long id, AlbumUpdateDTO updateDTO);

    /**
     * 删除专辑（逻辑删除）
     *
     * @param id 专辑ID
     * @return 是否删除成功
     */
    Boolean deleteAlbum(Long id);
}
