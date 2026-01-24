package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.common.Result;
import com.naruto.wangyiyunmusic.model.dto.MusicQueryDTO;
import com.naruto.wangyiyunmusic.model.entity.Music;
import com.naruto.wangyiyunmusic.model.vo.MusicDetailVO;
import com.naruto.wangyiyunmusic.service.MusicService;
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
    @GetMapping("/list")
    public Result<IPage<Music>> list(MusicQueryDTO queryDTO) {
        IPage<Music> page = musicService.pageQuery(queryDTO);
        return Result.success(page);
    }

    /**
     * 获取音乐详情
     *
     * @param id 音乐ID
     * @return 音乐详情
     */
    @GetMapping("/{id}")
    public Result<MusicDetailVO> getDetail(@PathVariable Long id) {
        MusicDetailVO detail = musicService.getMusicDetail(id);
        return Result.success(detail);
    }
}
