package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.BilibiliSearchDTO;
import com.naruto.wangyiyunmusic.model.vo.BilibiliVideoVO;
import com.naruto.wangyiyunmusic.model.vo.SearchHistoryVO;
import com.naruto.wangyiyunmusic.service.BilibiliSearchService;
import com.naruto.wangyiyunmusic.service.SearchHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * B站视频搜索控制器
 *
 * <p>提供 B 站视频音乐检索相关接口</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@RestController
@RequestMapping("/api/bilibili")
@Tag(name = "B站视频搜索", description = "B站视频音乐检索相关接口")
@Slf4j
public class BilibiliSearchController {

    @Autowired
    private BilibiliSearchService bilibiliSearchService;

    @Autowired
    private SearchHistoryService searchHistoryService;

    /**
     * 搜索B站视频
     *
     * @param searchDTO 搜索请求参数
     * @return 分页搜索结果
     */
    @PostMapping("/search")
    @Operation(summary = "搜索B站视频", description = "根据关键词搜索B站视频")
    public Result<IPage<BilibiliVideoVO>> searchVideos(@Valid @RequestBody BilibiliSearchDTO searchDTO) {
        log.info("收到B站搜索请求, keyword={}, page={}, pageSize={}",
                searchDTO.getKeyword(), searchDTO.getPage(), searchDTO.getPageSize());

        IPage<BilibiliVideoVO> result = bilibiliSearchService.searchVideos(searchDTO);

        return Result.success("搜索成功", result);
    }

    /**
     * 查询搜索历史
     *
     * @param page 页码
     * @param pageSize 分页大小
     * @return 分页搜索历史
     */
    @GetMapping("/search/history")
    @Operation(summary = "查询搜索历史", description = "查询用户搜索历史记录，按最后搜索时间降序排序")
    public Result<IPage<SearchHistoryVO>> getHistory(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "分页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize
    ) {
        log.info("查询搜索历史, page={}, pageSize={}", page, pageSize);

        IPage<SearchHistoryVO> result = searchHistoryService.getHistory(null, page, pageSize);

        return Result.success("查询成功", result);
    }

    /**
     * 清空搜索历史
     *
     * @return 操作结果
     */
    @DeleteMapping("/search/history")
    @Operation(summary = "清空搜索历史", description = "清空用户搜索历史记录")
    public Result<String> clearHistory() {
        log.info("清空搜索历史");

        searchHistoryService.clearHistory(null);

        return Result.success("搜索历史已清空");
    }
}
