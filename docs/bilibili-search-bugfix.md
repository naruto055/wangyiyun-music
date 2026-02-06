# B站搜索功能Bug修复记录

**修复日期**: 2026-02-06
**问题模块**: BilibiliSearchServiceImpl
**严重程度**: 高（核心功能无法正常使用）

---

## 问题描述

B站视频搜索功能返回的结果与输入关键词不匹配。例如：搜索"稻香"，返回的却是包含 A7、A8、E6、E7、E8 等字符的无关视频。

### 症状表现

```json
// 输入
{
  "keyword": "稻香",
  "page": 1,
  "pageSize": 10
}

// 错误的返回结果
{
  "records": [
    {
      "title": "9dabae96cka6bc8eca4ead4420e354a8 l",  // ❌ 包含 a8
      "bvid": "BV1ZV4y1U71w"
    },
    {
      "title": "Wonderland(%E8%8B%B1%E6%96%87...)",   // ❌ 包含 E8、E6、E7
      "bvid": "BV1VW411A7pG"
    },
    {
      "title": "A7A8A9是什么意思",                    // ❌ 包含 A7、A8、A9
      "bvid": "BV1AbZZYpESh"
    }
    // ...更多无关视频
  ]
}
```

---

## 根本原因分析

通过对比参考实现（extion.js）和调试日志，发现了**三个关键问题**：

### 问题1：Cookie 获取时 User-Agent 不正确

**现象**：使用 PC 端 User-Agent 获取 Cookie，导致返回的 Cookie 无效或不完整。

**原因**：B站的 Cookie API (`/x/frontend/finger/spi`) 对不同的 User-Agent 返回不同的数据。

**错误代码**：
```java
// ❌ 使用 PC 端 User-Agent
headers.set("User-Agent", userAgent);  // PC 浏览器标识
```

**正确实现**（来自 extion.js line 52-59）：
```javascript
// ✅ 使用 iPhone 移动端 User-Agent
headers: {
    "User-Agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/114.0.0.0",
}
```

---

### 问题2：Gzip 压缩响应无法解析

**现象**：
```
JsonParseException: Illegal character ((CTRL-CHAR, code 31))
```

**原因**：
- 代码设置了 `Accept-Encoding: gzip, deflate, br`
- B站返回 gzip 压缩数据（控制字符 31 是 gzip 标识）
- `SimpleClientHttpRequestFactory` 不支持自动解压
- Jackson 试图解析压缩的二进制数据，导致失败

**错误代码**：
```java
// ❌ 声明支持压缩，但无法解压
headers.set("Accept-Encoding", "gzip, deflate, br");
```

**修复方案**：
```java
// ✅ 删除 Accept-Encoding 头，让 B站返回未压缩的 JSON
// headers.set("Accept-Encoding", "gzip, deflate, br");  // 已删除
```

**备选方案**（如需支持压缩）：
- 添加 Apache HttpClient 依赖
- 配置 `HttpComponentsClientHttpRequestFactory`

---

### 问题3：URL 编码导致关键词错误（核心问题）

**现象**：搜索"稻香"，B站搜索了包含 `E7`、`A8`、`BB`、`E9`、`A6`、`99` 等字符的视频。

**原因分析**：

1. **URL 编码过程**：
   ```
   "稻香"
   → UTF-8 编码: E7 A8 BB E9 A6 99
   → URL 编码: %E7%A8%BB%E9%A6%99
   ```

2. **RestTemplate 的两个重载方法**：
   ```java
   // 方法1：接受 String，会对 URL 进行编码
   exchange(String url, HttpMethod method, ...)

   // 方法2：接受 URI 对象，不会再次编码
   exchange(URI url, HttpMethod method, ...)
   ```

3. **错误代码的执行流程**：
   ```java
   // ❌ 使用 String 导致二次编码
   String requestUrl = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
       .queryParam("keyword", "稻香")  // 原始字符串
       .build()
       .encode()                        // ① 第一次编码: %E7%A8%BB%E9%A6%99
       .toUriString();                  // ② 转为 String

   restTemplate.exchange(
       requestUrl,                      // ③ RestTemplate 再次编码
       HttpMethod.GET,
       entity,
       Map.class
   );

   // 实际发送给 B站的 URL：
   // keyword=%25E7%25A8%25BB%25E9%25A6%2599
   //         ^^^^ 百分号被编码为 %25

   // B站解码后得到：
   // keyword=%E7%A8%BB%E9%A6%99
   // 把编码字符串当成了关键词，搜索包含 E7、A8、BB、E9、A6、99 的视频
   ```

**正确实现**：
```java
// ✅ 使用 URI 对象避免二次编码
java.net.URI requestUri = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
    .queryParam("keyword", "稻香")
    .build()
    .encode()   // 编码一次
    .toUri();   // 返回 URI 对象（关键）

restTemplate.exchange(
    requestUri,  // 使用 URI 对象，不会再次编码
    HttpMethod.GET,
    entity,
    Map.class
);

// 实际发送给 B站的 URL：
// keyword=%E7%A8%BB%E9%A6%99  ✅ 正确
```

---

## 修复方案

### 修改文件

`src/main/java/com/naruto/wangyiyunmusic/service/impl/BilibiliSearchServiceImpl.java`

### 修改1：Cookie 获取（Line 78-80）

```java
// 设置请求头 - 关键:使用iPhone移动端User-Agent获取Cookie
HttpHeaders headers = new HttpHeaders();
headers.set("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_2_3 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/13.0.3 Mobile/15E148 Safari/604.1 Edg/114.0.0.0");
```

