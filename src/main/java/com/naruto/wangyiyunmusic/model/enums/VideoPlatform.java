package com.naruto.wangyiyunmusic.model.enums;

import lombok.Getter;

/**
 * 视频平台枚举
 *
 * <p>定义支持的视频平台类型</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-31
 */
@Getter
public enum VideoPlatform {

    /**
     * 哔哩哔哩
     */
    BILIBILI("BILIBILI", "哔哩哔哩"),

    /**
     * YouTube
     */
    YOUTUBE("YOUTUBE", "YouTube"),

    /**
     * 抖音
     */
    DOUYIN("DOUYIN", "抖音");

    /**
     * 平台代码
     */
    private final String code;

    /**
     * 平台名称
     */
    private final String name;

    VideoPlatform(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * 根据code查找枚举
     *
     * @param code 平台代码
     * @return 视频平台枚举
     */
    public static VideoPlatform fromCode(String code) {
        for (VideoPlatform platform : values()) {
            if (platform.getCode().equalsIgnoreCase(code)) {
                return platform;
            }
        }
        throw new IllegalArgumentException("不支持的平台类型: " + code);
    }
}
