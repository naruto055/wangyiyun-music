package com.naruto.wangyiyunmusic.service.strategy;

import com.naruto.wangyiyunmusic.exception.VideoParseException;
import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;
import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;
import com.naruto.wangyiyunmusic.service.YtDlpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * B站视频解析策略
 *
 * <p>负责解析B站视频链接并提取音频</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Component
public class BilibiliParseStrategy implements VideoPlatformStrategy {

    @Autowired
    private YtDlpService ytDlpService;

    /**
     * BV号正则表达式
     */
    private static final Pattern BV_PATTERN = Pattern.compile("BV[a-zA-Z0-9]+");

    /**
     * B站URL正则表达式（用于验证和提取BV号）
     */
    private static final Pattern BILIBILI_URL_PATTERN = Pattern.compile(
            "https?://(www\\.)?bilibili\\.com/video/(BV[a-zA-Z0-9]+)"
    );

    /**
     * 完整B站URL正则表达式（包括参数，用于从分享文本中提取）
     * <p>匹配示例：https://www.bilibili.com/video/BV1xxx/?share_source=copy_web&vd_source=xxx</p>
     */
    private static final Pattern FULL_BILIBILI_URL_PATTERN = Pattern.compile(
            "https?://(www\\.)?bilibili\\.com/video/BV[a-zA-Z0-9]+[^\\s\\u4e00-\\u9fa5]*"
    );

    @Override
    public VideoPlatform getSupportedPlatform() {
        return VideoPlatform.BILIBILI;
    }

    @Override
    public YtDlpResult parseVideo(String videoUrl) {
        log.info("B站解析策略开始解析视频: {}", videoUrl);

        // 从分享文本中提取实际的URL（支持B站原始分享格式）
        String cleanedUrl = extractUrlFromShareText(videoUrl);
        if (!cleanedUrl.equals(videoUrl)) {
            log.info("从分享文本中提取URL: {} -> {}", videoUrl, cleanedUrl);
        }

        // 验证URL格式
        if (!validateVideoUrl(cleanedUrl)) {
            throw new VideoParseException("无效的B站视频链接格式: " + cleanedUrl);
        }

        // 提取视频ID
        String videoId = extractVideoId(cleanedUrl);
        log.info("提取到BV号: {}", videoId);

        // 调用 yt-dlp 服务解析视频（使用清洗后的URL）
        YtDlpResult result = ytDlpService.parseVideoAndExtractAudio(cleanedUrl, getSupportedPlatform().getCode());

        log.info("B站视频解析成功: {}", videoId);
        return result;
    }

    @Override
    public boolean validateVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.trim().isEmpty()) {
            return false;
        }

        // 验证是否包含B站域名和BV号
        return BILIBILI_URL_PATTERN.matcher(videoUrl).find() || BV_PATTERN.matcher(videoUrl).find();
    }

    @Override
    public String extractVideoId(String videoUrl) {
        // 先尝试从完整URL中提取
        Matcher urlMatcher = BILIBILI_URL_PATTERN.matcher(videoUrl);
        if (urlMatcher.find()) {
            return urlMatcher.group(2);
        }

        // 再尝试直接提取BV号
        Matcher bvMatcher = BV_PATTERN.matcher(videoUrl);
        if (bvMatcher.find()) {
            return bvMatcher.group();
        }

        throw new VideoParseException("无法从URL中提取BV号: " + videoUrl);
    }

    /**
     * 从分享文本中提取实际的URL
     *
     * <p>支持B站原始分享格式：</p>
     * <ul>
     *   <li>原始分享文本：【标题】 https://www.bilibili.com/video/BV1xxx/?params</li>
     *   <li>提取结果：https://www.bilibili.com/video/BV1xxx/?params</li>
     * </ul>
     *
     * <p>如果输入已经是纯URL，则直接返回（向后兼容）</p>
     *
     * @param input 用户输入（可能包含标题的分享文本，或纯URL）
     * @return 提取的URL（如果找不到则返回原始输入）
     */
    private String extractUrlFromShareText(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        String trimmedInput = input.trim();

        // 尝试从文本中提取完整的B站URL（包括参数）
        Matcher matcher = FULL_BILIBILI_URL_PATTERN.matcher(trimmedInput);
        if (matcher.find()) {
            String extractedUrl = matcher.group();
            log.debug("从分享文本中提取到URL: {}", extractedUrl);
            return extractedUrl;
        }

        // 如果没有找到完整URL，返回原始输入（可能已经是纯URL或BV号）
        return trimmedInput;
    }
}
