# 项目任务分解规划

## 已明确的决策

- **技术栈**: Spring Boot 3.1.0 + Java 17 + MyBatis-Plus 3.5.5 + MySQL
- **架构模式**: 三层架构（Controller → Service → Mapper）
- **ORM框架**: MyBatis-Plus（继承 BaseMapper、IService 获得基础 CRUD 能力）
- **统一响应**: 使用 GlobalResponseAdvice 自动封装返回 Result
- **异常处理**: 使用 GlobalExceptionHandler 统一捕获业务异常
- **API文档**: SpringDoc OpenAPI（Swagger 注解）
- **参数校验**: Jakarta Validation（@Valid、@NotNull、@Size 等注解）
- **日志框架**: SLF4J + Logback（使用 @Slf4j）
- **删除策略**: 逻辑删除（MyBatis-Plus @TableLogic 注解）
- **命名规范**: 遵循《阿里巴巴 Java 开发手册》，使用中文注释
- **API路径前缀**: `/api/album`

---

## 整体规划概述

### 项目目标

为网易云音乐后端服务系统实现完整的专辑管理功能，包括：
1. 专辑的增删改查（CRUD）操作
2. 支持分页查询、关键词搜索、排序功能
3. 提供符合 RESTful 规范的 API 接口
4. 确保代码质量和可维护性

### 技术栈

- **表现层**: Spring MVC（RESTful API）
- **业务层**: Spring Service + MyBatis-Plus IService
- **持久层**: MyBatis-Plus BaseMapper
- **数据验证**: Jakarta Validation
- **API文档**: SpringDoc OpenAPI
- **数据库**: MySQL 8.0+

### 主要阶段

1. **阶段 1：数据传输层（DTO & VO）**
   创建查询参数对象、请求对象、响应视图对象

2. **阶段 2：服务层（Service）**
   实现业务逻辑方法，包含分页查询、详情查询、增删改功能

3. **阶段 3：控制层（Controller）**
   实现 RESTful API 接口，集成 Swagger 文档

4. **阶段 4：测试验证**
   通过 Swagger UI 测试所有接口，验证功能正确性

---

## 详细任务分解

### 阶段 1：数据传输层（DTO & VO）

#### 任务 1.1：创建专辑查询参数对象 AlbumQueryDTO

- **目标**: 封装专辑列表查询的请求参数
- **输入**: 无（新建文件）
- **输出**: `AlbumQueryDTO.java` 文件
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumQueryDTO.java` (新建)
- **预估工作量**: 15分钟

**字段设计**:
```java
- pageNum: Integer (默认 1) - 页码
- pageSize: Integer (默认 10) - 每页大小
- keyword: String - 关键词（专辑名称搜索）
- sortField: String (默认 "release_date") - 排序字段（release_date/create_time）
- sortOrder: String (默认 "desc") - 排序方式（asc/desc）
```

**技术要点**:
- 使用 `@Data` 注解简化代码
- 使用 `@Schema` 注解生成 Swagger 文档
- 字段提供默认值，方便前端调用
- 参考 `MusicQueryDTO` 实现模式

---

#### 任务 1.2：创建专辑创建请求对象 AlbumCreateDTO

- **目标**: 封装创建专辑的请求参数
- **输入**: 无（新建文件）
- **输出**: `AlbumCreateDTO.java` 文件
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumCreateDTO.java` (新建)
- **预估工作量**: 20分钟

**字段设计**:
```java
- name: String - 专辑名称（必填，长度 1-200）
- coverUrl: String - 封面URL（可选，URL格式）
- description: String - 专辑简介（可选）
- releaseDate: LocalDate - 发行日期（可选，日期格式）
```

**参数校验规则**:
- `@NotBlank(message = "专辑名称不能为空")` - name 字段
- `@Size(max = 200, message = "专辑名称长度不能超过200个字符")` - name 字段
- `@Size(max = 500, message = "封面URL长度不能超过500个字符")` - coverUrl 字段
- `@JsonFormat(pattern = "yyyy-MM-dd")` - releaseDate 字段

**技术要点**:
- 使用 Jakarta Validation 注解进行参数校验
- 使用 `@Schema` 注解生成 Swagger 文档
- 字段类型与数据库保持一致

---

#### 任务 1.3：创建专辑更新请求对象 AlbumUpdateDTO

