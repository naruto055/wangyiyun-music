# 测试脚本 - B站原始分享链接支持

## 🧪 测试目标

验证系统是否支持B站原始分享文本（包含标题），以及保持向后兼容性。

---

## 📋 测试前准备

### 1. 启动应用

```bash
# 停止旧版本（如果正在运行）
jps | grep WangyiyunMusicApplication

# 如果有进程在运行，停止它
# Windows:
# taskkill /F /PID <进程ID>
# 或使用 PowerShell:
# Stop-Process -Id <进程ID> -Force

# 启动新版本
cd d:\JavaCodeStudy\wangyiyun-music
java -jar target/wangyiyun-music-0.0.1-SNAPSHOT.jar
```

### 2. 等待应用启动

```bash
# 等待应用完全启动（大约20秒）
# 检查应用状态
curl -s -o /dev/null -w "%{http_code}" http://localhost:8910/swagger-ui/index.html
# 应返回 302（重定向）或 200
```

---

## 🎯 测试用例

### **测试用例 1: 原始分享文本（新功能）** ⭐

**描述**: 直接粘贴B站复制的分享文本（包含标题）

**准备工作**:
1. 打开B站视频页面（例如：https://www.bilibili.com/video/BV1RcUQBWEYN/）
2. 点击"分享"按钮
3. 复制分享文本

**测试数据**:
```json
{
  "videoUrl": "【【古风DJ】\"天青色等烟雨 而我在等你\"丨《青花瓷》DJ版（0.85x）】 https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc",
  "platform": "BILIBILI"
}
```

**执行命令**（Windows PowerShell）:
```powershell
$body = @{
    videoUrl = '【【古风DJ】"天青色等烟雨 而我在等你"丨《青花瓷》DJ版（0.85x）】 https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc'
    platform = 'BILIBILI'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8910/api/video/parse' `
    -Method Post `
    -ContentType 'application/json' `
    -Body $body
```

**执行命令**（Bash/Git Bash）:
```bash
curl -X POST http://localhost:8910/api/video/parse \
  -H "Content-Type: application/json" \
  -d '{
    "videoUrl": "【【古风DJ】\"天青色等烟雨 而我在等你\"丨《青花瓷》DJ版（0.85x）】 https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc",
    "platform": "BILIBILI"
  }'
```

**期望结果**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "title": "【古风DJ】"天青色等烟雨 而我在等你"丨《青花瓷》DJ版（0.85x）",
    "audioUrl": "http://localhost:8910/temp-audio/BV1RcUQBWEYN.mp3",
    "coverUrl": "...",
    "duration": ...,
    "sourceVideoId": "BV1RcUQBWEYN"
  }
}
```

**期望日志**:
```log
INFO  - B站解析策略开始解析视频: 【【古风DJ】...】 https://...
INFO  - 从分享文本中提取URL: 【...】 https://... -> https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=xxx
INFO  - 提取到BV号: BV1RcUQBWEYN
INFO  - B站视频解析成功: BV1RcUQBWEYN
```

**验证要点**:
- ✅ 接口返回成功（code=200）
- ✅ 能够解析出正确的视频信息
- ✅ 日志显示"从分享文本中提取URL"

---

### **测试用例 2: 纯URL（向后兼容）**

**描述**: 使用纯URL（不包含标题），确保向后兼容

**测试数据**:
```json
{
  "videoUrl": "https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc",
  "platform": "BILIBILI"
}
```

**执行命令**（PowerShell）:
```powershell
$body = @{
    videoUrl = 'https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc'
    platform = 'BILIBILI'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8910/api/video/parse' `
    -Method Post `
    -ContentType 'application/json' `
    -Body $body
```

**执行命令**（Bash）:
```bash
curl -X POST http://localhost:8910/api/video/parse \
  -H "Content-Type: application/json" \
  -d '{
    "videoUrl": "https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=c9261469495da8200c33a6760a8dc0dc",
    "platform": "BILIBILI"
  }'
