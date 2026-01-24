-- 网易云音乐数据库建表脚本
-- 作者: naruto
-- 创建时间: 2026-01-24

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `wangyiyun_music` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `wangyiyun_music`;

-- 1. 音乐表
DROP TABLE IF EXISTS `music`;
CREATE TABLE `music` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `title` VARCHAR(200) NOT NULL COMMENT '歌曲标题',
  `album_id` BIGINT DEFAULT NULL COMMENT '专辑ID',
  `category_id` BIGINT DEFAULT NULL COMMENT '分类ID',
  `duration` INT NOT NULL DEFAULT 0 COMMENT '时长(秒)',
  `file_url` VARCHAR(500) DEFAULT NULL COMMENT '音乐文件URL',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面图URL',
  `lyrics` TEXT COMMENT '歌词',
  `play_count` BIGINT NOT NULL DEFAULT 0 COMMENT '播放次数',
  `favorite_count` INT NOT NULL DEFAULT 0 COMMENT '收藏次数',
  `release_date` DATE DEFAULT NULL COMMENT '发行日期',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-未删除 1-已删除)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_album` (`album_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_play_count` (`play_count`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐表';

-- 2. 歌手表
DROP TABLE IF EXISTS `artist`;
CREATE TABLE `artist` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(100) NOT NULL COMMENT '歌手名称',
  `avatar_url` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `description` TEXT COMMENT '简介',
  `country` VARCHAR(50) DEFAULT NULL COMMENT '国家/地区',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='歌手表';

-- 3. 专辑表
DROP TABLE IF EXISTS `album`;
CREATE TABLE `album` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(200) NOT NULL COMMENT '专辑名称',
  `cover_url` VARCHAR(500) DEFAULT NULL COMMENT '封面URL',
  `description` TEXT COMMENT '专辑简介',
  `release_date` DATE DEFAULT NULL COMMENT '发行日期',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='专辑表';

-- 4. 分类表
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称(流行/摇滚/古典等)',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐分类表';

-- 5. 标签表
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='标签表';

-- 6. 音乐-歌手关联表
DROP TABLE IF EXISTS `music_artist`;
CREATE TABLE `music_artist` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `artist_id` BIGINT NOT NULL COMMENT '歌手ID',
  `artist_role` VARCHAR(20) DEFAULT 'singer' COMMENT '角色(singer-歌手/composer-作曲/lyricist-作词)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_music_artist` (`music_id`, `artist_id`, `artist_role`),
  KEY `idx_music` (`music_id`),
  KEY `idx_artist` (`artist_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐歌手关联表';

-- 7. 音乐-标签关联表
DROP TABLE IF EXISTS `music_tag`;
CREATE TABLE `music_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_music_tag` (`music_id`, `tag_id`),
  KEY `idx_music` (`music_id`),
  KEY `idx_tag` (`tag_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='音乐标签关联表';

-- 8. 播放记录表
DROP TABLE IF EXISTS `play_record`;
CREATE TABLE `play_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `user_id` BIGINT DEFAULT 0 COMMENT '用户ID(暂时默认0,后续扩展)',
  `play_duration` INT NOT NULL DEFAULT 0 COMMENT '播放时长(秒)',
  `play_source` VARCHAR(50) DEFAULT NULL COMMENT '播放来源(app/web/h5)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '播放时间',
  PRIMARY KEY (`id`),
  KEY `idx_music` (`music_id`),
  KEY `idx_user` (`user_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='播放记录表';

-- 9. 收藏表
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `music_id` BIGINT NOT NULL COMMENT '音乐ID',
  `user_id` BIGINT DEFAULT 0 COMMENT '用户ID(暂时默认0,后续扩展)',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0-收藏中 1-已取消)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_music` (`user_id`, `music_id`),
  KEY `idx_music` (`music_id`),
  KEY `idx_user` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';
