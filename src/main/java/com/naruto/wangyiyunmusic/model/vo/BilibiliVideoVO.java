package com.naruto.wangyiyunmusic.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * B站视频搜索结果（简化版 v1.3）
 *
 * <p>设计理念：专注音乐本身，不关注UP主信息和视频统计数据</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Data
@Schema(description = "B站视频搜索结果（简化版）")
public class BilibiliVideoVO {

    @Schema(description = "B站视频BV号", example = "BV1xx411c7XZ")
    private String bvid;

    @Schema(description = "视频标题", example = "周杰伦 - 晴天 官方MV")
    private String title;

    @Schema(description = "视频时长（格式化字符串）", example = "4:35")
    private String duration;

    @Schema(description = "B站视频链接", example = "https://www.bilibili.com/video/BV1xx411c7XZ")
    private String url;
}