- **目标**: 封装更新专辑的请求参数
- **输入**: 无（新建文件）
- **输出**: `AlbumUpdateDTO.java` 文件
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumUpdateDTO.java` (新建)
- **预估工作量**: 15分钟

**字段设计**:
```java
- name: String - 专辑名称（必填，长度 1-200）
- coverUrl: String - 封面URL（可选，URL格式）
- description: String - 专辑简介（可选）
- releaseDate: LocalDate - 发行日期（可选，日期格式）
```

**技术要点**:
- 字段设计与 `AlbumCreateDTO` 一致
- 参数校验规则相同
- 可考虑继承 `AlbumCreateDTO` 以复用代码

---

#### 任务 1.4：创建专辑列表视图对象 AlbumListVO

- **目标**: 封装专辑列表展示所需的字段
- **输入**: 无（新建文件）
- **输出**: `AlbumListVO.java` 文件
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/model/vo/AlbumListVO.java` (新建)
- **预估工作量**: 20分钟

**字段设计**:
```java
- id: Long - 专辑ID
- name: String - 专辑名称
- coverUrl: String - 封面URL
- releaseDate: LocalDate - 发行日期
- createTime: LocalDateTime - 创建时间
- updateTime: LocalDateTime - 更新时间
```

**技术要点**:
- 使用 `@Data` 注解简化代码
- 使用 `@Schema` 注解生成 Swagger 文档
- 使用 `@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")` 格式化日期
- 使用 `@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")` 格式化时间
- 参考 `MusicListVO` 实现模式

---

#### 任务 1.5：创建专辑详情视图对象 AlbumDetailVO

- **目标**: 封装专辑详情展示所需的字段
- **输入**: 无（新建文件）
- **输出**: `AlbumDetailVO.java` 文件
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/model/vo/AlbumDetailVO.java` (新建)
- **预估工作量**: 25分钟

**字段设计**:
```java
- id: Long - 专辑ID
- name: String - 专辑名称
- coverUrl: String - 封面URL
- description: String - 专辑简介
- releaseDate: LocalDate - 发行日期
- createTime: LocalDateTime - 创建时间
- updateTime: LocalDateTime - 更新时间
- musicCount: Integer - 歌曲数量（可选，通过关联查询获取）
```

**技术要点**:
- 使用 `@Data` 注解简化代码
- 使用 `@Schema` 注解生成 Swagger 文档
- 使用 `@JsonFormat` 注解格式化日期时间
- 可选扩展：添加专辑下的歌曲列表（`List<MusicListVO> musics`）
- 参考 `MusicDetailVO` 实现模式

---

### 阶段 2：服务层（Service）

#### 任务 2.1：定义 AlbumService 接口方法

- **目标**: 在 AlbumService 接口中定义业务方法
- **输入**: 现有的 `AlbumService.java` 空接口
- **输出**: 包含方法签名的 `AlbumService.java` 接口
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/AlbumService.java` (修改)
- **预估工作量**: 15分钟

**方法签名**:
```java
/**
 * 分页查询专辑列表
 * @param queryDTO 查询条件
 * @return 分页结果
 */
IPage<AlbumListVO> pageQuery(AlbumQueryDTO queryDTO);

/**
 * 获取专辑详情
 * @param id 专辑ID
 * @return 专辑详情
 */
AlbumDetailVO getAlbumDetail(Long id);

/**
 * 创建专辑
 * @param createDTO 创建参数
 * @return 新增专辑的ID
 */
Long createAlbum(AlbumCreateDTO createDTO);

/**
 * 更新专辑
 * @param id 专辑ID
 * @param updateDTO 更新参数
 * @return 是否更新成功
 */
Boolean updateAlbum(Long id, AlbumUpdateDTO updateDTO);

/**
 * 删除专辑（逻辑删除）
 * @param id 专辑ID
 * @return 是否删除成功
 */
Boolean deleteAlbum(Long id);
```

**技术要点**:
- 继续继承 `IService<Album>` 接口，获得基础 CRUD 能力
- 方法命名遵循业务语义
- 添加完整的中文 JavaDoc 注释

---

#### 任务 2.2：实现 AlbumServiceImpl.pageQuery() 方法

- **目标**: 实现专辑分页查询业务逻辑
- **输入**: 任务 1.1 创建的 `AlbumQueryDTO`
- **输出**: 包含 `pageQuery()` 实现的 `AlbumServiceImpl.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` (修改)
- **预估工作量**: 30分钟

