package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 专辑查询请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "专辑查询条件")
public class AlbumQueryDTO {
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
     * 关键词（专辑名称搜索）
     */
    @Schema(description = "关键词（专辑名称）", example = "范特西")
    private String keyword;

    /**
     * 排序字段(release_date/create_time)
     */
    @Schema(description = "排序字段", example = "release_date", allowableValues = {"release_date", "create_time"})
    private String sortField = "release_date";

    /**
     * 排序方式(asc/desc)
     */
    @Schema(description = "排序方式", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
