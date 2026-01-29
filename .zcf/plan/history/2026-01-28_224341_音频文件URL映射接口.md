# 音频文件URL映射接口开发计划

**创建时间**: 2026-01-28 22:05:10
**任务类型**: 新功能开发
**优先级**: 中
**预计工作量**: 2-3小时

---

## 📋 任务背景

### 用户需求
开发一个接口，将服务器指定目录下的本地音频文件映射成URL路径，供前端访问音频文件进行播放。

### 需求分析结果
- **音频存储位置**: 服务器指定目录
- **功能范围**: 单个音频文件访问
- **返回方式**: 返回静态资源URL
- **技术要求**: 支持HTTP Range请求（拖拽播放）

### 需求完整性评分
**10/10** ✅ - 需求非常明确，可直接实施

---

## 🎯 技术方案

### 选定方案
**方案1：静态资源映射 + URL拼接接口**

### 核心设计
1. **静态资源映射**
   - 在 `WebMvcConfig` 中配置音频目录映射到 `/audio/**` 路径
   - 利用 Spring MVC 原生静态资源处理能力
   - 自动支持 HTTP Range 请求

2. **接口设计**
   - 提供 REST 接口根据 Music ID 返回音频访问URL
   - 接口路径: `GET /api/audio/{musicId}`
   - 返回格式: `AudioUrlVO` (包含URL、文件名、时长等信息)

3. **配置管理**
   - 在 `application.yaml` 中配置音频文件物理路径
   - 支持灵活配置，便于不同环境部署

### 技术优势
- ✅ 实现简单，开发成本低
- ✅ 性能优秀，直接由 Spring MVC 处理文件流
- ✅ 自动支持 Range 请求，无需额外开发
- ✅ 易于维护和扩展

---

## 📐 详细实施步骤

### 步骤 1: 配置音频存储路径
**文件**: `src/main/resources/application.yaml`

**操作**: 在配置文件末尾添加自定义配置

**新增配置**:
```yaml
# 音频文件配置
audio:
  # 音频文件存储的物理路径（根据实际部署环境调整）
  storage-path: file:D:/music-data/audio/
  # 访问URL前缀
  url-prefix: /audio/
```

**说明**:
- `storage-path`: 音频文件实际存储的磁盘路径，使用 `file:` 协议
- `url-prefix`: 前端访问音频的URL路径前缀
- Windows 路径使用正斜杠，Linux 示例: `file:/data/music/audio/`

**预期结果**: 配置项添加成功，应用可读取配置

---

### 步骤 2: 创建音频URL视图对象
**文件**: `src/main/java/com/naruto/wangyiyunmusic/model/vo/AudioUrlVO.java`

**操作**: 创建新的 VO 类

**代码结构**:
```java
@Data
@Schema(description = "音频URL响应对象")
public class AudioUrlVO {
    @Schema(description = "音频访问URL", example = "http://localhost:8910/audio/song.mp3")
    private String audioUrl;

    @Schema(description = "音乐ID", example = "1")
    private Long musicId;

    @Schema(description = "音乐标题", example = "晴天")
    private String title;

    @Schema(description = "文件名", example = "song.mp3")
    private String fileName;

    @Schema(description = "时长(秒)", example = "240")
    private Integer duration;
}
```

**字段说明**:
- `audioUrl`: 完整的音频访问URL，前端可直接使用
- `musicId`: 音乐ID，用于关联
- `title`: 音乐标题，便于前端展示
- `fileName`: 音频文件名
- `duration`: 音乐时长（秒）

**预期结果**: VO类创建成功，包含完整的 Swagger 注解

---

### 步骤 3: 配置静态资源映射
**文件**: `src/main/java/com/naruto/wangyiyunmusic/config/WebMvcConfig.java`

**操作**: 在 `addResourceHandlers` 方法中添加音频资源映射

**修改位置**: `addResourceHandlers` 方法内，在 Swagger 配置之后

**新增代码**:
```java
// 音频文件静态资源映射
registry.addResourceHandler("/audio/**")
        .addResourceLocations(audioStoragePath)
        .setCachePeriod(3600)  // 缓存1小时
        .resourceChain(true);
```

**依赖注入**:
```java
@Value("${audio.storage-path}")
private String audioStoragePath;
```

**说明**:
- `/audio/**`: URL访问路径模式
- `addResourceLocations`: 映射到配置的物理路径
- `setCachePeriod(3600)`: 设置浏览器缓存1小时，提升性能
- `resourceChain(true)`: 启用资源链，支持 Range 请求

**预期结果**: 静态资源映射配置完成，可通过 `/audio/文件名` 访问音频

---

### 步骤 4: 创建音频服务层
**文件**: `src/main/java/com/naruto/wangyiyunmusic/service/AudioService.java`

**操作**: 创建新的 Service 接口

**接口定义**:
```java
public interface AudioService {
    /**
     * 根据音乐ID获取音频访问URL
     *
     * @param musicId 音乐ID
     * @return 音频URL信息
     */
    AudioUrlVO getAudioUrl(Long musicId);
}
```

**实现类**: `src/main/java/com/naruto/wangyiyunmusic/service/impl/AudioServiceImpl.java`

**核心逻辑**:
1. 根据 musicId 从数据库查询 Music 实体
2. 验证音乐是否存在
3. 从 Music 实体提取 fileUrl 字段（存储的是文件名或相对路径）
4. 拼接完整的访问URL: `http://服务器地址:端口/audio/文件名`
5. 构建 AudioUrlVO 对象返回

