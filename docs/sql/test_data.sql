-- 测试数据脚本
-- 作者: naruto
-- 创建时间: 2026-01-24

USE `wangyiyun_music`;

-- 插入分类数据
INSERT INTO `category` (`name`, `sort_order`) VALUES
('流行', 1),
('摇滚', 2),
('古典', 3),
('民谣', 4),
('电子', 5),
('说唱', 6);

-- 插入歌手数据
INSERT INTO `artist` (`name`, `country`, `description`) VALUES
('周杰伦', '中国台湾', '华语流行音乐天王，创作型歌手'),
('林俊杰', '新加坡', '新加坡创作型男歌手，音乐制作人'),
('陈奕迅', '中国香港', '香港著名男歌手，被誉为"歌神接班人"'),
('邓紫棋', '中国香港', '香港创作型女歌手，铁肺天后'),
('薛之谦', '中国', '中国流行男歌手、词曲创作人');

-- 插入专辑数据
INSERT INTO `album` (`name`, `release_date`, `description`) VALUES
('Jay', '2000-11-07', '周杰伦首张个人专辑'),
('范特西', '2001-09-14', '周杰伦第二张录音室专辑'),
('叶惠美', '2003-07-31', '周杰伦第四张录音室专辑'),
('八度空间', '2002-07-18', '周杰伦第三张录音室专辑'),
('JJ陆', '2008-10-18', '林俊杰第六张录音室专辑');

-- 插入标签数据
INSERT INTO `tag` (`name`) VALUES
('华语'),
('抒情'),
('快歌'),
('经典'),
('怀旧'),
('校园'),
('治愈'),
('励志');

-- 插入音乐数据
INSERT INTO `music` (`title`, `album_id`, `category_id`, `duration`, `play_count`, `favorite_count`, `release_date`, `lyrics`, `file_url`, `cover_url`) VALUES
('晴天', 3, 1, 269, 15678, 3245, '2003-07-31',
'故事的小黄花\n从出生那年就飘着\n童年的荡秋千\n随记忆一直晃到现在\nRe So So Si Do Si La\nSo La Si Si Si Si La Si La So\n吹着前奏望着天空我\n想起花瓣试着掉落',
'http://music.example.com/sunny_day.mp3',
'http://music.example.com/covers/sunny_day.jpg'),

('七里香', 2, 1, 300, 12543, 2876, '2004-08-03',
'窗外的麻雀在电线杆上多嘴\n你说这一句很有夏天的感觉\n手中的铅笔在画纸上来来回回\n我用几行字形容你是我的谁',
'http://music.example.com/qi_li_xiang.mp3',
'http://music.example.com/covers/qi_li_xiang.jpg'),

('稻香', 4, 4, 223, 18923, 4532, '2008-10-15',
'对这个世界如果你有太多的抱怨\n跌倒了就不敢继续往前走\n为什么人要这么的脆弱 堕落\n请你打开电视看看\n多少人为生命在努力勇敢的走下去',
'http://music.example.com/dao_xiang.mp3',
'http://music.example.com/covers/dao_xiang.jpg'),

('江南', 5, 1, 243, 9876, 1987, '2004-06-03',
'风到这里就是粘\n粘住过客的思念\n雨到了这里缠成线\n缠着我们流连人世间',
'http://music.example.com/jiang_nan.mp3',
'http://music.example.com/covers/jiang_nan.jpg'),

('红豆', NULL, 1, 287, 7654, 1543, '1998-11-10',
'还没好好的感受\n雪花绽放的气候\n我们一起颤抖\n会更明白什么是温柔',
'http://music.example.com/hong_dou.mp3',
'http://music.example.com/covers/hong_dou.jpg'),

('演员', NULL, 1, 265, 21345, 5678, '2015-06-05',
'简单点说话的方式简单点\n递进的情绪请省略\n你又不是个演员\n别设计那些情节',
'http://music.example.com/yan_yuan.mp3',
'http://music.example.com/covers/yan_yuan.jpg');

-- 插入音乐-歌手关联数据
INSERT INTO `music_artist` (`music_id`, `artist_id`, `artist_role`) VALUES
(1, 1, 'singer'),
(1, 1, 'composer'),
(2, 1, 'singer'),
(2, 1, 'composer'),
(3, 1, 'singer'),
(3, 1, 'composer'),
(3, 1, 'lyricist'),
(4, 2, 'singer'),
(4, 2, 'composer'),
(5, 3, 'singer'),
(6, 5, 'singer'),
(6, 5, 'composer'),
(6, 5, 'lyricist');

-- 插入音乐-标签关联数据
INSERT INTO `music_tag` (`music_id`, `tag_id`) VALUES
(1, 1), -- 晴天 - 华语
(1, 2), -- 晴天 - 抒情
(1, 4), -- 晴天 - 经典
(1, 6), -- 晴天 - 校园
(2, 1), -- 七里香 - 华语
(2, 2), -- 七里香 - 抒情
(2, 4), -- 七里香 - 经典
(3, 1), -- 稻香 - 华语
(3, 7), -- 稻香 - 治愈
(3, 8), -- 稻香 - 励志
(4, 1), -- 江南 - 华语
(4, 2), -- 江南 - 抒情
(5, 1), -- 红豆 - 华语
(5, 2), -- 红豆 - 抒情
(5, 4), -- 红豆 - 经典
(6, 1), -- 演员 - 华语
(6, 2); -- 演员 - 抒情

-- 插入播放记录示例数据
INSERT INTO `play_record` (`music_id`, `user_id`, `play_duration`, `play_source`) VALUES
(1, 0, 269, 'web'),
(2, 0, 300, 'web'),
(3, 0, 223, 'app'),
(1, 0, 180, 'h5');

-- 插入收藏示例数据
INSERT INTO `favorite` (`music_id`, `user_id`) VALUES
(1, 0),
(3, 0);
