package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.config.VideoParseConfig;
import com.naruto.wangyiyunmusic.exception.VideoParseException;
import com.naruto.wangyiyunmusic.model.dto.VideoParseRequestDTO;
import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;
import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;
import com.naruto.wangyiyunmusic.model.vo.VideoParseResultVO;
import com.naruto.wangyiyunmusic.service.FileValidationService;
import com.naruto.wangyiyunmusic.service.VideoParseService;
import com.naruto.wangyiyunmusic.service.strategy.VideoPlatformStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 视频解析服务实现类
 *
 * <p>协调整个视频解析流程，整合策略、验证、清理等服务</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class VideoParseServiceImpl implements VideoParseService {

    @Autowired
    private VideoParseConfig config;

    @Autowired
    private FileValidationService fileValidationService;

    @Autowired
    private List<VideoPlatformStrategy> strategies;

    /**
     * 策略映射缓存
     */
    private Map<VideoPlatform, VideoPlatformStrategy> strategyMap;

    /**
     * 初始化策略映射
     */
    @Autowired
    public void initStrategyMap(List<VideoPlatformStrategy> strategies) {
        this.strategyMap = strategies.stream()
                .collect(Collectors.toMap(
                        VideoPlatformStrategy::getSupportedPlatform,
                        Function.identity()
                ));

        log.info("✅ 视频解析策略加载完成，支持平台: {}",
                strategyMap.keySet().stream()
                        .map(VideoPlatform::getName)
                        .collect(Collectors.joining(", "))
        );
    }

    @Override
    public VideoParseResultVO parseVideo(VideoParseRequestDTO requestDTO) {
        log.info("========== 开始解析视频 ==========");
        log.info("视频链接: {}", requestDTO.getVideoUrl());
        log.info("平台类型: {}", requestDTO.getPlatform().getName());

        // 1. 获取对应平台的解析策略
        VideoPlatformStrategy strategy = getStrategy(requestDTO.getPlatform());

        // 2. 验证URL格式
        if (!strategy.validateVideoUrl(requestDTO.getVideoUrl())) {
            throw new VideoParseException("无效的视频链接格式");
        }

        // 3. 验证存储容量（预留足够空间用于下载）
        fileValidationService.validateStorageCapacity(config.getMaxFileSizeBytes());

        // 4. 调用策略解析视频
        YtDlpResult ytDlpResult = strategy.parseVideo(requestDTO.getVideoUrl());

        // 5. 验证文件大小和格式（验证失败时删除已下载的文件）
        try {
            fileValidationService.validateFileSize(ytDlpResult.getFileSize());
            fileValidationService.validateAudioFormat(ytDlpResult.getAudioFormat());
        } catch (Exception e) {
            // 验证失败，删除已下载的文件
            File audioFile = new File(ytDlpResult.getAudioFilePath());
            if (audioFile.exists() && audioFile.delete()) {
                log.warn("⚠️ 验证失败，已删除无效文件: {}", audioFile.getName());
            }
            throw e;  // 重新抛出异常
        }

        // 7. 构建访问URL
        String audioUrl = buildAudioUrl(ytDlpResult.getAudioFilePath());

        // 8. 构建返回结果
        VideoParseResultVO resultVO = new VideoParseResultVO();
        resultVO.setTitle(ytDlpResult.getTitle());
        resultVO.setAudioUrl(audioUrl);
        resultVO.setCoverUrl(ytDlpResult.getThumbnail());
        resultVO.setDuration(ytDlpResult.getDuration());
        resultVO.setFileSize(ytDlpResult.getFileSize());
        resultVO.setAudioFormat(ytDlpResult.getAudioFormat());
        resultVO.setSourceVideoId(ytDlpResult.getVideoId());

        log.info("========== 视频解析成功 ==========");
        log.info("标题: {}", resultVO.getTitle());
        log.info("音频URL: {}", resultVO.getAudioUrl());
        log.info("文件大小: {} MB", String.format("%.2f", resultVO.getFileSize() / 1024.0 / 1024.0));

        return resultVO;
    }

    /**
     * 获取平台解析策略
     */
    private VideoPlatformStrategy getStrategy(VideoPlatform platform) {
        VideoPlatformStrategy strategy = strategyMap.get(platform);

        if (strategy == null) {
            throw new VideoParseException("不支持的平台类型: " + platform.getName());
        }

        return strategy;
    }

    /**
     * 构建音频访问URL
     *
     * @param audioFilePath 音频文件路径
     * @return 访问URL
     */
    private String buildAudioUrl(String audioFilePath) {
        File audioFile = new File(audioFilePath);
        String fileName = audioFile.getName();

        // 使用配置的URL前缀
        return config.getServerBaseUrl() + config.getTempAudioUrlPrefix() + fileName;
    }
}
