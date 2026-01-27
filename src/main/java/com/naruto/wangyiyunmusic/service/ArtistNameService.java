package com.naruto.wangyiyunmusic.service;

import java.util.List;

/**
 * 歌手名称填充服务接口
 *
 * <p>提供统一的歌手名称批量填充功能，避免 N+1 查询问题</p>
 *
 * @Author: naruto
 * @CreateTime: 2026-01-27
 */
public interface ArtistNameService {

    /**
     * 批量填充歌手名称（避免 N+1 问题）
     *
     * <p>该方法通过批量查询优化性能：</p>
     * <ol>
     *     <li>批量查询音乐-歌手关联关系</li>
     *     <li>批量查询歌手信息</li>
     *     <li>按音乐ID分组并拼接歌手名称</li>
     *     <li>填充到VO对象</li>
     * </ol>
     *
     * @param voList 实现了 ArtistNameFillable 接口的 VO 列表
     * @param <T> VO 类型，必须实现 ArtistNameFillable 接口
     */
    <T extends ArtistNameFillable> void fillArtistNames(List<T> voList);
}
