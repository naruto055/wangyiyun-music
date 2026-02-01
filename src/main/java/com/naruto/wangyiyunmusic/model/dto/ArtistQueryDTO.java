package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 歌手查询请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "歌手查询条件")
public class ArtistQueryDTO {
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
     * 歌手名称（模糊搜索）
     */
    @Schema(description = "歌手名称（模糊搜索）", example = "周杰伦")
    private String name;

    /**
     * 国家/地区（精确匹配）
     */
    @Schema(description = "国家/地区", example = "中国台湾")
    private String country;

    /**
     * 排序字段(create_time/update_time)
     */
    @Schema(description = "排序字段", example = "create_time", allowableValues = {"create_time", "update_time"})
    private String sortField = "create_time";

    /**
     * 排序方式(asc/desc)
     */
    @Schema(description = "排序方式", example = "desc", allowableValues = {"asc", "desc"})
    private String sortOrder = "desc";
}
