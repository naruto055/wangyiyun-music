package com.naruto.wangyiyunmusic.service;

import com.naruto.wangyiyunmusic.model.dto.VideoParseRequestDTO;
import com.naruto.wangyiyunmusic.model.vo.VideoParseResultVO;

/**
 * 视频解析服务接口
 *
 * <p>负责整合视频解析业务流程</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public interface VideoParseService {

    /**
     * 解析视频并提取音频
     *
     * @param requestDTO 解析请求参数
     * @return 解析结果
     */
    VideoParseResultVO parseVideo(VideoParseRequestDTO requestDTO);
}
