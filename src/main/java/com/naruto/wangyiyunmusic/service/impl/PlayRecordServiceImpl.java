package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.mapper.PlayRecordMapper;
import com.naruto.wangyiyunmusic.model.dto.PlayRecordDTO;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.entity.PlayRecord;
import com.naruto.wangyiyunmusic.service.MusicService;
import com.naruto.wangyiyunmusic.service.PlayRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 * 播放记录表 服务实现类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Service
public class PlayRecordServiceImpl extends ServiceImpl<PlayRecordMapper, PlayRecord> implements PlayRecordService {

    @Autowired
    private MusicService musicService;

    @Override
    @Transactional
    public void recordPlay(PlayRecordDTO recordDTO) {
        // 验证音乐是否存在
        Music music = musicService.getById(recordDTO.getMusicId());
        if (music == null) {
            throw new com.naruto.wangyiyunmusic.exception.BusinessException("音乐不存在");
        }

        // 插入播放记录
        PlayRecord record = new PlayRecord();
        record.setMusicId(recordDTO.getMusicId());
        record.setUserId(0L); // 暂时默认0
        record.setPlayDuration(recordDTO.getPlayDuration());
        record.setPlaySource(recordDTO.getPlaySource());
        this.save(record);

        // 更新音乐播放次数
        music.setPlayCount(music.getPlayCount() + 1);
        musicService.updateById(music);
    }

    @Override
    public IPage<PlayRecord> getPlayHistory(Long userId, Integer pageNum, Integer pageSize) {
        Page<PlayRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<PlayRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PlayRecord::getUserId, userId)
                .orderByDesc(PlayRecord::getCreateTime);
        return this.page(page, wrapper);
    }
}
