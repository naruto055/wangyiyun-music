package com.naruto.wangyiyunmusic.exception;

/**
 * 业务异常
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
