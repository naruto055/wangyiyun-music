package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.dto.MusicQueryDTO;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.baomidou.mybatisplus.extension.service.IService;
import com.naruto.wangyiyunmusic.model.vo.MusicDetailVO;

/**
 * <p>
 * 音乐表 服务类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
public interface MusicService extends IService<Music> {

    /**
     * 分页查询音乐列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<Music> pageQuery(MusicQueryDTO queryDTO);

    /**
     * 获取音乐详情（包含歌手、专辑、标签等）
     *
     * @param id 音乐ID
     * @return 音乐详情
     */
    MusicDetailVO getMusicDetail(Long id);
}
