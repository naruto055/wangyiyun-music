package com.naruto.wangyiyunmusic.exception;

import lombok.Getter;

/**
 * 防盗链异常
 *
 * <p>当请求被防盗链策略拦截时抛出此异常</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Getter
public class AntiLeechException extends RuntimeException {

    /**
     * IP地址
     */
    private final String ip;

    /**
     * 拦截原因
     */
    private final String reason;

    /**
     * 构造函数
     *
     * @param ip IP地址
     * @param reason 拦截原因
     * @param message 异常消息
     */
    public AntiLeechException(String ip, String reason, String message) {
        super(message);
        this.ip = ip;
        this.reason = reason;
    }
}