**实现逻辑**:
1. 构建分页对象 `Page<Album>`
2. 构建查询条件 `LambdaQueryWrapper<Album>`
   - 关键词搜索：`like(Album::getName, keyword)`
   - 排序：`orderBy(sortField, sortOrder)`
3. 执行分页查询 `this.page(page, wrapper)`
4. 转换为 `AlbumListVO` 列表（使用 `BeanUtils.copyProperties`）
5. 构造返回分页结果 `Page<AlbumListVO>`

**技术要点**:
- 使用 MyBatis-Plus `LambdaQueryWrapper` 构建查询条件
- 使用 `BeanUtils.copyProperties()` 进行对象拷贝
- 使用 Java 8 Stream API 进行数据转换
- 参考 `MusicServiceImpl.pageQuery()` 实现模式
- 添加 `@Slf4j` 注解记录日志

**示例代码结构**:
```java
@Override
public IPage<AlbumListVO> pageQuery(AlbumQueryDTO queryDTO) {
    log.info("分页查询专辑列表, queryDTO: {}", queryDTO);

    // 1. 构建分页对象
    Page<Album> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());

    // 2. 构建查询条件
    LambdaQueryWrapper<Album> wrapper = buildQueryWrapper(queryDTO);

    // 3. 执行分页查询
    IPage<Album> albumPage = this.page(page, wrapper);

    // 4. 转换为 AlbumListVO
    List<AlbumListVO> voList = albumPage.getRecords().stream()
        .map(this::convertToListVO)
        .collect(Collectors.toList());

    // 5. 构造返回结果
    Page<AlbumListVO> result = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    result.setRecords(voList);

    return result;
}

private LambdaQueryWrapper<Album> buildQueryWrapper(AlbumQueryDTO queryDTO) {
    // 构建查询条件逻辑
}

private AlbumListVO convertToListVO(Album album) {
    // 对象转换逻辑
}
```

---

#### 任务 2.3：实现 AlbumServiceImpl.getAlbumDetail() 方法

- **目标**: 实现专辑详情查询业务逻辑
- **输入**: 专辑ID
- **输出**: 包含 `getAlbumDetail()` 实现的 `AlbumServiceImpl.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` (修改)
- **预估工作量**: 30分钟

**实现逻辑**:
1. 根据 ID 查询专辑基本信息 `this.getById(id)`
2. 判断专辑是否存在，不存在抛出 `BusinessException("专辑不存在")`
3. 转换为 `AlbumDetailVO`（使用 `BeanUtils.copyProperties`）
4. （可选）查询专辑下的歌曲数量
5. 返回详情对象

**技术要点**:
- 使用 MyBatis-Plus `getById()` 方法查询
- 使用 `BusinessException` 处理业务异常
- 使用 `BeanUtils.copyProperties()` 进行对象拷贝
- 参考 `MusicServiceImpl.getMusicDetail()` 实现模式
- 添加日志记录

**示例代码结构**:
```java
@Override
public AlbumDetailVO getAlbumDetail(Long id) {
    log.info("查询专辑详情, id: {}", id);

    // 1. 查询专辑基本信息
    Album album = this.getById(id);
    if (album == null) {
        throw new BusinessException("专辑不存在，ID: " + id);
    }

    // 2. 转换为 AlbumDetailVO
    AlbumDetailVO vo = new AlbumDetailVO();
    BeanUtils.copyProperties(album, vo);

    // 3. 可选：查询专辑下的歌曲数量
    // Long musicCount = musicService.count(new LambdaQueryWrapper<Music>().eq(Music::getAlbumId, id));
    // vo.setMusicCount(musicCount.intValue());

    return vo;
}
```

---

#### 任务 2.4：实现 AlbumServiceImpl.createAlbum() 方法

- **目标**: 实现创建专辑业务逻辑
- **输入**: `AlbumCreateDTO`
- **输出**: 包含 `createAlbum()` 实现的 `AlbumServiceImpl.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` (修改)
- **预估工作量**: 25分钟

**实现逻辑**:
1. 将 `AlbumCreateDTO` 转换为 `Album` 实体
2. 设置创建时间和更新时间（如果数据库没有自动填充）
3. 调用 `this.save(album)` 保存到数据库
4. 返回新增专辑的 ID