```

**期望结果**:
- ✅ 接口返回成功（code=200）
- ✅ 能够解析出正确的视频信息

**期望日志**:
```log
INFO  - B站解析策略开始解析视频: https://www.bilibili.com/video/BV1RcUQBWEYN/?...
INFO  - 提取到BV号: BV1RcUQBWEYN
INFO  - B站视频解析成功: BV1RcUQBWEYN
```

**验证要点**:
- ✅ 接口正常工作
- ✅ **日志中没有**"从分享文本中提取URL"（因为输入已经是纯URL）

---

### **测试用例 3: 无参数URL**

**描述**: 使用不带参数的URL

**测试数据**:
```json
{
  "videoUrl": "https://www.bilibili.com/video/BV1RcUQBWEYN/",
  "platform": "BILIBILI"
}
```

**执行命令**（PowerShell）:
```powershell
$body = @{
    videoUrl = 'https://www.bilibili.com/video/BV1RcUQBWEYN/'
    platform = 'BILIBILI'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8910/api/video/parse' `
    -Method Post `
    -ContentType 'application/json' `
    -Body $body
```

**期望结果**:
- ✅ 接口返回成功（code=200）
- ✅ 能够解析出正确的视频信息

---

### **测试用例 4: 纯BV号**

**描述**: 只提供BV号（最简格式）

**测试数据**:
```json
{
  "videoUrl": "BV1RcUQBWEYN",
  "platform": "BILIBILI"
}
```

**执行命令**（PowerShell）:
```powershell
$body = @{
    videoUrl = 'BV1RcUQBWEYN'
    platform = 'BILIBILI'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8910/api/video/parse' `
    -Method Post `
    -ContentType 'application/json' `
    -Body $body
```

**期望结果**:
- ✅ 接口返回成功（code=200）
- ✅ 能够解析出正确的视频信息

---

### **测试用例 5: 包含中文但无B站格式的情况**

**描述**: 混合中文和URL（非标准分享格式）

**测试数据**:
```json
{
  "videoUrl": "看这个视频 https://www.bilibili.com/video/BV1RcUQBWEYN/ 很有意思",
  "platform": "BILIBILI"
}
```

**执行命令**（PowerShell）:
```powershell
$body = @{
    videoUrl = '看这个视频 https://www.bilibili.com/video/BV1RcUQBWEYN/ 很有意思'
    platform = 'BILIBILI'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8910/api/video/parse' `
    -Method Post `
    -ContentType 'application/json' `
    -Body $body
```

**期望结果**:
- ✅ 接口返回成功（code=200）
- ✅ 能够从文本中提取URL并解析

**期望日志**:
```log
INFO  - 从分享文本中提取URL: 看这个视频 https://... 很有意思 -> https://www.bilibili.com/video/BV1RcUQBWEYN/
```

---

## 📊 测试结果记录表

| 测试用例 | 输入格式 | 预期结果 | 实际结果 | 通过 |
|---------|---------|---------|---------|------|
| 用例1 | 原始分享文本（带标题） | 自动提取URL并解析 | | ☐ |
| 用例2 | 纯URL（带参数） | 正常解析 | | ☐ |
| 用例3 | 纯URL（无参数） | 正常解析 | | ☐ |
| 用例4 | 纯BV号 | 正常解析 | | ☐ |
| 用例5 | 混合中文文本 | 提取URL并解析 | | ☐ |

---

## 🔍 日志检查要点

### **成功的日志示例**（用例1）:
```log
2026-01-31T17:30:00.123+08:00  INFO 12345 --- [nio-8910-exec-1] c.n.w.s.s.BilibiliParseStrategy          : B站解析策略开始解析视频: 【【古风DJ】...】 https://...
2026-01-31T17:30:00.124+08:00  INFO 12345 --- [nio-8910-exec-1] c.n.w.s.s.BilibiliParseStrategy          : 从分享文本中提取URL: 【...】 https://... -> https://www.bilibili.com/video/BV1RcUQBWEYN/?share_source=copy_web&vd_source=xxx
2026-01-31T17:30:00.125+08:00  INFO 12345 --- [nio-8910-exec-1] c.n.w.s.s.BilibiliParseStrategy          : 提取到BV号: BV1RcUQBWEYN
```

### **成功的日志示例**（用例2 - 纯URL）:
```log
2026-01-31T17:30:00.123+08:00  INFO 12345 --- [nio-8910-exec-1] c.n.w.s.s.BilibiliParseStrategy          : B站解析策略开始解析视频: https://www.bilibili.com/video/BV1RcUQBWEYN/?...
2026-01-31T17:30:00.124+08:00  INFO 12345 --- [nio-8910-exec-1] c.n.w.s.s.BilibiliParseStrategy          : 提取到BV号: BV1RcUQBWEYN
```
（注意：没有"从分享文本中提取URL"的日志）

---

## 🎉 测试通过标准

所有5个测试用例都应该：
- ✅ 接口返回 `code: 200`
- ✅ 成功解析视频信息
- ✅ 提取到正确的BV号
- ✅ 日志记录正确（用例1和5应有URL提取日志）

---

## ⚠️ 常见问题

### **问题 1: JSON 格式错误**

**症状**: 接口返回 400 错误

**原因**: JSON 中的特殊字符（如双引号）未正确转义

**解决**:
- PowerShell 中使用单引号包裹整个字符串
- JSON 内部的双引号使用 `\"` 转义

### **问题 2: 应用未启动**

**症状**: curl 返回 "Connection refused" 或 000 错误码

**解决**:
```bash
# 检查应用是否启动
jps | grep WangyiyunMusicApplication

# 检查端口是否监听
netstat -ano | findstr :8910
```

### **问题 3: 端口被占用**

**症状**: 应用启动失败，提示端口已被使用

**解决**:
```bash
# Windows 查找占用端口的进程
netstat -ano | findstr :8910

# 杀死进程
taskkill /F /PID <进程ID>
```

---

## 📄 测试完成后

### **1. 记录测试结果**

将测试结果填入上面的"测试结果记录表"

### **2. 检查临时文件**

```bash
# 查看临时目录
ls -lh D:/music-data/temp/

# 应该能看到下载的 mp3 文件
```

### **3. 访问音频URL**

从接口返回的 `audioUrl` 中复制URL，在浏览器中访问，应该能播放音频。

---

**测试文档创建时间**: 2026-01-31
**测试目标**: 验证B站原始分享链接支持功能
**预计测试时间**: 15-20分钟