**异常处理**:
- 音乐不存在: 抛出 `BusinessException("音乐不存在")`
- 文件URL为空: 抛出 `BusinessException("音频文件不存在")`

**预期结果**: Service层业务逻辑实现完成，可正确返回URL信息

---

### 步骤 5: 创建音频控制器
**文件**: `src/main/java/com/naruto/wangyiyunmusic/controller/AudioController.java`

**操作**: 创建新的 Controller 类

**接口定义**:
```java
@Tag(name = "音频管理", description = "提供音频文件访问接口")
@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioService audioService;

    /**
     * 获取音频访问URL
     *
     * @param musicId 音乐ID
     * @return 音频URL信息
     */
    @Operation(summary = "获取音频URL", description = "根据音乐ID获取音频文件的访问URL，支持Range请求实现拖拽播放")
    @GetMapping("/{musicId}")
    public AudioUrlVO getAudioUrl(
            @Parameter(description = "音乐ID", example = "1", required = true)
            @PathVariable Long musicId) {
        return audioService.getAudioUrl(musicId);
    }
}
```

**接口特点**:
- RESTful 风格: `GET /api/audio/{musicId}`
- 自动封装为 `Result<AudioUrlVO>` (GlobalResponseAdvice)
- 完整的 Swagger 文档注解
- 遵循项目现有规范

**预期结果**: Controller 创建完成，Swagger 文档自动生成

---

### 步骤 6: 测试验证
**测试内容**:

1. **接口功能测试**
   - 访问 Swagger UI: `http://localhost:8910/swagger-ui/index.html`
   - 测试 `GET /api/audio/{musicId}` 接口
   - 验证返回的 URL 格式正确

2. **音频播放测试**
   - 将返回的 audioUrl 粘贴到浏览器地址栏
   - 验证音频可以直接播放
   - 测试拖拽功能（验证 Range 请求）

3. **边界测试**
   - 测试不存在的 musicId (应返回异常信息)
   - 测试 fileUrl 为空的音乐 (应返回异常信息)

**预期结果**:
- ✅ 接口正常返回音频URL
- ✅ 浏览器可直接播放音频
- ✅ 支持拖拽播放（Range请求生效）
- ✅ 异常情况正常处理

---

## 📊 文件清单

### 新增文件
| 文件路径 | 类型 | 说明 |
|---------|------|------|
| `model/vo/AudioUrlVO.java` | VO类 | 音频URL响应对象 |
| `service/AudioService.java` | 接口 | 音频服务接口 |
| `service/impl/AudioServiceImpl.java` | 实现类 | 音频服务实现 |
| `controller/AudioController.java` | 控制器 | 音频接口控制器 |

### 修改文件
| 文件路径 | 修改内容 |
|---------|---------|
| `application.yaml` | 添加音频存储路径配置 |
| `WebMvcConfig.java` | 添加音频静态资源映射 |

---

## 🔧 技术细节

### Range 请求支持原理
Spring MVC 的 `ResourceHttpRequestHandler` 自动支持 HTTP Range 请求：
- 客户端发送 `Range: bytes=0-1023` 请求头
- 服务器返回 `206 Partial Content` 状态码
- 响应包含 `Content-Range` 和 `Accept-Ranges` 头
- 前端 `<audio>` 标签利用此特性实现拖拽播放

### URL 拼接逻辑
```java
// 假设 Music.fileUrl = "jay/晴天.mp3"
String baseUrl = "http://localhost:8910";
String urlPrefix = "/audio/";
String audioUrl = baseUrl + urlPrefix + music.getFileUrl();
// 结果: http://localhost:8910/audio/jay/晴天.mp3
```

### 异常处理
依赖项目现有的 `GlobalExceptionHandler` 统一处理业务异常，返回标准错误格式。

---

## ⚠️ 注意事项

1. **文件路径配置**
   - 确保配置的物理路径存在且有读权限
   - Windows 路径也使用正斜杠: `D:/music-data/`
   - 路径末尾需要有斜杠: `file:D:/music-data/audio/`

2. **数据库数据**
   - 确保 Music 表的 `file_url` 字段有正确的文件名
   - 文件名应与实际存储的文件名一致
   - 支持子目录，如: `singer/song.mp3`

3. **安全性考虑**
   - 当前方案无权限控制，任何人知道URL都可访问
   - 如需权限控制，后续可升级到方案2或方案3
   - 建议只存储公开音乐

4. **性能优化**
   - 已配置浏览器缓存1小时
   - 大文件传输由 Spring MVC 处理，性能良好
   - 生产环境建议使用 Nginx 作为静态资源服务器

---

## 🎯 成功标准

- [x] 需求分析完成，评分10/10
- [ ] 配置文件添加音频路径配置
- [ ] WebMvcConfig 完成静态资源映射
- [ ] AudioUrlVO 创建完成
- [ ] AudioService 业务逻辑实现
- [ ] AudioController 接口开发完成
- [ ] Swagger 文档自动生成
- [ ] 接口测试通过
- [ ] 音频播放测试通过
- [ ] Range 请求验证通过

---

## 📝 后续优化方向

1. **权限控制**: 集成 Spring Security，限制访问权限
2. **播放统计**: 在获取URL时记录播放次数
3. **CDN加速**: 将音频文件上传到CDN，提升访问速度
4. **文件上传**: 开发音频文件上传接口
5. **格式转换**: 支持音频格式自动转换
6. **流量控制**: 添加访问频率限制，防止恶意下载

---

**计划制定完成，等待用户批准后开始执行。**