**技术要点**:
- 使用 `BeanUtils.copyProperties()` 进行对象拷贝
- 使用 MyBatis-Plus `save()` 方法保存
- 保存成功后，主键 ID 会自动回填到 `album.getId()`
- 添加日志记录

**示例代码结构**:
```java
@Override
public Long createAlbum(AlbumCreateDTO createDTO) {
    log.info("创建专辑, createDTO: {}", createDTO);

    // 1. DTO 转 Entity
    Album album = new Album();
    BeanUtils.copyProperties(createDTO, album);

    // 2. 保存到数据库（MyBatis-Plus 会自动填充 createTime 和 updateTime）
    boolean success = this.save(album);
    if (!success) {
        throw new BusinessException("专辑创建失败");
    }

    log.info("专辑创建成功, id: {}", album.getId());
    return album.getId();
}
```

---

#### 任务 2.5：实现 AlbumServiceImpl.updateAlbum() 方法

- **目标**: 实现更新专辑业务逻辑
- **输入**: 专辑ID、`AlbumUpdateDTO`
- **输出**: 包含 `updateAlbum()` 实现的 `AlbumServiceImpl.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` (修改)
- **预估工作量**: 25分钟

**实现逻辑**:
1. 根据 ID 查询专辑是否存在
2. 不存在则抛出 `BusinessException("专辑不存在")`
3. 将 `AlbumUpdateDTO` 转换为 `Album` 实体
4. 设置 ID 和更新时间
5. 调用 `this.updateById(album)` 更新数据库
6. 返回更新结果

**技术要点**:
- 使用 MyBatis-Plus `getById()` 检查存在性
- 使用 `BeanUtils.copyProperties()` 进行对象拷贝
- 使用 MyBatis-Plus `updateById()` 方法更新
- 添加日志记录

**示例代码结构**:
```java
@Override
public Boolean updateAlbum(Long id, AlbumUpdateDTO updateDTO) {
    log.info("更新专辑, id: {}, updateDTO: {}", id, updateDTO);

    // 1. 检查专辑是否存在
    Album existingAlbum = this.getById(id);
    if (existingAlbum == null) {
        throw new BusinessException("专辑不存在，ID: " + id);
    }

    // 2. DTO 转 Entity
    Album album = new Album();
    BeanUtils.copyProperties(updateDTO, album);
    album.setId(id);

    // 3. 更新数据库（MyBatis-Plus 会自动更新 updateTime）
    boolean success = this.updateById(album);

    log.info("专辑更新成功, id: {}", id);
    return success;
}
```

---

#### 任务 2.6：实现 AlbumServiceImpl.deleteAlbum() 方法

- **目标**: 实现删除专辑业务逻辑（逻辑删除）
- **输入**: 专辑ID
- **输出**: 包含 `deleteAlbum()` 实现的 `AlbumServiceImpl.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` (修改)
- **预估工作量**: 20分钟

**实现逻辑**:
1. 根据 ID 查询专辑是否存在
2. 不存在则抛出 `BusinessException("专辑不存在")`
3. 调用 `this.removeById(id)` 逻辑删除专辑
4. 返回删除结果

**技术要点**:
- 使用 MyBatis-Plus `getById()` 检查存在性
- 使用 MyBatis-Plus `removeById()` 方法删除（自动逻辑删除）
- Album 实体已配置 `@TableLogic` 注解，会自动执行逻辑删除
- 添加日志记录

**示例代码结构**:
```java
@Override
public Boolean deleteAlbum(Long id) {
    log.info("删除专辑, id: {}", id);

    // 1. 检查专辑是否存在
    Album existingAlbum = this.getById(id);
    if (existingAlbum == null) {
        throw new BusinessException("专辑不存在，ID: " + id);
    }

    // 2. 逻辑删除（MyBatis-Plus 自动设置 is_deleted = 1）
    boolean success = this.removeById(id);

    log.info("专辑删除成功, id: {}", id);
    return success;
}
```

---

### 阶段 3：控制层（Controller）

#### 任务 3.1：修复 AlbumController 请求路径

- **目标**: 将 `@RequestMapping("/album")` 修改为 `@RequestMapping("/api/album")`
- **输入**: 现有的 `AlbumController.java`
- **输出**: 修正路径的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 5分钟

