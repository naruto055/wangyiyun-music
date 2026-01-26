package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.MusicQueryDTO;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.vo.MusicDetailVO;
import com.naruto.wangyiyunmusic.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 音乐表 前端控制器
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Tag(name = "音乐管理", description = "提供音乐查询、详情等接口")
@RestController
@RequestMapping("/api/music")
public class MusicController {

    @Autowired
    private MusicService musicService;

    /**
     * 分页查询音乐列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询音乐列表", description = "支持按分类、标签、关键词等条件筛选音乐")
    @GetMapping("/list")
    public IPage<Music> list(
            @Parameter(description = "查询条件，包含关键词、分类ID、标签ID、页码、每页大小等")
            MusicQueryDTO queryDTO) {
        return musicService.pageQuery(queryDTO);
    }

    /**
     * 获取音乐详情
     *
     * @param id 音乐ID
     * @return 音乐详情
     */
    @Operation(summary = "获取音乐详情", description = "根据音乐ID查询详细信息，包含歌手、专辑、标签等关联数据")
    @GetMapping("/{id}")
    public MusicDetailVO getDetail(
            @Parameter(description = "音乐ID", example = "1", required = true)
            @PathVariable Long id) {
        return musicService.getMusicDetail(id);
    }
}
