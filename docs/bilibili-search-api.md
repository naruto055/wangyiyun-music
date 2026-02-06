# B站视频搜索功能 - API对接文档

**文档版本**: v1.0
**更新日期**: 2026-02-05
**API版本**: v1
**服务地址**: `http://localhost:8910`

---

## 目录

- [1. 功能概述](#1-功能概述)
- [2. 基础信息](#2-基础信息)
- [3. 接口列表](#3-接口列表)
- [4. 接口详细说明](#4-接口详细说明)
  - [4.1 搜索B站视频](#41-搜索b站视频)
  - [4.2 从B站保存音乐](#42-从b站保存音乐)
  - [4.3 查询搜索历史](#43-查询搜索历史)
  - [4.4 清空搜索历史](#44-清空搜索历史)
- [5. 数据模型](#5-数据模型)
- [6. 错误码说明](#6-错误码说明)
- [7. 集成示例](#7-集成示例)
- [8. 常见问题](#8-常见问题)

---

## 1. 功能概述

B站视频搜索功能为网易云音乐平台提供了从B站搜索、播放和保存音乐的能力,主要功能包括:

- ✅ **视频搜索**: 通过关键词搜索B站视频,支持分页
- ✅ **音频播放**: 实时解析B站视频并提取音频播放
- ✅ **保存音乐**: 将B站视频音频永久保存到音乐库
- ✅ **搜索历史**: 自动记录搜索历史,支持查询和清空

### 核心特性

- **轻量设计**: 搜索结果仅返回核心字段(bvid、title、duration、url)
- **专注音乐**: 不关注UP主信息和视频统计数据
- **统一管理**: 保存后的音乐与平台音乐统一管理,无来源区分
- **智能提取**: 后端自动提取封面和时长信息,前端无需传递

---

## 2. 基础信息

### 2.1 API基础配置

| 配置项 | 值 | 说明 |
|--------|-----|------|
| **基础URL** | `http://localhost:8910` | 开发环境地址 |
| **API前缀** | `/api` | 所有接口统一前缀 |
| **请求格式** | `application/json` | 请求体使用JSON格式 |
| **响应格式** | `application/json` | 响应体使用JSON格式 |
| **字符编码** | `UTF-8` | 统一字符编码 |

### 2.2 统一响应格式

所有接口返回统一的 `Result` 封装格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": { }
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| code | Integer | 状态码(200=成功, 其他=失败) |
| message | String | 响应消息 |
| data | Object | 业务数据(失败时为null) |

### 2.3 分页响应格式

分页接口返回 MyBatis-Plus 标准分页对象:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 1500,
    "current": 1,
    "size": 20,
    "pages": 75,
    "records": []
  }
}
```

**分页字段说明**:

| 字段 | 类型 | 说明 |
|------|------|------|
| total | Long | 总记录数 |
| current | Integer | 当前页码(从1开始) |
| size | Integer | 每页大小 |
| pages | Integer | 总页数 |
| records | Array | 当前页数据列表 |

---

## 3. 接口列表

| 序号 | 接口名称 | 请求方式 | 接口路径 | 说明 |
|------|---------|---------|---------|------|
| 1 | 搜索B站视频 | POST | `/api/bilibili/search` | 根据关键词搜索B站视频 |
| 2 | 从B站保存音乐 | POST | `/api/music/from-bilibili` | 将B站视频保存为音乐 |
| 3 | 查询搜索历史 | GET | `/api/bilibili/search/history` | 查询用户搜索历史 |
| 4 | 清空搜索历史 | DELETE | `/api/bilibili/search/history` | 清空用户搜索历史 |

---

## 4. 接口详细说明

### 4.1 搜索B站视频

**接口描述**: 根据关键词搜索B站视频,返回分页结果。搜索时会自动记录到搜索历史。

#### 基本信息

- **请求方式**: `POST`
- **接口路径**: `/api/bilibili/search`
- **Content-Type**: `application/json`

#### 请求参数

**请求体 (Body)**:

```json
{
  "keyword": "周杰伦",
  "page": 1,
  "pageSize": 20
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 | 示例 |
|--------|------|------|--------|------|------|
| keyword | String | 是 | - | 搜索关键词 | 周杰伦 |
| page | Integer | 否 | 1 | 页码(从1开始) | 1 |
| pageSize | Integer | 否 | 20 | 每页大小(最大50) | 20 |

**参数校验规则**:

- `keyword`: 不能为空
- `page`: 最小值为1
- `pageSize`: 最小值为1,最大值为50

#### 响应示例

**成功响应 (200)**:

```json
{
  "code": 200,
  "message": "搜索成功",
  "data": {
    "total": 1500,
    "current": 1,
    "size": 20,
    "pages": 75,
    "records": [
      {
        "bvid": "BV1xx411c7XZ",
        "title": "周杰伦 - 晴天 官方MV",
        "duration": "4:35",
        "url": "https://www.bilibili.com/video/BV1xx411c7XZ"
      },
      {
        "bvid": "BV1yy422d8FG",
        "title": "周杰伦 - 七里香 现场版",
        "duration": "3:52",
        "url": "https://www.bilibili.com/video/BV1yy422d8FG"
      }
    ]
  }
}
```

**响应字段说明** (records数组元素):

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| bvid | String | B站视频BV号(唯一标识) | BV1xx411c7XZ |
| title | String | 视频标题(已清理高亮标签) | 周杰伦 - 晴天 官方MV |
| duration | String | 视频时长(格式化字符串) | 4:35 |
| url | String | B站视频完整链接 | https://www.bilibili.com/video/BV1xx411c7XZ |

**失败响应 (400)**:

```json
{
  "code": 400,
  "message": "搜索关键词不能为空",
  "data": null
}
```

**失败响应 (500)**:

```json
{
  "code": 500,
  "message": "搜索服务暂时不可用,请稍后重试",
  "data": null
}
```

#### 注意事项

- 搜索时会自动记录到搜索历史表
- 相同关键词重复搜索会增加搜索次数计数
- 标题中的HTML实体已自动解码
- 搜索高亮标签(`<em>`)已自动移除
- 时长格式为 `MM:SS` 或 `HH:MM:SS`

---

### 4.2 从B站保存音乐

**接口描述**: 将B站视频解析并保存为音乐到音乐库。后端会调用yt-dlp工具提取音频,并自动获取封面和时长信息。

#### 基本信息

- **请求方式**: `POST`
- **接口路径**: `/api/music/from-bilibili`
- **Content-Type**: `application/json`

#### 请求参数

**请求体 (Body)**:

```json
{
  "bvid": "BV1xx411c7XZ",
  "title": "周杰伦 - 晴天",
  "categoryId": 1
}
```

**参数说明**:

| 参数名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| bvid | String | 是 | B站视频BV号 | BV1xx411c7XZ |
| title | String | 是 | 音乐标题 | 周杰伦 - 晴天 |
| categoryId | Long | 否 | 音乐分类ID | 1 |

**参数校验规则**:

- `bvid`: 不能为空,必须是有效的BV号格式
- `title`: 不能为空
- `categoryId`: 可选,必须是有效的分类ID

**注意**:

- ❌ 无需传递 `coverUrl` - 由yt-dlp自动提取
- ❌ 无需传递 `duration` - 由yt-dlp自动提取
- ✅ 后端会自动处理封面和时长信息

#### 响应示例

**成功响应 (200)**:

```json
{
  "code": 200,
  "message": "保存成功",
  "data": {
    "musicId": 123,
    "audioUrl": "http://localhost:8910/audio/abc123.mp3"
  }
}
```

**响应字段说明**:

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| musicId | Long | 新创建的音乐ID | 123 |
| audioUrl | String | 音频访问URL | http://localhost:8910/audio/abc123.mp3 |

**失败响应 (400)**:

```json
{
  "code": 400,
  "message": "B站视频BV号不能为空",
  "data": null
}
```

**失败响应 (500)**:

```json
{
  "code": 500,
  "message": "视频解析失败,请稍后重试",
  "data": null
}
```

#### 处理流程

1. 验证 `bvid` 格式
2. 拼接B站视频URL: `https://www.bilibili.com/video/{bvid}`
3. 调用 `VideoParseService` 解析视频(使用yt-dlp)
4. 等待音频提取完成(通常3-10秒)
5. 将临时音频文件移动到永久存储路径
6. 从解析结果中自动提取封面和时长
7. 创建普通 `Music` 记录(与平台音乐无区别)
8. 返回音乐ID和音频URL

#### 注意事项

- 保存操作耗时较长(3-10秒),建议前端显示加载状态
- 音频文件永久保存,不会被定时清理
- 保存后的音乐与平台上传音乐完全一致
- 无来源标识,统一管理在音乐库中
- 如果视频已失效或不可访问,会返回500错误

---

### 4.3 查询搜索历史

**接口描述**: 查询用户搜索历史记录,按最后搜索时间倒序排列。

#### 基本信息

- **请求方式**: `GET`
- **接口路径**: `/api/bilibili/search/history`

#### 请求参数

**查询参数 (Query)**:

| 参数名 | 类型 | 必填 | 默认值 | 说明 | 示例 |
|--------|------|------|--------|------|------|
| page | Integer | 否 | 1 | 页码(从1开始) | 1 |
| pageSize | Integer | 否 | 10 | 每页大小 | 10 |

**请求示例**:

```
GET /api/bilibili/search/history?page=1&pageSize=10
```

#### 响应示例

**成功响应 (200)**:

```json
{
  "code": 200,
  "message": "查询成功",
  "data": {
    "total": 5,
    "current": 1,
    "size": 10,
    "pages": 1,
    "records": [
      {
        "id": 1,
        "keyword": "周杰伦",
        "searchCount": 5,
        "lastSearchTime": "2026-02-01 12:00:00"
      },
      {
        "id": 2,
        "keyword": "林俊杰",
        "searchCount": 3,
        "lastSearchTime": "2026-02-01 11:30:00"
      }
    ]
  }
}
```

**响应字段说明** (records数组元素):

| 字段 | 类型 | 说明 | 示例 |
|------|------|------|------|
| id | Long | 历史记录ID | 1 |
| keyword | String | 搜索关键词 | 周杰伦 |
| searchCount | Integer | 搜索次数(重复搜索累加) | 5 |
| lastSearchTime | String | 最后搜索时间 | 2026-02-01 12:00:00 |

#### 注意事项

- 记录按 `lastSearchTime` 降序排序(最近搜索的在前)
- 当前版本返回全局搜索历史(未来支持用户隔离)
- 搜索次数会随着重复搜索累加

---

### 4.4 清空搜索历史

**接口描述**: 清空用户所有搜索历史记录。

#### 基本信息

- **请求方式**: `DELETE`
- **接口路径**: `/api/bilibili/search/history`

#### 请求参数

无

**请求示例**:

```
DELETE /api/bilibili/search/history
```

#### 响应示例

**成功响应 (200)**:

```json
{
  "code": 200,
  "message": "搜索历史已清空",
  "data": null
}
```

#### 注意事项

- 当前版本清空全局搜索历史
- 未来扩展时可根据 `user_id` 清空个人历史
- 操作不可逆,请谨慎调用

---

## 5. 数据模型

### 5.1 BilibiliVideoVO (B站视频信息)

**说明**: 搜索接口返回的视频信息对象(简化版,仅包含核心字段)

```json
{
  "bvid": "BV1xx411c7XZ",
  "title": "周杰伦 - 晴天 官方MV",
  "duration": "4:35",
  "url": "https://www.bilibili.com/video/BV1xx411c7XZ"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| bvid | String | B站视频BV号(唯一标识符) |
| title | String | 视频标题(已清理高亮标签和HTML实体) |
| duration | String | 视频时长(格式: MM:SS 或 HH:MM:SS) |
| url | String | B站视频完整链接 |

**v1.3简化说明**:

- ❌ 移除 `cover` - 不展示封面
- ❌ 移除 `author` - 不关注UP主
- ❌ 移除 `authorFace` - 不关注UP主头像
- ❌ 移除 `play` - 不关注播放量
- ❌ 移除 `danmaku` - 不关注弹幕数
- ❌ 移除 `pubdate` - 不关注发布时间
- ❌ 移除 `description` - 不需要详细描述

**设计理念**: 专注音乐本身,不关注UP主信息和视频统计数据

---

### 5.2 SearchHistoryVO (搜索历史)

**说明**: 搜索历史记录对象

```json
{
  "id": 1,
  "keyword": "周杰伦",
  "searchCount": 5,
  "lastSearchTime": "2026-02-01 12:00:00"
}
```

| 字段 | 类型 | 说明 |
|------|------|------|
| id | Long | 历史记录ID |
| keyword | String | 搜索关键词 |
| searchCount | Integer | 搜索次数(重复搜索累加) |
| lastSearchTime | String | 最后搜索时间(格式: yyyy-MM-dd HH:mm:ss) |

---

### 5.3 SaveFromBilibiliDTO (保存请求参数)

**说明**: 从B站保存音乐的请求参数对象(v1.3简化版)

```json
{
  "bvid": "BV1xx411c7XZ",
  "title": "周杰伦 - 晴天",
  "categoryId": 1
}
```

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| bvid | String | 是 | B站视频BV号 |
| title | String | 是 | 音乐标题 |
| categoryId | Long | 否 | 音乐分类ID |

**v1.3简化说明**:

- ❌ 移除 `coverUrl` - 由yt-dlp自动提取
- ❌ 移除 `duration` - 由yt-dlp自动提取
- ❌ 移除 `author` - 不关注UP主信息

**优势**: 前端只需传递3个核心字段,后端自动处理封面和时长

---

## 6. 错误码说明

### 6.1 HTTP状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 请求成功 |
| 400 | 请求参数错误(如参数缺失、格式错误) |
| 403 | 权限不足(如限流) |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

### 6.2 业务错误码

| code | message | 说明 | 解决方案 |
|------|---------|------|---------|
| 200 | 操作成功 | 请求成功 | - |
| 400 | 搜索关键词不能为空 | 参数校验失败 | 检查请求参数 |
| 400 | B站视频BV号不能为空 | 参数校验失败 | 检查请求参数 |
| 400 | 页码必须大于0 | 参数校验失败 | 检查分页参数 |
| 400 | 分页大小不能超过50 | 参数校验失败 | 调整pageSize ≤ 50 |
| 500 | 搜索服务暂时不可用 | B站API异常 | 稍后重试 |
| 500 | 视频解析失败 | yt-dlp解析失败 | 检查视频是否可访问或稍后重试 |
| 500 | 音乐不存在 | 音乐ID无效 | 检查音乐ID是否正确 |

### 6.3 错误处理建议

**前端错误处理示例**:

```javascript
async function searchVideos(keyword) {
  try {
    const response = await fetch('/api/bilibili/search', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ keyword, page: 1, pageSize: 20 })
    });

    const result = await response.json();

    if (result.code === 200) {
      // 成功处理
      return result.data;
    } else {
      // 业务错误
      alert(result.message);
      return null;
    }
  } catch (error) {
    // 网络错误或服务器错误
    alert('网络错误,请检查连接');
    console.error(error);
    return null;
  }
}
```

---

## 7. 集成示例

### 7.1 JavaScript/TypeScript 集成

#### 示例1: 搜索B站视频

```javascript
/**
 * 搜索B站视频
 * @param {string} keyword - 搜索关键词
 * @param {number} page - 页码(默认1)
 * @param {number} pageSize - 每页大小(默认20)
 * @returns {Promise<Object>} 搜索结果
 */
async function searchBilibiliVideos(keyword, page = 1, pageSize = 20) {
  const response = await fetch('http://localhost:8910/api/bilibili/search', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({ keyword, page, pageSize })
  });

  const result = await response.json();

  if (result.code === 200) {
    return result.data; // { total, current, size, pages, records }
  } else {
    throw new Error(result.message);
  }
}

// 使用示例
searchBilibiliVideos('周杰伦', 1, 20)
  .then(data => {
    console.log(`总记录数: ${data.total}`);
    console.log(`当前页: ${data.current}/${data.pages}`);

    data.records.forEach(video => {
      console.log(`标题: ${video.title}`);
      console.log(`时长: ${video.duration}`);
      console.log(`链接: ${video.url}`);
      console.log(`BV号: ${video.bvid}`);
      console.log('---');
    });
  })
  .catch(error => {
    console.error('搜索失败:', error.message);
  });
```

#### 示例2: 保存B站音乐到音乐库

```javascript
/**
 * 从B站保存音乐到音乐库(v1.3简化版)
 * @param {Object} video - 视频对象
 * @param {string} video.bvid - BV号
 * @param {string} video.title - 标题
 * @param {number|null} categoryId - 分类ID(可选)
 * @returns {Promise<Object>} 保存结果
 */
async function saveMusicFromBilibili(video, categoryId = null) {
  // 显示加载提示
  showLoading('正在解析音频,请稍候...');

  try {
    const response = await fetch('http://localhost:8910/api/music/from-bilibili', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        bvid: video.bvid,
        title: video.title,
        categoryId: categoryId
      })
    });

    const result = await response.json();

    if (result.code === 200) {
      hideLoading();
      alert(`保存成功!音乐ID: ${result.data.musicId}`);
      return result.data; // { musicId, audioUrl }
    } else {
      hideLoading();
      alert(`保存失败: ${result.message}`);
      return null;
    }
  } catch (error) {
    hideLoading();
    alert('网络错误,请检查连接');
    console.error(error);
    return null;
  }
}

// 使用示例
const video = {
  bvid: 'BV1xx411c7XZ',
  title: '周杰伦 - 晴天 官方MV'
};

saveMusicFromBilibili(video, 1)
  .then(data => {
    if (data) {
      console.log('音乐ID:', data.musicId);
      console.log('音频URL:', data.audioUrl);
      // 可以跳转到音乐播放页面
      window.location.href = `/player?musicId=${data.musicId}`;
    }
  });
```

#### 示例3: 查询搜索历史

```javascript
/**
 * 查询搜索历史
 * @param {number} page - 页码(默认1)
 * @param {number} pageSize - 每页大小(默认10)
 * @returns {Promise<Object>} 搜索历史
 */
async function getSearchHistory(page = 1, pageSize = 10) {
  const response = await fetch(
    `http://localhost:8910/api/bilibili/search/history?page=${page}&pageSize=${pageSize}`,
    { method: 'GET' }
  );

  const result = await response.json();

  if (result.code === 200) {
    return result.data; // { total, current, size, pages, records }
  } else {
    throw new Error(result.message);
  }
}

// 使用示例
getSearchHistory(1, 10)
  .then(data => {
    console.log('搜索历史:');
    data.records.forEach(item => {
      console.log(`关键词: ${item.keyword}, 搜索次数: ${item.searchCount}`);
      console.log(`最后搜索: ${item.lastSearchTime}`);
      console.log('---');
    });
  })
  .catch(error => {
    console.error('查询失败:', error.message);
  });
```

#### 示例4: 清空搜索历史

```javascript
/**
 * 清空搜索历史
 * @returns {Promise<boolean>} 是否成功
 */
async function clearSearchHistory() {
  if (!confirm('确定要清空所有搜索历史吗?')) {
    return false;
  }

  const response = await fetch('http://localhost:8910/api/bilibili/search/history', {
    method: 'DELETE'
  });

  const result = await response.json();

  if (result.code === 200) {
    alert('搜索历史已清空');
    return true;
  } else {
    alert(`清空失败: ${result.message}`);
    return false;
  }
}

// 使用示例
clearSearchHistory().then(success => {
  if (success) {
    // 刷新历史列表
    getSearchHistory();
  }
});
```

### 7.2 Vue.js 集成示例

```vue
<template>
  <div class="bilibili-search">
    <!-- 搜索框 -->
    <div class="search-box">
      <input
        v-model="keyword"
        @keyup.enter="search"
        placeholder="搜索B站音乐..."
      />
      <button @click="search">搜索</button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      正在搜索...
    </div>

    <!-- 搜索结果 -->
    <div v-if="!loading && videos.length > 0" class="video-list">
      <div
        v-for="video in videos"
        :key="video.bvid"
        class="video-item"
      >
        <div class="video-info">
          <h3>{{ video.title }}</h3>
          <p>时长: {{ video.duration }}</p>
        </div>
        <div class="video-actions">
          <button @click="saveMusic(video)">保存到音乐库</button>
          <a :href="video.url" target="_blank">在B站打开</a>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div v-if="totalPages > 1" class="pagination">
      <button
        @click="changePage(currentPage - 1)"
        :disabled="currentPage === 1"
      >
        上一页
      </button>
      <span>{{ currentPage }} / {{ totalPages }}</span>
      <button
        @click="changePage(currentPage + 1)"
        :disabled="currentPage === totalPages"
      >
        下一页
      </button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BilibiliSearch',
  data() {
    return {
      keyword: '',
      videos: [],
      loading: false,
      currentPage: 1,
      pageSize: 20,
      totalPages: 0,
      total: 0
    };
  },
  methods: {
    async search() {
      if (!this.keyword.trim()) {
        alert('请输入搜索关键词');
        return;
      }

      this.loading = true;
      this.currentPage = 1;

      try {
        const response = await fetch('http://localhost:8910/api/bilibili/search', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            keyword: this.keyword,
            page: this.currentPage,
            pageSize: this.pageSize
          })
        });

        const result = await response.json();

        if (result.code === 200) {
          this.videos = result.data.records;
          this.total = result.data.total;
          this.totalPages = result.data.pages;
        } else {
          alert(result.message);
        }
      } catch (error) {
        alert('搜索失败,请检查网络连接');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },

    async changePage(page) {
      if (page < 1 || page > this.totalPages) return;

      this.currentPage = page;
      this.loading = true;

      try {
        const response = await fetch('http://localhost:8910/api/bilibili/search', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            keyword: this.keyword,
            page: this.currentPage,
            pageSize: this.pageSize
          })
        });

        const result = await response.json();

        if (result.code === 200) {
          this.videos = result.data.records;
        }
      } catch (error) {
        alert('加载失败');
        console.error(error);
      } finally {
        this.loading = false;
      }
    },

    async saveMusic(video) {
      const categoryId = null; // 可以让用户选择分类

      this.loading = true;

      try {
        const response = await fetch('http://localhost:8910/api/music/from-bilibili', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            bvid: video.bvid,
            title: video.title,
            categoryId: categoryId
          })
        });

        const result = await response.json();

        if (result.code === 200) {
          alert(`保存成功!音乐ID: ${result.data.musicId}`);
          // 可以跳转到音乐播放页面
          this.$router.push(`/player/${result.data.musicId}`);
        } else {
          alert(`保存失败: ${result.message}`);
        }
      } catch (error) {
        alert('保存失败,请检查网络连接');
        console.error(error);
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.bilibili-search {
  padding: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-box input {
  flex: 1;
  padding: 10px;
  font-size: 16px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.search-box button {
  padding: 10px 20px;
  font-size: 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.video-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  margin-bottom: 10px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.video-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
  margin-top: 20px;
}
</style>
```

### 7.3 React 集成示例

```jsx
import React, { useState } from 'react';

function BilibiliSearch() {
  const [keyword, setKeyword] = useState('');
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  const search = async () => {
    if (!keyword.trim()) {
      alert('请输入搜索关键词');
      return;
    }

    setLoading(true);
    setCurrentPage(1);

    try {
      const response = await fetch('http://localhost:8910/api/bilibili/search', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ keyword, page: 1, pageSize: 20 })
      });

      const result = await response.json();

      if (result.code === 200) {
        setVideos(result.data.records);
        setTotalPages(result.data.pages);
      } else {
        alert(result.message);
      }
    } catch (error) {
      alert('搜索失败,请检查网络连接');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const saveMusic = async (video) => {
    setLoading(true);

    try {
      const response = await fetch('http://localhost:8910/api/music/from-bilibili', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          bvid: video.bvid,
          title: video.title,
          categoryId: null
        })
      });

      const result = await response.json();

      if (result.code === 200) {
        alert(`保存成功!音乐ID: ${result.data.musicId}`);
      } else {
        alert(`保存失败: ${result.message}`);
      }
    } catch (error) {
      alert('保存失败');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bilibili-search">
      <div className="search-box">
        <input
          value={keyword}
          onChange={(e) => setKeyword(e.target.value)}
          onKeyUp={(e) => e.key === 'Enter' && search()}
          placeholder="搜索B站音乐..."
        />
        <button onClick={search}>搜索</button>
      </div>

      {loading && <div className="loading">正在搜索...</div>}

      {!loading && videos.length > 0 && (
        <div className="video-list">
          {videos.map((video) => (
            <div key={video.bvid} className="video-item">
              <div className="video-info">
                <h3>{video.title}</h3>
                <p>时长: {video.duration}</p>
              </div>
              <div className="video-actions">
                <button onClick={() => saveMusic(video)}>保存到音乐库</button>
                <a href={video.url} target="_blank" rel="noopener noreferrer">
                  在B站打开
                </a>
              </div>
            </div>
          ))}
        </div>
      )}

      {totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setCurrentPage(currentPage - 1)}
            disabled={currentPage === 1}
          >
            上一页
          </button>
          <span>{currentPage} / {totalPages}</span>
          <button
            onClick={() => setCurrentPage(currentPage + 1)}
            disabled={currentPage === totalPages}
          >
            下一页
          </button>
        </div>
      )}
    </div>
  );
}