**修改内容**:
- 修改 `@RequestMapping` 路径为 `/api/album`
- 添加 `@Tag` 注解用于 Swagger 分组
- 注入 `AlbumService` 依赖

**示例代码**:
```java
@Tag(name = "专辑管理", description = "提供专辑查询、创建、更新、删除等接口")
@RestController
@RequestMapping("/api/album")
public class AlbumController {

    @Autowired
    private AlbumService albumService;

}
```

---

#### 任务 3.2：实现 AlbumController.list() 接口

- **目标**: 实现分页查询专辑列表接口
- **输入**: 阶段 2 实现的 `AlbumService.pageQuery()` 方法
- **输出**: 包含 `list()` 方法的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 20分钟

**API设计**:
- **请求方式**: GET
- **请求路径**: `/api/album/list`
- **请求参数**: `AlbumQueryDTO` (查询字符串参数)
- **响应格式**: `IPage<AlbumListVO>`（由 GlobalResponseAdvice 自动封装为 Result）

**技术要点**:
- 使用 `@GetMapping("/list")` 映射请求
- 使用 `@Operation` 注解生成 Swagger 文档
- 使用 `@Parameter` 注解描述参数
- 参考 `MusicController.list()` 实现模式

**示例代码**:
```java
/**
 * 分页查询专辑列表
 *
 * @param queryDTO 查询条件
 * @return 分页结果
 */
@Operation(summary = "分页查询专辑列表", description = "支持按关键词搜索专辑名称，支持按发行日期或创建时间排序")
@GetMapping("/list")
public IPage<AlbumListVO> list(
        @Parameter(description = "查询条件，包含关键词、页码、每页大小、排序字段等")
        AlbumQueryDTO queryDTO) {
    return albumService.pageQuery(queryDTO);
}
```

---

#### 任务 3.3：实现 AlbumController.getDetail() 接口

- **目标**: 实现获取专辑详情接口
- **输入**: 阶段 2 实现的 `AlbumService.getAlbumDetail()` 方法
- **输出**: 包含 `getDetail()` 方法的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 15分钟

**API设计**:
- **请求方式**: GET
- **请求路径**: `/api/album/{id}`
- **路径参数**: `id` (专辑ID)
- **响应格式**: `AlbumDetailVO`（由 GlobalResponseAdvice 自动封装为 Result）

**技术要点**:
- 使用 `@GetMapping("/{id}")` 映射请求
- 使用 `@PathVariable` 绑定路径参数
- 使用 `@Operation` 注解生成 Swagger 文档
- 参考 `MusicController.getDetail()` 实现模式

**示例代码**:
```java
/**
 * 获取专辑详情
 *
 * @param id 专辑ID
 * @return 专辑详情
 */
@Operation(summary = "获取专辑详情", description = "根据专辑ID查询详细信息")
@GetMapping("/{id}")
public AlbumDetailVO getDetail(
        @Parameter(description = "专辑ID", example = "1", required = true)
        @PathVariable Long id) {
    return albumService.getAlbumDetail(id);
}
```

---

#### 任务 3.4：实现 AlbumController.create() 接口

- **目标**: 实现创建专辑接口
- **输入**: 阶段 2 实现的 `AlbumService.createAlbum()` 方法
- **输出**: 包含 `create()` 方法的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 20分钟

**API设计**:
- **请求方式**: POST
- **请求路径**: `/api/album`
- **请求体**: `AlbumCreateDTO` (JSON 格式)
- **响应格式**: `Long`（新增专辑的ID，由 GlobalResponseAdvice 自动封装为 Result）

**技术要点**:
- 使用 `@PostMapping` 映射请求
- 使用 `@RequestBody` 绑定请求体
- 使用 `@Valid` 触发参数校验
- 使用 `@Operation` 注解生成 Swagger 文档

**示例代码**:
```java
/**
 * 创建专辑
 *
 * @param createDTO 创建参数
 * @return 新增专辑的ID
 */
@Operation(summary = "创建专辑", description = "新增一个专辑记录")
@PostMapping
public Long create(
        @Parameter(description = "专辑创建参数", required = true)
        @Valid @RequestBody AlbumCreateDTO createDTO) {
    return albumService.createAlbum(createDTO);
}
```

---

#### 任务 3.5：实现 AlbumController.update() 接口

