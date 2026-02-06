-- ========================================
-- 搜索历史表建表SQL
-- 功能：记录B站视频搜索历史
-- 作者：naruto
-- 创建时间：2026-02-05
-- ========================================

CREATE TABLE `search_history` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NULL COMMENT '用户ID（预留，当前可为NULL）',
  `keyword` VARCHAR(255) NOT NULL COMMENT '搜索关键词',
  `search_count` INT NOT NULL DEFAULT 1 COMMENT '搜索次数',
  `last_search_time` DATETIME NOT NULL COMMENT '最后搜索时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_keyword` (`user_id`, `keyword`),
  KEY `idx_last_search` (`last_search_time` DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='搜索历史表';

-- ========================================
-- 索引说明
-- ========================================
-- 1. idx_user_keyword: 唯一索引，防止同一用户重复记录相同关键词
-- 2. idx_last_search: 普通索引，优化按最后搜索时间排序的查询

-- ========================================
-- 使用说明
-- ========================================
-- 1. user_id 当前允许为 NULL，为未来用户系统预留扩展性
-- 2. search_count 记录搜索次数，重复搜索时自增
-- 3. last_search_time 记录最后一次搜索的时间，用于排序
