package com.naruto.wangyiyunmusic.service.strategy;

import com.naruto.wangyiyunmusic.exception.VideoParseException;
import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;
import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * YouTube视频解析策略（预留）
 *
 * <p>暂不支持，为后续扩展预留</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Component
public class YoutubeParseStrategy implements VideoPlatformStrategy {

    /**
     * YouTube URL正则表达式
     */
    private static final Pattern YOUTUBE_URL_PATTERN = Pattern.compile(
            "https?://(www\\.)?(youtube\\.com/watch\\?v=|youtu\\.be/)([a-zA-Z0-9_-]+)"
    );

    @Override
    public VideoPlatform getSupportedPlatform() {
        return VideoPlatform.YOUTUBE;
    }

    @Override
    public YtDlpResult parseVideo(String videoUrl) {
        log.warn("YouTube解析策略暂不支持: {}", videoUrl);
        throw new VideoParseException("YouTube平台暂不支持，敬请期待");
    }

    @Override
    public boolean validateVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            return false;
        }

        return YOUTUBE_URL_PATTERN.matcher(videoUrl).find();
    }

    @Override
    public String extractVideoId(String videoUrl) {
        var matcher = YOUTUBE_URL_PATTERN.matcher(videoUrl);
        if (matcher.find()) {
            return matcher.group(3);
        }

        throw new VideoParseException("无法从URL中提取YouTube视频ID: " + videoUrl);
    }
}