- **目标**: 实现更新专辑接口
- **输入**: 阶段 2 实现的 `AlbumService.updateAlbum()` 方法
- **输出**: 包含 `update()` 方法的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 20分钟

**API设计**:
- **请求方式**: PUT
- **请求路径**: `/api/album/{id}`
- **路径参数**: `id` (专辑ID)
- **请求体**: `AlbumUpdateDTO` (JSON 格式)
- **响应格式**: `Boolean`（是否更新成功，由 GlobalResponseAdvice 自动封装为 Result）

**技术要点**:
- 使用 `@PutMapping("/{id}")` 映射请求
- 使用 `@PathVariable` 绑定路径参数
- 使用 `@RequestBody` 绑定请求体
- 使用 `@Valid` 触发参数校验
- 使用 `@Operation` 注解生成 Swagger 文档

**示例代码**:
```java
/**
 * 更新专辑
 *
 * @param id 专辑ID
 * @param updateDTO 更新参数
 * @return 是否更新成功
 */
@Operation(summary = "更新专辑", description = "根据专辑ID更新专辑信息")
@PutMapping("/{id}")
public Boolean update(
        @Parameter(description = "专辑ID", example = "1", required = true)
        @PathVariable Long id,
        @Parameter(description = "专辑更新参数", required = true)
        @Valid @RequestBody AlbumUpdateDTO updateDTO) {
    return albumService.updateAlbum(id, updateDTO);
}
```

---

#### 任务 3.6：实现 AlbumController.delete() 接口

- **目标**: 实现删除专辑接口
- **输入**: 阶段 2 实现的 `AlbumService.deleteAlbum()` 方法
- **输出**: 包含 `delete()` 方法的 `AlbumController.java`
- **涉及文件**:
  - `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` (修改)
- **预估工作量**: 15分钟

**API设计**:
- **请求方式**: DELETE
- **请求路径**: `/api/album/{id}`
- **路径参数**: `id` (专辑ID)
- **响应格式**: `Boolean`（是否删除成功，由 GlobalResponseAdvice 自动封装为 Result）

**技术要点**:
- 使用 `@DeleteMapping("/{id}")` 映射请求
- 使用 `@PathVariable` 绑定路径参数
- 使用 `@Operation` 注解生成 Swagger 文档

**示例代码**:
```java
/**
 * 删除专辑（逻辑删除）
 *
 * @param id 专辑ID
 * @return 是否删除成功
 */
@Operation(summary = "删除专辑", description = "根据专辑ID逻辑删除专辑")
@DeleteMapping("/{id}")
public Boolean delete(
        @Parameter(description = "专辑ID", example = "1", required = true)
        @PathVariable Long id) {
    return albumService.deleteAlbum(id);
}
```

---

### 阶段 4：测试验证

#### 任务 4.1：通过 Swagger UI 测试所有接口

- **目标**: 验证所有 API 接口功能正确性
- **输入**: 阶段 3 完成的 `AlbumController`
- **输出**: 测试报告（Markdown 格式）
- **涉及文件**: 无（测试验证）
- **预估工作量**: 30分钟

**测试步骤**:
1. 启动 Spring Boot 应用
2. 访问 Swagger UI: http://localhost:8910/swagger-ui/index.html
3. 找到"专辑管理"API 分组
4. 按顺序测试以下接口：

**测试用例**:

| 接口 | 测试场景 | 预期结果 |
|------|---------|---------|
| **POST /api/album** | 创建专辑（name="七里香"） | 返回新增ID，code=200 |
| **GET /api/album/{id}** | 查询刚创建的专辑详情 | 返回专辑信息，code=200 |
| **GET /api/album/list** | 查询专辑列表（pageNum=1, pageSize=10） | 返回分页数据，code=200 |
| **GET /api/album/list** | 关键词搜索（keyword="七里香"） | 返回包含"七里香"的专辑，code=200 |
| **PUT /api/album/{id}** | 更新专辑（description="经典专辑"） | 返回 true，code=200 |
| **DELETE /api/album/{id}** | 删除专辑 | 返回 true，code=200 |
| **GET /api/album/{id}** | 查询已删除的专辑 | 抛出异常，code=500 |

**验收标准**:
- 所有接口返回正确的状态码和数据格式
- 参数校验生效（如 name 为空时返回 400 错误）
- 异常处理生效（如查询不存在的 ID 返回业务异常）
- Swagger 文档显示正确

---

