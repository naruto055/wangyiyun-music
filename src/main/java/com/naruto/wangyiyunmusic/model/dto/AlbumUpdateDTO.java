package com.naruto.wangyiyunmusic.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

/**
 * 专辑更新请求DTO
 *
 * @Author: naruto
 * @CreateTime: 2026-02-01
 */
@Data
@Schema(description = "专辑更新请求")
public class AlbumUpdateDTO {
    /**
     * 专辑名称
     */
    @NotBlank(message = "专辑名称不能为空")
    @Size(min = 1, max = 200, message = "专辑名称长度必须在1-200之间")
    @Schema(description = "专辑名称", example = "范特西", required = true)
    private String name;

    /**
     * 封面URL
     */
    @URL(message = "封面URL格式不正确")
    @Size(max = 500, message = "封面URL长度不能超过500")
    @Schema(description = "封面URL", example = "https://example.com/cover.jpg")
    private String coverUrl;

    /**
     * 专辑简介
     */
    @Schema(description = "专辑简介", example = "周杰伦第二张专辑")
    private String description;

    /**
     * 发行日期
     */
    @Schema(description = "发行日期", example = "2001-09-14")
    private LocalDate releaseDate;
}
