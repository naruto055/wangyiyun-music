package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.exception.BusinessException;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.vo.AudioUrlVO;
import com.naruto.wangyiyunmusic.service.AudioService;
import com.naruto.wangyiyunmusic.service.MusicService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 音频服务实现类
 *
 * <p>处理音频文件URL获取相关业务逻辑</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-28
 */
@Slf4j
@Service
public class AudioServiceImpl implements AudioService {

    @Autowired
    private MusicService musicService;

    /**
     * 服务器基础URL（如: http://localhost:8910）
     */
    @Value("${audio.server-base-url}")
    private String serverBaseUrl;

    /**
     * 音频URL前缀（如: /audio/）
     */
    @Value("${audio.url-prefix}")
    private String audioUrlPrefix;

    /**
     * 根据音乐ID获取音频访问URL
     *
     * @param musicId 音乐ID
     * @return 音频URL信息
     */
    @Override
    public AudioUrlVO getAudioUrl(Long musicId) {
        log.info("获取音频URL请求, musicId: {}", musicId);

        // 1. 查询音乐信息
        Music music = musicService.getById(musicId);
        if (music == null) {
            log.warn("音乐不存在, musicId: {}", musicId);
            throw new BusinessException("音乐不存在，ID: " + musicId);
        }

        // 2. 验证音频文件URL是否存在
        String fileUrl = music.getFileUrl();
        if (!StringUtils.hasText(fileUrl)) {
            log.warn("音乐暂无音频文件, musicId: {}, title: {}", musicId, music.getTitle());
            throw new BusinessException("该音乐暂无音频文件");
        }

        // 3. 构建完整的音频访问URL
        String audioUrl = buildAudioUrl(fileUrl);

        // 4. 提取文件名（取最后一部分）
        String fileName = extractFileName(fileUrl);

        // 5. 构建返回对象
        AudioUrlVO vo = new AudioUrlVO();
        vo.setAudioUrl(audioUrl);
        vo.setMusicId(music.getId());
        vo.setTitle(music.getTitle());
        vo.setFileName(fileName);
        vo.setDuration(music.getDuration());

        log.info("音频URL生成成功, musicId: {}, title: {}, audioUrl: {}",
                musicId, music.getTitle(), audioUrl);

        return vo;
    }

    /**
     * 构建完整的音频访问URL
     *
     * @param fileUrl 文件URL（相对路径或文件名）
     * @return 完整的HTTP访问URL
     */
    private String buildAudioUrl(String fileUrl) {
        // 确保 fileUrl 开头没有斜杠
        if (fileUrl.startsWith("/")) {
            fileUrl = fileUrl.substring(1);
        }

        // 确保 audioUrlPrefix 开头有斜杠，结尾有斜杠
        String prefix = audioUrlPrefix;
        if (!prefix.startsWith("/")) {
            prefix = "/" + prefix;
        }
        if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }

        // 确保 serverBaseUrl 结尾没有斜杠
        String baseUrl = serverBaseUrl;
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }

        // 拼接完整URL: http://服务器地址/audio/文件名
        return baseUrl + prefix + fileUrl;
    }

    /**
     * 提取文件名（取路径的最后一部分）
     *
     * @param fileUrl 文件URL
     * @return 文件名
     */
    private String extractFileName(String fileUrl) {
        if (!StringUtils.hasText(fileUrl)) {
            return "";
        }

        // 处理Windows和Unix路径分隔符
        int lastSlash = Math.max(fileUrl.lastIndexOf('/'), fileUrl.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            return fileUrl.substring(lastSlash + 1);
        }

        return fileUrl;
    }
}
