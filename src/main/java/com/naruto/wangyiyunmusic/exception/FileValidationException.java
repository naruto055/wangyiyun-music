package com.naruto.wangyiyunmusic.exception;

/**
 * 文件验证异常
 *
 * <p>当文件大小、格式验证失败时抛出此异常</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
public class FileValidationException extends BusinessException {

    public FileValidationException(String message) {
        super(message);
    }

    public FileValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
