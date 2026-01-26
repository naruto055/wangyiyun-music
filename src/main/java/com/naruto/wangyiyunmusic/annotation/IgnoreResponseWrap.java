package com.naruto.wangyiyunmusic.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 忽略响应封装注解
 *
 * <p>用于标记不需要统一封装为 Result 的接口方法或类</p>
 * <p>典型场景：文件下载、第三方回调、SSE 推送等</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-26
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface IgnoreResponseWrap {
}
