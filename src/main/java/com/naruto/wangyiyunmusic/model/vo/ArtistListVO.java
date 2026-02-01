package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 歌手列表视图对象
 *
 * <p>用于歌手列表展示，包含基本信息</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "歌手列表信息")
public class ArtistListVO {

    /**
     * 歌手ID
     */
    @Schema(description = "歌手ID", example = "1")
    private Long id;

    /**
     * 歌手名称
     */
    @Schema(description = "歌手名称", example = "周杰伦")
    private String name;

    /**
     * 头像URL
     */
    @Schema(description = "头像URL", example = "https://example.com/avatar/jay.jpg")
    private String avatarUrl;

    /**
     * 国家/地区
     */
    @Schema(description = "国家/地区", example = "中国台湾")
    private String country;

    /**
     * 创建时间
     */
    @Schema(description = "创建时间", example = "2026-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间", example = "2026-01-01 10:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
