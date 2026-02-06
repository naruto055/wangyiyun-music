package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * B站视频搜索请求参数
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Data
@Schema(description = "B站视频搜索请求参数")
public class BilibiliSearchDTO {

    @NotBlank(message = "搜索关键词不能为空")
    @Schema(description = "搜索关键词", example = "周杰伦", requiredMode = Schema.RequiredMode.REQUIRED)
    private String keyword;

    @Min(value = 1, message = "页码必须大于0")
    @Schema(description = "页码", example = "1")
    private Integer page = 1;

    @Min(value = 1, message = "分页大小必须大于0")
    @Max(value = 50, message = "分页大小不能超过50")
    @Schema(description = "分页大小", example = "20")
    private Integer pageSize = 20;
}
