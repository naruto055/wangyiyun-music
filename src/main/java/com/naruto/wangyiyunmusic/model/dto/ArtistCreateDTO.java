package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

/**
 * 歌手创建请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "歌手创建请求")
public class ArtistCreateDTO {
    /**
     * 歌手名称
     */
    @NotBlank(message = "歌手名称不能为空")
    @Size(min = 1, max = 50, message = "歌手名称长度必须在1-50之间")
    @Schema(description = "歌手名称", example = "周杰伦", required = true)
    private String name;

    /**
     * 头像URL
     */
    @URL(message = "头像URL格式不正确")
    @Size(max = 500, message = "头像URL长度不能超过500")
    @Schema(description = "头像URL", example = "https://example.com/avatar/jay.jpg")
    private String avatarUrl;

    /**
     * 简介
     */
    @Size(max = 500, message = "简介长度不能超过500")
    @Schema(description = "歌手简介", example = "华语流行音乐天王")
    private String description;

    /**
     * 国家/地区
     */
    @Size(max = 50, message = "国家/地区长度不能超过50")
    @Schema(description = "国家/地区", example = "中国台湾")
    private String country;
}
