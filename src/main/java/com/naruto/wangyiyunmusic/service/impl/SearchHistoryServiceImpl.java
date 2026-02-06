package com.naruto.wangyiyunmusic.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.naruto.wangyiyunmusic.mapper.SearchHistoryMapper;
import com.naruto.wangyiyunmusic.mapper.TagMapper;
import com.naruto.wangyiyunmusic.model.entity.SearchHistory;
import com.naruto.wangyiyunmusic.model.entity.Tag;
import com.naruto.wangyiyunmusic.model.vo.SearchHistoryVO;
import com.naruto.wangyiyunmusic.service.SearchHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索历史服务实现类
 *
 * <p>实现搜索历史的记录、查询、清空功能</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-02-05
 */
@Slf4j
@Service
public class SearchHistoryServiceImpl  extends ServiceImpl<SearchHistoryMapper, SearchHistory> implements SearchHistoryService {

    @Autowired
    private SearchHistoryMapper searchHistoryMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordSearch(Long userId, String keyword) {
        log.info("记录搜索历史, userId: {}, keyword: {}", userId, keyword);

        // 查询是否已有相同关键词的历史记录
        SearchHistory existingHistory = searchHistoryMapper.selectByUserAndKeyword(userId, keyword);

        if (existingHistory != null) {
            // 已存在，增加搜索次数
            log.info("搜索历史已存在，增加计数, historyId: {}", existingHistory.getId());
            searchHistoryMapper.incrementSearchCount(existingHistory.getId());
        } else {
            // 不存在，插入新记录
            log.info("创建新搜索历史记录");
            SearchHistory newHistory = new SearchHistory();
            newHistory.setUserId(userId);
            newHistory.setKeyword(keyword);
            newHistory.setSearchCount(1);
            newHistory.setLastSearchTime(LocalDateTime.now());
            newHistory.setCreateTime(LocalDateTime.now());
            newHistory.setUpdateTime(LocalDateTime.now());
            searchHistoryMapper.insert(newHistory);
        }

        log.info("搜索历史记录成功");
    }

    @Override
    public IPage<SearchHistoryVO> getHistory(Long userId, Integer page, Integer pageSize) {
        log.info("查询搜索历史, userId: {}, page: {}, pageSize: {}", userId, page, pageSize);

        // 构建查询条件
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();

        // 用户ID条件（处理NULL情况）
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        } else {
            queryWrapper.isNull("user_id");
        }

        // 按最后搜索时间降序排序
        queryWrapper.orderByDesc("last_search_time");

        // 分页查询
        Page<SearchHistory> pageParam = new Page<>(page, pageSize);
        IPage<SearchHistory> historyPage = searchHistoryMapper.selectPage(pageParam, queryWrapper);

        // 转换为VO
        List<SearchHistoryVO> voList = historyPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 构建返回结果
        Page<SearchHistoryVO> resultPage = new Page<>(page, pageSize);
        resultPage.setRecords(voList);
        resultPage.setTotal(historyPage.getTotal());

        log.info("查询搜索历史成功, 共 {} 条记录", historyPage.getTotal());
        return resultPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearHistory(Long userId) {
        log.info("清空搜索历史, userId: {}", userId);

        // 构建删除条件
        QueryWrapper<SearchHistory> queryWrapper = new QueryWrapper<>();

        // 用户ID条件（处理NULL情况）
        if (userId != null) {
            queryWrapper.eq("user_id", userId);
        } else {
            queryWrapper.isNull("user_id");
        }

        int deletedCount = searchHistoryMapper.delete(queryWrapper);

        log.info("搜索历史清空成功, 删除 {} 条记录", deletedCount);
    }

    /**
     * 转换为VO
     *
     * @param entity 实体对象
     * @return VO对象
     */
    private SearchHistoryVO convertToVO(SearchHistory entity) {
        SearchHistoryVO vo = new SearchHistoryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
