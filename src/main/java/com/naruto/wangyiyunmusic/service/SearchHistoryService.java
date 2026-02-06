package com.naruto.wangyiyunmusic.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.naruto.wangyiyunmusic.model.entity.PlayRecord;
import com.naruto.wangyiyunmusic.model.entity.SearchHistory;
import com.naruto.wangyiyunmusic.model.vo.SearchHistoryVO;

/**
 * 搜索历史服务接口
 *
 * <p>提供搜索历史记录、查询、清空等功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
public interface SearchHistoryService extends IService<SearchHistory> {

    /**
     * 记录搜索历史（幂等，重复搜索增加计数）
     *
     * @param userId 用户ID（当前可为NULL）
     * @param keyword 搜索关键词
     */
    void recordSearch(Long userId, String keyword);

    /**
     * 查询搜索历史
     *
     * @param userId 用户ID（当前可为NULL）
     * @param page 页码
     * @param pageSize 分页大小
     * @return 分页搜索历史
     */
    IPage<SearchHistoryVO> getHistory(Long userId, Integer page, Integer pageSize);

    /**
     * 清空搜索历史
     *
     * @param userId 用户ID（当前可为NULL）
     */
    void clearHistory(Long userId);
}
