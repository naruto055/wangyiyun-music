package com.naruto.wangyiyunmusic.service;

import com.naruto.wangyiyunmusic.model.vo.AudioUrlVO;

/**
 * 音频服务接口
 *
 * <p>提供音频文件访问URL相关功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-28
 */
public interface AudioService {

    /**
     * 根据音乐ID获取音频访问URL
     *
     * @param musicId 音乐ID
     * @return 音频URL信息
     */
    AudioUrlVO getAudioUrl(Long musicId);
}
