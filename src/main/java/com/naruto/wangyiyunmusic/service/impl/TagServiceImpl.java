package com.naruto.wangyiyunmusic.service.impl;

import com.naruto.wangyiyunmusic.model.entity.Tag;
import com.naruto.wangyiyunmusic.mapper.TagMapper;
import com.naruto.wangyiyunmusic.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 标签表 服务实现类
 * </p>
 *
 * @author naruto
 * @since 2026-01-24
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

}