export default BilibiliSearch;
```

---

## 8. 常见问题

### 8.1 搜索相关问题

**Q1: 搜索时提示"搜索服务暂时不可用"怎么办?**

A: 可能是以下原因:
- B站API临时不可用,请稍后重试
- 网络连接问题,检查网络设置
- 服务器负载过高,等待片刻后重试

**Q2: 搜索结果为空是什么原因?**

A: 可能是以下原因:
- 关键词过于具体,建议使用更通用的关键词
- B站确实没有相关视频
- 建议搜索:"歌手名 + 歌曲名"格式

**Q3: 搜索结果中为什么没有封面图?**

A: v1.3版本为了简化设计,专注音乐本身,移除了封面、UP主等信息。如需查看视频详情,请点击视频链接在B站打开。

**Q4: 搜索历史记录能删除单条吗?**

A: 当前版本仅支持清空全部历史,单条删除功能计划在未来版本支持。

---

### 8.2 保存相关问题

**Q5: 为什么保存音乐需要这么长时间?**

A: 保存音乐需要以下步骤:
1. 调用yt-dlp解析B站视频(3-10秒)
2. 提取音频文件
3. 移动到永久存储路径
4. 创建数据库记录

建议在前端显示"正在解析音频,请稍候..."提示。

**Q6: 保存时提示"视频解析失败"怎么办?**

A: 可能是以下原因:
- 视频已被UP主删除或下架
- 视频设置了地区限制
- 视频为付费内容
- 网络不稳定导致下载失败

解决方案:
- 尝试搜索其他相似音乐
- 检查视频在B站是否可正常播放
- 稍后重试

**Q7: 保存的音乐为什么没有封面和时长?**

A: v1.3版本会自动从yt-dlp解析结果中提取封面和时长,如果某些视频没有这些信息:
- 可能是视频本身未提供封面
- 可能是解析失败
- 可以手动在音乐管理界面编辑补充

**Q8: 保存后的音乐在哪里查看?**

A: 保存后的音乐会自动添加到音乐库,与平台上传的音乐统一管理,可以通过以下方式查看:
- 音乐列表页面(按创建时间倒序)
- 根据分类筛选(如果保存时选择了分类)
- 通过音乐搜索功能查找

---

### 8.3 技术相关问题

**Q9: API调用频率有限制吗?**

A: 当前版本暂无明确限制,但建议:
- 搜索频率不要过高(建议间隔至少1秒)
- 避免短时间内批量保存大量音乐
- 未来可能会增加IP限流和用户限流

**Q10: 接口支持跨域请求吗?**

A: 服务端已配置CORS跨域支持,前端可以直接调用。如遇跨域问题:
- 检查请求头是否正确设置
- 确认服务端CORS配置是否生效
- 考虑使用代理解决开发环境跨域问题

**Q11: 接口是否需要认证?**

A: 当前版本所有接口均无需认证,直接调用即可。未来版本可能会增加用户认证系统,届时需要在请求头中携带Token。

**Q12: 数据格式有特殊要求吗?**

A: 注意以下几点:
- 请求体必须使用JSON格式
- Content-Type必须设置为 `application/json`
- 字符编码统一使用UTF-8
- 时间格式统一使用 `yyyy-MM-dd HH:mm:ss`

**Q13: 如何处理网络超时?**

A: 建议设置合理的超时时间:
- 搜索接口: 10秒超时
- 保存接口: 30秒超时(因为需要解析视频)
- 历史查询接口: 5秒超时

**Q14: 接口返回数据过大怎么办?**

A: 当前设计已考虑数据量:
- 搜索结果每页最多50条(推荐20条)
- VO对象已简化,仅包含核心字段
- 分页查询避免一次性加载过多数据

---

### 8.4 前端集成问题

**Q15: 前端如何显示视频时长?**

A: 后端返回的 `duration` 字段已格式化为字符串(如"4:35"),可直接显示。如需转换为秒数:

```javascript
function parseDuration(duration) {
  const parts = duration.split(':').map(Number);
  if (parts.length === 2) {
    // MM:SS
    return parts[0] * 60 + parts[1];
  } else if (parts.length === 3) {
    // HH:MM:SS
    return parts[0] * 3600 + parts[1] * 60 + parts[2];
  }
  return 0;
}
```

**Q16: 如何实现搜索防抖?**

A: 建议使用防抖函数避免频繁搜索:

```javascript
function debounce(func, delay) {
  let timer;
  return function(...args) {
    clearTimeout(timer);
    timer = setTimeout(() => func.apply(this, args), delay);
  };
}

