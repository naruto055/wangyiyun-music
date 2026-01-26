package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 音乐查询请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
@Schema(description = "音乐查询条件")
public class MusicQueryDTO {
    /**
     * 页码
     */
    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    /**
     * 分类ID
     */
    @Schema(description = "分类ID", example = "1")
    private Long categoryId;

    /**
     * 关键词（歌曲名/歌手名）
     */
    @Schema(description = "关键词（歌曲名/歌手名）", example = "周杰伦")
    private String keyword;

    /**
     * 排序字段(play_count/create_time)
     */
    @Schema(description = "排序字段", example = "create_time", allowableValues = {"play_count", "create_time"})
    private String sortField = "create_time";

    /**
     * 排序方式(asc/desc)
     */
    @Schema(description = "排序方式", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
