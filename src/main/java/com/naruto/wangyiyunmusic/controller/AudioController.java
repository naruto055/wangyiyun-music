package com.naruto.wangyiyunmusic.controller;

import com.naruto.wangyiyunmusic.model.vo.AudioUrlVO;
import com.naruto.wangyiyunmusic.service.AudioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 音频管理控制器
 *
 * <p>提供音频文件访问URL相关接口</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-28
 */
@Tag(name = "音频管理", description = "提供音频文件访问接口")
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    /**
     * 获取音频访问URL
     *
     * @param musicId 音乐ID
     * @return 音频URL信息
     */
    @Operation(
            summary = "获取音频URL",
            description = "根据音乐ID获取音频文件的访问URL，前端可直接使用该URL播放音频，支持HTTP Range请求实现拖拽播放"
    )
    @GetMapping("/{musicId}")
    public AudioUrlVO getAudioUrl(
            @Parameter(description = "音乐ID", example = "1", required = true)
            @PathVariable Long musicId) {
        return audioService.getAudioUrl(musicId);
    }
}
