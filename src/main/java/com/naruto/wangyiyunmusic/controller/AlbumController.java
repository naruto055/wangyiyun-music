package com.naruto.wangyiyunmusic.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.dto.AlbumCreateDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumQueryDTO;
import com.naruto.wangyiyunmusic.model.dto.AlbumUpdateDTO;
import com.naruto.wangyiyunmusic.model.vo.AlbumDetailVO;
import com.naruto.wangyiyunmusic.model.vo.AlbumListVO;
import com.naruto.wangyiyunmusic.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 专辑表 前端控制器
 *
 * <p>提供专辑管理相关的 RESTful API</p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Tag(name = "专辑管理", description = "提供专辑的增删改查接口")
@RestController
@RequestMapping("/api/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

    /**
     * 分页查询专辑列表
     *
     * @param queryDTO 查询条件
     * @return 分页结果（包含歌曲数量）
     */
    @Operation(summary = "分页查询专辑列表", description = "支持按关键词搜索、排序，返回包含歌曲数量的专辑列表")
    @GetMapping("/list")
    public IPage<AlbumListVO> list(
            @Parameter(description = "查询条件，包含关键词、排序字段、页码、每页大小等")
            AlbumQueryDTO queryDTO) {
        return albumService.pageQuery(queryDTO);
    }

    /**
     * 获取专辑详情
     *
     * @param id 专辑ID
     * @return 专辑详情（包含歌曲数量）
     */
    @Operation(summary = "获取专辑详情", description = "根据专辑ID查询详细信息，包含歌曲数量")
    @GetMapping("/{id}")
    public AlbumDetailVO getDetail(
            @Parameter(description = "专辑ID", example = "1", required = true)
            @PathVariable Long id) {
        return albumService.getAlbumDetail(id);
    }

    /**
     * 创建专辑
     *
     * @param createDTO 创建请求参数
     * @return 新增专辑的ID
     */
    @Operation(summary = "创建专辑", description = "新增一个专辑记录")
    @PostMapping
    public Long create(
            @Parameter(description = "专辑创建参数", required = true)
            @Valid @RequestBody AlbumCreateDTO createDTO) {
        return albumService.createAlbum(createDTO);
    }

    /**
     * 更新专辑信息
     *
     * @param id        专辑ID
     * @param updateDTO 更新请求参数
     * @return 是否更新成功
     */
    @Operation(summary = "更新专辑信息", description = "根据专辑ID更新专辑信息")
    @PutMapping("/{id}")
    public Boolean update(
            @Parameter(description = "专辑ID", example = "1", required = true)
            @PathVariable Long id,
            @Parameter(description = "专辑更新参数", required = true)
            @Valid @RequestBody AlbumUpdateDTO updateDTO) {
        return albumService.updateAlbum(id, updateDTO);
    }

    /**
     * 删除专辑（逻辑删除）
     *
     * @param id 专辑ID
     * @return 是否删除成功
     */
    @Operation(summary = "删除专辑", description = "根据专辑ID逻辑删除专辑（需检查是否有关联歌曲）")
    @DeleteMapping("/{id}")
    public Boolean delete(
            @Parameter(description = "专辑ID", example = "1", required = true)
            @PathVariable Long id) {
        return albumService.deleteAlbum(id);
    }
}

