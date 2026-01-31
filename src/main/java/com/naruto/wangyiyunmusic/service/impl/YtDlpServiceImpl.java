package com.naruto.wangyiyunmusic.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.naruto.wangyiyunmusic.config.VideoParseConfig;
import com.naruto.wangyiyunmusic.exception.VideoParseException;
import com.naruto.wangyiyunmusic.model.entity.YtDlpResult;
import com.naruto.wangyiyunmusic.model.enums.VideoPlatform;
import com.naruto.wangyiyunmusic.service.YtDlpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * yt-dlp 工具服务实现类
 *
 * <p>使用 ProcessBuilder 调用 yt-dlp 工具解析视频</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Slf4j
@Service
public class YtDlpServiceImpl implements YtDlpService {

    @Autowired
    private VideoParseConfig config;

    private static final Pattern BV_PATTERN = Pattern.compile("BV[a-zA-Z0-9]+");

    @Override
    public YtDlpResult parseVideoAndExtractAudio(String videoUrl, String platform) {
        log.info("开始解析视频: {}, 平台: {}", videoUrl, platform);

        // 验证 yt-dlp 工具存在
        File ytDlpFile = new File(config.getYtDlpPath());
        if (!ytDlpFile.exists()) {
            throw new VideoParseException("yt-dlp 工具不存在，路径: " + config.getYtDlpPath());
        }

        // 提取视频ID
        String videoId = extractVideoId(videoUrl, platform);
        log.info("提取视频ID: {}", videoId);

        // 构建输出文件路径
        String outputTemplate = config.getTempPath() + videoId + ".%(ext)s";

        // 构建 yt-dlp 命令
        List<String> command = buildCommand(videoUrl, outputTemplate);
        log.info("执行命令: {}", String.join(" ", command));

        try {
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                    log.debug("yt-dlp 输出: {}", line);
                }
            }

            // 等待命令执行完成
            boolean completed = process.waitFor(config.getTimeout(), TimeUnit.SECONDS);
            if (!completed) {
                process.destroy();
                throw new VideoParseException("视频解析超时（超过 " + config.getTimeout() + " 秒）");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("yt-dlp 执行失败，退出码: {}, 输出: {}", exitCode, output);
                throw new VideoParseException("视频解析失败，退出码: " + exitCode);
            }

            // 解析元数据
            YtDlpResult result = parseMetadata(videoUrl, videoId);
            result.setPlatform(platform);

            // 查找生成的音频文件
            String audioFilePath = findAudioFile(videoId);
            if (audioFilePath == null) {
                throw new VideoParseException("未找到生成的音频文件");
            }

            result.setAudioFilePath(audioFilePath);
            result.setAudioFormat(getFileExtension(audioFilePath));

            // 获取文件大小
            File audioFile = new File(audioFilePath);
            result.setFileSize(audioFile.length());

            log.info("视频解析成功: {}", result);
            return result;

        } catch (VideoParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("视频解析异常", e);
            throw new VideoParseException("视频解析异常: " + e.getMessage(), e);
        }
    }

    @Override
    public YtDlpResult getVideoMetadata(String videoUrl) {
        log.info("获取视频元数据: {}", videoUrl);

        // 验证 yt-dlp 工具存在
        File ytDlpFile = new File(config.getYtDlpPath());
        if (!ytDlpFile.exists()) {
            throw new VideoParseException("yt-dlp 工具不存在，路径: " + config.getYtDlpPath());
        }

        // 构建命令（仅获取元数据）
        List<String> command = new ArrayList<>();
        command.add(config.getYtDlpPath());
        command.add("--dump-json");
        command.add("--no-download");
        command.add(videoUrl);

        log.info("执行命令: {}", String.join(" ", command));

        try {
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取输出
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
            }

            // 等待命令执行完成
            boolean completed = process.waitFor(60, TimeUnit.SECONDS);
            if (!completed) {
                process.destroy();
                throw new VideoParseException("获取元数据超时");
            }

            int exitCode = process.exitValue();
            if (exitCode != 0) {
                log.error("获取元数据失败，退出码: {}, 输出: {}", exitCode, output);
                throw new VideoParseException("获取元数据失败，退出码: " + exitCode);
            }

            // 解析 JSON 元数据
            JSONObject json = JSON.parseObject(output.toString());
            YtDlpResult result = new YtDlpResult();
            result.setTitle(json.getString("title"));
            result.setDuration(json.getInteger("duration"));
            result.setThumbnail(json.getString("thumbnail"));
            result.setVideoId(json.getString("id"));

            log.info("元数据获取成功: {}", result);
            return result;

        } catch (VideoParseException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取元数据异常", e);
            throw new VideoParseException("获取元数据异常: " + e.getMessage(), e);
        }
    }

    /**
     * 构建 yt-dlp 命令
     */
    private List<String> buildCommand(String videoUrl, String outputTemplate) {
        List<String> command = new ArrayList<>();
        command.add(config.getYtDlpPath());

        // 提取音频
        command.add("-x");
        command.add("--audio-format");
        command.add(config.getAudioFormat());

        // 音频质量
        command.add("--audio-quality");
        command.add(config.getAudioQuality().toString());

        // 输出模板
        command.add("-o");
        command.add(outputTemplate);

        // 不输出进度条（避免干扰日志）
        command.add("--no-progress");

        // 输出详细信息
        command.add("--print-json");

        // 视频URL
        command.add(videoUrl);

        return command;
    }

    /**
     * 解析元数据
     */
    private YtDlpResult parseMetadata(String videoUrl, String videoId) {
        try {
            YtDlpResult metadata = getVideoMetadata(videoUrl);
            metadata.setVideoId(videoId);
            return metadata;
        } catch (Exception e) {
            log.warn("获取元数据失败，使用默认值", e);
            YtDlpResult result = new YtDlpResult();
            result.setVideoId(videoId);
            result.setTitle("未知标题");
            return result;
        }
    }

    /**
     * 提取视频ID
     */
    private String extractVideoId(String videoUrl, String platform) {
        if (VideoPlatform.BILIBILI.getCode().equalsIgnoreCase(platform)) {
            Matcher matcher = BV_PATTERN.matcher(videoUrl);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        // 默认返回URL的hash值
        return String.valueOf(Math.abs(videoUrl.hashCode()));
    }

    /**
     * 查找生成的音频文件
     */
    private String findAudioFile(String videoId) {
        try {
            File tempDir = new File(config.getTempPath());
            File[] files = tempDir.listFiles((dir, name) ->
                    name.startsWith(videoId) && (name.endsWith(".mp3") || name.endsWith(".m4a") || name.endsWith(".opus"))
            );

            if (files != null && files.length > 0) {
                return files[0].getAbsolutePath();
            }

            return null;
        } catch (Exception e) {
            log.error("查找音频文件失败", e);
            return null;
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filePath) {
        int lastDot = filePath.lastIndexOf('.');
        if (lastDot > 0) {
            return filePath.substring(lastDot + 1);
        }
        return "mp3";
    }
}
