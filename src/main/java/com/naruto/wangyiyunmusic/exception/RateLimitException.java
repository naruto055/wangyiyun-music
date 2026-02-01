package com.naruto.wangyiyunmusic.exception;

import lombok.Getter;

/**
 * 限流异常
 *
 * <p>当IP触发限流策略时抛出此异常</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Getter
public class RateLimitException extends RuntimeException {

    /**
     * IP地址
     */
    private final String ip;

    /**
     * 限流类型
     */
    private final String limitType;

    /**
     * 构造函数
     *
     * @param ip IP地址
     * @param limitType 限流类型
     * @param message 异常消息
     */
    public RateLimitException(String ip, String limitType, String message) {
        super(message);
        this.ip = ip;
        this.limitType = limitType;
    }
}
