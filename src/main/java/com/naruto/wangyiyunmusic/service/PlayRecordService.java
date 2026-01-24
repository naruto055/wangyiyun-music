package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.dto.PlayRecordDTO;
import com.naruto.wangyiyunmusic.model.entity.PlayRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 播放记录表 服务类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
public interface PlayRecordService extends IService<PlayRecord> {

    /**
     * 记录播放
     *
     * @param recordDTO 播放记录DTO
     */
    void recordPlay(PlayRecordDTO recordDTO);

    /**
     * 查询播放历史
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 播放记录列表
     */
    IPage<PlayRecord> getPlayHistory(Long userId, Integer pageNum, Integer pageSize);
}
