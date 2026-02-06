package com.naruto.wangyiyunmusic.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 搜索历史实体
 *
 * <p>记录B站视频搜索历史，支持搜索次数统计和时间排序</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Data
@TableName("search_history")
@Schema(description = "搜索历史实体")
public class SearchHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Long id;

    /**
     * 用户ID（预留，当前可为NULL）
     */
    @TableField("user_id")
    @Schema(description = "用户ID（预留，当前可为NULL）", example = "1")
    private Long userId;

    /**
     * 搜索关键词
     */
    @TableField("keyword")
    @Schema(description = "搜索关键词", example = "周杰伦")
    private String keyword;

    /**
     * 搜索次数
     */
    @TableField("search_count")
    @Schema(description = "搜索次数", example = "5")
    private Integer searchCount;

    /**
     * 最后搜索时间
     */
    @TableField("last_search_time")
    @Schema(description = "最后搜索时间", example = "2026-02-05 12:00:00")
    private LocalDateTime lastSearchTime;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间", example = "2026-02-05 10:00:00")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间", example = "2026-02-05 12:00:00")
    private LocalDateTime updateTime;
}
