package com.naruto.wangyiyunmusic.controller;

import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.VideoParseRequestDTO;
import com.naruto.wangyiyunmusic.model.vo.VideoParseResultVO;
import com.naruto.wangyiyunmusic.service.VideoParseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 视频解析控制器
 *
 * <p>提供视频解析和音频提取相关接口</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@RestController
@RequestMapping("/api/video")
@Tag(name = "视频解析管理", description = "视频解析和音频提取相关接口")
public class VideoParseController {

    @Autowired
    private VideoParseService videoParseService;

    /**
     * 解析视频并提取音频
     *
     * @param requestDTO 解析请求参数
     * @return 解析结果
     */
    @PostMapping("/parse")
    @Operation(
            summary = "解析视频并提取音频",
            description = "支持B站、YouTube等平台的视频解析，提取音频文件并返回临时访问URL（1小时有效）"
    )
    public Result<VideoParseResultVO> parseVideo(
            @Parameter(description = "视频解析请求参数", required = true)
            @Valid @RequestBody VideoParseRequestDTO requestDTO) {

        log.info("收到视频解析请求: {}", requestDTO.getVideoUrl());

        VideoParseResultVO result = videoParseService.parseVideo(requestDTO);

        return Result.success(result);
    }
}
