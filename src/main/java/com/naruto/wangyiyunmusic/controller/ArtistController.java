package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.dto.ArtistCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.ArtistUpdateDTO;
import com.naruto.wangyiyunmusic.model.vo.ArtistDetailVO;
import com.naruto.wangyiyunmusic.model.vo.ArtistListVO;
import com.naruto.wangyiyunmusic.service.ArtistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 歌手表 前端控制器
 *
 * <p>提供歌手管理相关的 RESTful API</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Tag(name = "歌手管理", description = "提供歌手的增删改查接口")
@RestController
@RequestMapping("/api/artist")
public class ArtistController {

    @Autowired
    private ArtistService artistService;

    /**
     * 分页查询歌手列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @Operation(summary = "分页查询歌手列表", description = "支持按名称、国家搜索，可排序，返回歌手列表")
    @GetMapping("/list")
    public IPage<ArtistListVO> list(
            @Parameter(description = "查询条件，包含名称、国家、排序字段、页码、每页大小等")
            ArtistQueryDTO queryDTO) {
        return artistService.pageQuery(queryDTO);
    }

    /**
     * 获取歌手详情
     *
     * @param id 歌手ID
     * @return 歌手详情
     */
    @Operation(summary = "获取歌手详情", description = "根据歌手ID查询详细信息")
    @GetMapping("/{id}")
    public ArtistDetailVO getDetail(
            @Parameter(description = "歌手ID", example = "1", required = true)
            @PathVariable Long id) {
        return artistService.getArtistDetail(id);
    }

    /**
     * 创建歌手
     *
     * @param createDTO 创建请求参数
     * @return 新增歌手的ID
     */
    @Operation(summary = "创建歌手", description = "新增一个歌手记录")
    @PostMapping
    public Long create(
            @Parameter(description = "歌手创建参数", required = true)
            @Valid @RequestBody ArtistCreateDTO createDTO) {
        return artistService.createArtist(createDTO);
    }

    /**
     * 更新歌手信息
     *
     * @param id        歌手ID
     * @param updateDTO 更新请求参数
     * @return 是否更新成功
     */
    @Operation(summary = "更新歌手信息", description = "根据歌手ID更新歌手信息")
    @PutMapping("/{id}")
    public Boolean update(
            @Parameter(description = "歌手ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "歌手更新参数", required = true)
            @Valid @RequestBody ArtistUpdateDTO updateDTO) {
        return artistService.updateArtist(id, updateDTO);
    }

    /**
     * 删除歌手（逻辑删除）
     *
     * @param id 歌手ID
     * @return 是否删除成功
     */
    @Operation(summary = "删除歌手", description = "根据歌手ID逻辑删除歌手")
    @DeleteMapping("/{id}")
    public Boolean delete(
            @Parameter(description = "歌手ID", example = "1", required = true)
            @PathVariable Long id) {
        return artistService.deleteArtist(id);
    }
}