const debouncedSearch = debounce(search, 500);
```

**Q17: 如何显示保存进度?**

A: 保存操作耗时较长,建议:
1. 显示加载状态:"正在解析音频,请稍候..."
2. 禁用保存按钮,避免重复点击
3. 可选:显示进度条或加载动画

**Q18: 如何处理BV号?**

A: BV号是B站视频的唯一标识符:
- 格式: `BV` + 10位字符(如 `BV1xx411c7XZ`)
- 可从视频URL中提取: `https://www.bilibili.com/video/BV1xx411c7XZ`
- 搜索结果已包含 `bvid` 字段,直接使用即可

---

### 8.5 设计理念问题

**Q19: 为什么不显示UP主信息?**

A: v1.3版本采用"专注音乐本身"的设计理念:
- 用户关注的是音乐,而非UP主
- 简化前端界面,减少信息干扰
- 减少API响应数据量,提升性能
- 如需查看视频详情,可点击视频链接在B站打开

**Q20: 保存后的音乐为什么看不到来源?**

A: 设计理念:
- 从B站保存的音乐就是"平台音乐",不区分来源
- 统一用户认知:"全部是我的音乐"
- 降低版权风险,不明确标识外部来源
- 提供统一的音乐管理体验

---

## 9. 更新日志

### v1.0 (2026-02-05)

- ✅ 初始版本发布
- ✅ 支持B站视频搜索(分页)
- ✅ 支持从B站保存音乐到音乐库
- ✅ 支持搜索历史记录和查询
- ✅ 采用v1.3简化设计(专注音乐本身)
- ✅ 后端自动提取封面和时长
- ✅ 前端集成示例(JavaScript/Vue/React)

---

## 10. 联系与支持

### 技术支持

- **项目地址**: `d:\JavaCodeStudy\wangyiyun-music`
- **API文档**: http://localhost:8910/swagger-ui/index.html
- **开发环境**: Spring Boot 3.1.0 + Java 17

### 相关文档

- [详细规划文档](./.claude/plan/video-search-feature.md)
- [项目CLAUDE.md](./CLAUDE.md)
- [Swagger API文档](http://localhost:8910/swagger-ui/index.html)

---

**文档结束**

最后更新时间: 2026-02-05
