package com.naruto.wangyiyunmusic.exception;

/**
 * 视频解析异常
 *
 * <p>当视频解析过程中发生错误时抛出此异常</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public class VideoParseException extends BusinessException {

    public VideoParseException(String message) {
        super(message);
    }

    public VideoParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
