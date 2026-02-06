package com.naruto.wangyiyunmusic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.naruto.wangyiyunmusic.model.entity.SearchHistory;
import org.apache.ibatis.annotations.Param;

/**
 * 搜索历史 Mapper 接口
 *
 * <p>提供搜索历史数据访问功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
public interface SearchHistoryMapper extends BaseMapper<SearchHistory> {

    /**
     * 根据用户ID和关键词查询历史记录
     *
     * @param userId 用户ID（可为NULL）
     * @param keyword 搜索关键词
     * @return 历史记录
     */
    SearchHistory selectByUserAndKeyword(@Param("userId") Long userId, @Param("keyword") String keyword);

    /**
     * 增加搜索次数
     *
     * @param id 历史记录ID
     * @return 影响行数
     */
    int incrementSearchCount(@Param("id") Long id);
}
