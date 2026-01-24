package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.PlayRecordDTO;
import com.naruto.wangyiyunmusic.model.entity.PlayRecord;
import com.naruto.wangyiyunmusic.service.PlayRecordService;
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
    @PostMapping
    public Result<Void> recordPlay(@RequestBody @Valid PlayRecordDTO recordDTO) {
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
    @GetMapping("/history")
    public Result<IPage<PlayRecord>> getHistory(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        IPage<PlayRecord> page = playRecordService.getPlayHistory(0L, pageNum, pageSize);
        return Result.success(page);
    }
}
