package com.naruto.wangyiyunmusic.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * B站Cookie信息
 *
 * <p>用于缓存 B 站 API 调用所需的 Cookie(buvid3 和 buvid4)</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Data
@Schema(description = "B站Cookie信息")
public class BilibiliCookieVO {

    @Schema(description = "buvid3", example = "xxxxxxxx")
    private String b3;

    @Schema(description = "buvid4", example = "yyyyyyyy")
    private String b4;

    @Schema(description = "过期时间", example = "2026-02-06 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}
