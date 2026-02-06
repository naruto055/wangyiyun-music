package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搜索历史记录VO
 *
 * <p>返回给前端的搜索历史数据</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Data
@Schema(description = "搜索历史记录")
public class SearchHistoryVO {

    /**
     * 历史记录ID
     */
    @Schema(description = "历史记录ID", example = "1")
    private Long id;

    /**
     * 搜索关键词
     */
    @Schema(description = "搜索关键词", example = "周杰伦")
    private String keyword;

    /**
     * 搜索次数
     */
    @Schema(description = "搜索次数", example = "5")
    private Integer searchCount;

    /**
     * 最后搜索时间
     */
    @Schema(description = "最后搜索时间", example = "2026-02-05 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSearchTime;
}