### 修改2：删除 Accept-Encoding（Line 167）

```java
// 注意：不设置 Accept-Encoding，避免 gzip 解压问题
// headers.set("Accept-Encoding", "gzip, deflate, br");  // 已删除
```

### 修改3：使用 URI 对象（Line 141-189）

```java
// 2. 构建请求URI（关键：使用 URI 对象避免 RestTemplate 二次编码）
java.net.URI requestUri = UriComponentsBuilder.fromHttpUrl(searchApiUrl)
        .queryParam("context", "")
        .queryParam("page", searchDTO.getPage())
        .queryParam("order", "")
        .queryParam("page_size", searchDTO.getPageSize())
        .queryParam("keyword", searchDTO.getKeyword())
        .queryParam("duration", "")
        .queryParam("tids_1", "")
        .queryParam("tids_2", "")
        .queryParam("__refresh__", true)
        .queryParam("_extra", "")
        .queryParam("highlight", 1)
        .queryParam("single_column", 0)
        .queryParam("platform", "pc")
        .queryParam("from_source", "")
        .queryParam("search_type", "video")
        .queryParam("dynamic_offset", 0)
        .build()
        .encode()  // 正确编码URL
        .toUri();  // 关键：返回 URI 对象，避免 RestTemplate 二次编码

// 3. 设置请求头（参照extion.js的searchHeaders配置）
HttpHeaders headers = new HttpHeaders();
headers.set("User-Agent", userAgent);
headers.set("Accept", "application/json, text/plain, */*");
// 注意：不设置 Accept-Encoding，避免 gzip 解压问题
headers.set("Origin", "https://search.bilibili.com");
headers.set("Sec-Fetch-Site", "same-site");
headers.set("Sec-Fetch-Mode", "cors");
headers.set("Sec-Fetch-Dest", "empty");
headers.set("Referer", "https://search.bilibili.com/");
headers.set("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");
headers.set("Cookie", "buvid3=" + cookie.getB3() + ";buvid4=" + cookie.getB4());

HttpEntity<String> entity = new HttpEntity<>(headers);

// 4. 发送请求（使用 URI 对象而不是 String）
ResponseEntity<Map> response = restTemplate.exchange(
        requestUri,  // 传递 URI 对象
        HttpMethod.GET,
        entity,
        Map.class
);
```

---

## 验证结果

### 修复前

```
搜索关键词: "稻香"
返回结果: 包含 A7、A8、E6、E7、E8 等字符的无关视频
状态: ❌ 失败
```

### 修复后

```
搜索关键词: "稻香"
返回结果: 周杰伦《稻香》及相关音乐视频
状态: ✅ 成功
```

---

## 经验总结

### 1. 第三方 API 集成注意事项

- **参考官方或稳定的实现**：本次修复关键参考了 extion.js 的实现
- **User-Agent 很重要**：不同的 User-Agent 可能返回不同的数据
- **Cookie 机制需要匹配**：移动端 API 可能需要移动端 Cookie

### 2. HTTP 客户端使用技巧

- **压缩支持**：确认 HTTP 客户端是否支持自动解压（SimpleClientHttpRequestFactory 不支持）
- **URL 编码陷阱**：
  - `RestTemplate.exchange(String url, ...)` 会对 String 进行编码
  - `RestTemplate.exchange(URI url, ...)` 不会对 URI 对象再次编码
  - **推荐**：使用 `UriComponentsBuilder.build().encode().toUri()` 返回 URI 对象

### 3. 调试技巧

- **添加详细日志**：打印完整的请求 URL 和响应数据
- **对比参考实现**：仔细对比正确实现的每个细节
- **分步排查**：Cookie → 压缩 → 编码，逐个问题解决

---

## 相关资源

- **参考实现**: `extion.js` (项目根目录)
- **B站搜索API**: `https://api.bilibili.com/x/web-interface/search/type`
- **B站Cookie API**: `https://api.bilibili.com/x/frontend/finger/spi`
- **Spring UriComponentsBuilder 文档**: https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/web/util/UriComponentsBuilder.html

---

## 后续优化建议

### 1. 配置支持 Gzip 的 HTTP 客户端（可选）

如果希望支持压缩以节省带宽，可以：

**步骤1**：添加依赖（pom.xml）
```xml
<dependency>
    <groupId>org.apache.httpcomponents.client5</groupId>
    <artifactId>httpclient5</artifactId>
</dependency>
```

**步骤2**：修改 RestTemplateConfig.java
```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    // 创建支持 Gzip 的 HttpClient
    CloseableHttpClient httpClient = HttpClients.custom()
            .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(Timeout.ofSeconds(10))
                    .setResponseTimeout(Timeout.ofSeconds(30))
                    .build())
            .build();

    HttpComponentsClientHttpRequestFactory factory =
        new HttpComponentsClientHttpRequestFactory(httpClient);

    return builder
            .requestFactory(() -> factory)
            .build();
}
```

### 2. 单元测试

建议添加单元测试覆盖：
- Cookie 获取逻辑
- URL 编码正确性
- 搜索结果解析

### 3. 监控与告警

- 添加搜索失败率监控
- 当 Cookie 获取失败时告警
- 记录搜索关键词与结果的匹配度

---

**修复人**: Claude (AI Assistant)
**审核人**: 待审核
**文档版本**: v1.0
