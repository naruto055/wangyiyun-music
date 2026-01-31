package com.naruto.wangyiyunmusic.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置类
 *
 * <p>启用Spring定时任务支持，用于临时文件自动清理等功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    // 启用Spring定时任务支持
}
