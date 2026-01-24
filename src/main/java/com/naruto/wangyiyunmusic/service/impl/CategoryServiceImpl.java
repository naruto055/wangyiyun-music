package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.model.entity.Category;
import com.naruto.wangyiyunmusic.mapper.CategoryMapper;
import com.naruto.wangyiyunmusic.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 音乐分类表 服务实现类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
