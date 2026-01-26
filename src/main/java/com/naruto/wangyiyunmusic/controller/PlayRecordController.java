package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.PlayRecordDTO;
import com.naruto.wangyiyunmusic.model.entity.PlayRecord;
import com.naruto.wangyiyunmusic.service.PlayRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 播放记录表 前端控制器
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Tag(name = "播放记录管理", description = "提供播放记录、播放历史查询等接口")
@RestController
@RequestMapping("/api/play-record")
public class PlayRecordController {

    @Autowired
    private PlayRecordService playRecordService;

    /**
     * 记录播放
     *
     * @param recordDTO 播放记录DTO
     * @return 成功响应
     */
    @Operation(summary = "记录播放", description = "记录用户播放音乐的详细信息，包括播放进度、播放时长等")
    @PostMapping
    public Result<Void> recordPlay(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "播放记录信息")
            @org.springframework.web.bind.annotation.RequestBody @Valid PlayRecordDTO recordDTO) {
        playRecordService.recordPlay(recordDTO);
        return Result.success();
    }

    /**
     * 查询播放历史
     *
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 播放记录列表
     */
    @Operation(summary = "查询播放历史", description = "分页查询用户的播放历史记录")
    @GetMapping("/history")
    public Result<IPage<PlayRecord>> getHistory(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PlayRecord> page = playRecordService.getPlayHistory(0L, pageNum, pageSize);
        return Result.success(page);
    }
}