#### 任务 4.2：编写单元测试（可选）

- **目标**: 为 Service 层和 Controller 层编写单元测试
- **输入**: 阶段 2 和阶段 3 完成的代码
- **输出**: 单元测试类
- **涉及文件**:
  - `src/test/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImplTest.java` (新建)
  - `src/test/java/com/naruto/wangyiyunmusic/controller/AlbumControllerTest.java` (新建)
- **预估工作量**: 60分钟

**测试内容**:
- AlbumService 测试：分页查询、详情查询、创建、更新、删除
- AlbumController 测试：使用 MockMvc 测试 RESTful API

**技术要点**:
- 使用 JUnit 5 + Mockito
- 使用 `@SpringBootTest` 和 `@AutoConfigureMockMvc`
- 使用 `@MockBean` 模拟依赖

**说明**: 本任务为可选任务，如果时间充足建议完成

---

## 需要进一步明确的问题

### 问题 1：专辑详情是否需要包含歌曲列表？

**推荐方案**:

- **方案 A**：仅返回专辑基本信息 + 歌曲数量（性能优先）
  - 优点：查询速度快，减少数据库压力
  - 缺点：需要额外调用音乐列表接口

- **方案 B**：返回专辑基本信息 + 歌曲列表（功能完整）
  - 优点：一次查询获取完整信息
  - 缺点：数据量大，查询性能较低

**✅ 用户已选择**:

```
[✓] 方案 A（推荐）- 仅返回专辑基本信息 + 歌曲数量
    理由：性能优先，减少数据库压力，职责清晰
```

---

### 问题 2：AlbumUpdateDTO 是否复用 AlbumCreateDTO？

**推荐方案**:

- **方案 A**：创建独立的 AlbumUpdateDTO 类（代码清晰）
  - 优点：职责明确，便于未来扩展（如更新时支持部分字段更新）
  - 缺点：代码冗余

- **方案 B**：AlbumUpdateDTO 继承 AlbumCreateDTO（代码复用）
  - 优点：减少重复代码
  - 缺点：耦合度高，未来扩展性差

**✅ 用户已选择**:

```
[✓] 方案 A（推荐）- 创建独立的 AlbumUpdateDTO 类
    理由：职责明确，代码清晰，便于未来扩展
```

---

### 问题 3：是否需要添加专辑歌手关联功能？

当前数据库设计中，专辑和歌手之间可能存在关联关系（如某专辑是某歌手的专辑），但本次任务范围未明确是否需要实现。

**推荐方案**:

- **方案 A**：暂不实现，本次仅实现专辑基础 CRUD（简化优先）
  - 优点：任务范围明确，快速完成
  - 缺点：专辑信息不完整

- **方案 B**：实现专辑-歌手关联查询（功能完整）
  - 优点：专辑详情包含歌手信息
  - 缺点：增加开发工作量

**✅ 用户已选择**:

```
[✓] 方案 A（推荐）- 暂不实现专辑歌手关联功能
    理由：聚焦核心 CRUD 功能，符合单一职责原则，后续如需可扩展
```

---

### 问题 4：删除专辑时是否需要检查关联数据？

当前专辑可能与音乐（music 表）存在外键关联，删除专辑时需要考虑以下情况：

**推荐方案**:

- **方案 A**：删除前检查是否有歌曲关联，有则禁止删除（安全优先）
  - 优点：数据一致性强，避免误删
  - 缺点：删除操作受限

- **方案 B**：直接逻辑删除专辑，不检查关联数据（简化优先）
  - 优点：操作简单
  - 缺点：可能产生孤儿数据

- **方案 C**：删除专辑时级联删除关联歌曲（彻底清理）
  - 优点：数据干净
  - 缺点：误删风险高

**✅ 用户已选择**:

```
[✓] 方案 A（推荐）- 删除前检查是否有歌曲关联，有则禁止删除
    理由：数据安全优先，保证数据一致性，避免误删除
```

---

## 用户反馈区域

**✅ 所有技术决策已确认完毕，采用全部推荐方案 (方案 A)**

技术决策汇总：
1. ✓ 专辑详情仅返回基本信息 + 歌曲数量
2. ✓ 创建独立的 AlbumUpdateDTO 类
3. ✓ 暂不实现专辑歌手关联功能
4. ✓ 删除前检查歌曲关联

