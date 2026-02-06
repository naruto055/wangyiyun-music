package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.naruto.wangyiyunmusic.model.dto.BilibiliSearchDTO;
import com.naruto.wangyiyunmusic.model.vo.BilibiliCookieVO;
import com.naruto.wangyiyunmusic.model.vo.BilibiliVideoVO;

/**
 * B站视频搜索服务接口
 *
 * <p>提供 B 站视频搜索功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
public interface BilibiliSearchService {

    /**
     * 搜索B站视频
     *
     * @param searchDTO 搜索请求参数
     * @return 分页搜索结果
     */
    IPage<BilibiliVideoVO> searchVideos(BilibiliSearchDTO searchDTO);

    /**
     * 获取B站Cookie（buvid3和buvid4）
     *
     * @return Cookie信息
     */
    BilibiliCookieVO getBilibiliCookie();
}
