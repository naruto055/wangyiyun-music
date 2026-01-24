package com.naruto.wangyiyunmusic.model.dto;

import lombok.Data;

/**
 * 音乐查询请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-01-24
 */
@Data
public class MusicQueryDTO {
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    private Integer pageSize = 10;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 关键词（歌曲名/歌手名）
     */
    private String keyword;

    /**
     * 排序字段(play_count/create_time)
     */
    private String sortField = "create_time";

    /**
     * 排序方式(asc/desc)
     */
    private String sortOrder = "desc";
}