**规划状态**: 已完成，等待开始执行实施

```
用户补充内容：

---

---

---

```

---

## 附录：文件清单

### 需要新建的文件（9个）

| 文件路径 | 文件说明 |
|---------|---------|
| `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumQueryDTO.java` | 专辑查询参数对象 |
| `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumCreateDTO.java` | 专辑创建请求对象 |
| `src/main/java/com/naruto/wangyiyunmusic/model/dto/AlbumUpdateDTO.java` | 专辑更新请求对象 |
| `src/main/java/com/naruto/wangyiyunmusic/model/vo/AlbumListVO.java` | 专辑列表视图对象 |
| `src/main/java/com/naruto/wangyiyunmusic/model/vo/AlbumDetailVO.java` | 专辑详情视图对象 |
| `src/test/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImplTest.java` | Service 单元测试（可选） |
| `src/test/java/com/naruto/wangyiyunmusic/controller/AlbumControllerTest.java` | Controller 单元测试（可选） |

### 需要修改的文件（3个）

| 文件路径 | 修改说明 |
|---------|---------|
| `src/main/java/com/naruto/wangyiyunmusic/service/AlbumService.java` | 添加业务方法签名 |
| `src/main/java/com/naruto/wangyiyunmusic/service/impl/AlbumServiceImpl.java` | 实现业务逻辑方法 |
| `src/main/java/com/naruto/wangyiyunmusic/controller/AlbumController.java` | 实现 RESTful API 接口 |

### 不需要修改的文件（2个）

| 文件路径 | 说明 |
|---------|------|
| `src/main/java/com/naruto/wangyiyunmusic/model/entity/Album.java` | 实体类已完整，无需修改 |
| `src/main/java/com/naruto/wangyiyunmusic/mapper/AlbumMapper.java` | Mapper 接口已完整，无需修改 |

---

## 技术要点总结

### 1. MyBatis-Plus 使用技巧
- 继承 `IService<Album>` 获得 `save()`, `getById()`, `updateById()`, `removeById()` 等基础方法
- 使用 `LambdaQueryWrapper` 构建类型安全的查询条件
- 使用 `@TableLogic` 注解实现逻辑删除
- 使用 `Page` 对象实现分页查询

### 2. 对象转换技巧
- 使用 `BeanUtils.copyProperties(source, target)` 进行对象拷贝
- 使用 Java 8 Stream API 进行批量转换

### 3. 参数校验技巧
- 使用 `@Valid` 注解触发校验
- 使用 `@NotBlank`, `@Size` 等注解定义校验规则
- 校验失败由 `GlobalExceptionHandler` 统一处理

### 4. 异常处理技巧
- 使用 `BusinessException` 抛出业务异常
- 由 `GlobalExceptionHandler` 统一捕获并返回错误响应

### 5. Swagger 文档技巧
- 使用 `@Tag` 注解对 API 分组
- 使用 `@Operation` 注解描述接口
- 使用 `@Parameter` 注解描述参数
- 使用 `@Schema` 注解描述模型字段

---

## 预估总工作量

| 阶段 | 任务数 | 预估时间 |
|------|-------|---------|
| **阶段 1：DTO & VO** | 5 | 95分钟 |
| **阶段 2：Service** | 6 | 165分钟 |
| **阶段 3：Controller** | 6 | 95分钟 |
| **阶段 4：测试验证** | 2 | 90分钟 |
| **合计** | 19 | 445分钟（约7.5小时） |

**说明**:
- 以上预估为单人开发时间
- 不包括需求沟通、代码评审、问题排查时间
- 单元测试为可选任务，如跳过可减少约60分钟

---

## 实施建议

1. **按阶段顺序开发**：先完成 DTO/VO → Service → Controller，确保依赖关系清晰
2. **参考现有代码**：参考 `MusicController` 和 `MusicServiceImpl` 的实现模式
3. **及时测试验证**：每完成一个 API 接口，立即通过 Swagger UI 测试
4. **遵循代码规范**：使用中文注释，遵循阿里巴巴 Java 开发手册
5. **代码复用**：充分利用 MyBatis-Plus 提供的基础方法，避免重复造轮子
6. **日志记录**：在关键业务方法中添加日志，便于排查问题

---

**文档版本**: v1.0
**创建时间**: 2026-02-01
**作者**: AI Assistant
**审核状态**: 待用户审核
