# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 AI 面试系统的后端服务，基于 Spring Boot 3.2.0 开发，使用 Java 17。项目为前后端分离架构，为前端提供接口，不要使用restful风格，我不喜欢他。
我希望每当我有需求提出或者改动的时候，如果和该文档有冲突，你要及时修改文档。如果是文档中没有记录的，请按需记录到文档中，readme文件也应该适时更新。

## 核心技术栈

- **框架**: Spring Boot 3.2.0
- **Java 版本**: 17
- **数据库**: MySQL 8.0
- **ORM 框架**: MyBatis Plus 3.5.5
- **构建工具**: Maven
- **端口**: 8080
- **AI 集成**: 豆包AI（深度思考 + 联网模式）
- **缓存**: Spring Data Redis
- **工具库**: Hutool、FastJSON、Lombok

## 开发命令

### 构建和运行
```bash
# 编译项目
mvn compile

# 运行应用
mvn spring-boot:run

# 打包应用
mvn package

# 运行测试
mvn test

# 清理构建文件
mvn clean
```

### 数据库连接
- MySQL 数据库: localhost:3306
- 数据库名称: ai_interview
- 用户名: root
- 密码: root
- 连接URL: `jdbc:mysql://localhost:3306/ai_interview?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true`

## 项目架构

### 包结构
```
com.example.aiinterview/
├── AiInterviewApplication.java     # 主启动类
├── config/                         # 配置类
│   ├── MybatisPlusConfig.java      # MyBatis Plus 配置
│   └── RedisCache.java             # Redis缓存工具类
├── controller/                     # REST 控制器层
│   ├── HelloController.java        # 基础 API 接口
│   └── AiController.java           # AI问答接口控制器
└── util/                           # 工具类
    └── DoubaoUtil.java             # 豆包AI工具类
```

### API 接口设计
- 基础路径: `/api`
- 不要使用rest风格
- CORS 配置: 允许 http://localhost:5173 的跨域请求
- 当前可用接口:
  - `GET /api/hello` - 返回欢迎信息
  - `GET /api/status` - 返回服务状态
  - `POST /api/ai/chat` - AI问答（深度思考模式，优先豆包AI）
  - `POST /api/ai/chat-network` - AI问答（联网模式，优先豆包AI）
  - `POST /api/ai/chat-http` - AI问答（HTTP模式）
  - `POST /api/ai/chat-simple` - AI问答（简化模式）
  - `GET /api/ai/status` - AI服务状态

### 关键配置
- 服务端口: 8080
- 应用名称: ai-interview-backend
- 配置文件: application.yml（YAML 格式）
- 数据库: MySQL 8.0，支持自动建表（ddl-auto: update）
- MyBatis Plus: 
  - 开启驼峰命名转换
  - 启用 SQL 日志输出
  - 配置逻辑删除（deleted 字段）
  - 分页插件已配置

## 开发注意事项

### 文件组织原则
- Java 文件遵循 500 行以内的限制
- 每个文件夹不超过 8 个文件
- 按功能模块组织包结构

### 代码规范
- 避免循环依赖
- 保持单一职责原则
- 使用清晰的命名约定
- 及时重构重复代码
- **优先使用Lombok注解进行依赖注入**：
  - 使用`@RequiredArgsConstructor`替代字段注入
  - 必需依赖使用`private final`字段
  - 可选依赖使用`private final Optional<T>`配合`@RequiredArgsConstructor`
  - 避免使用`@Autowired(required = false)`，改用Optional模式

### 扩展建议
后续开发可能需要添加的模块：
- `service/` - 业务逻辑层
- `entity/` - 数据实体类
- `mapper/` - MyBatis Plus 数据访问层
- `dto/` - 数据传输对象
- `config/` - 配置类（已创建）
- `util/` - 工具类（已创建）

## AI功能模块

### AI集成说明
- **豆包AI模式**: 优先使用豆包AI的深度思考和联网功能（如果依赖可用）
- **HTTP模式**: 使用WebClient直接调用AI API，支持高级和标准两种模式
- **简化模式**: 本地模拟AI响应，用于开发测试
- **智能降级**: 系统会自动检测豆包AI是否可用，不可用时自动降级到HTTP模式
- **流式响应**: 支持SSE（Server-Sent Events）实现实时消息推送
- **超时设置**: 豆包AI和HTTP模式10分钟，简化模式5分钟

### 依赖问题解决方案
由于豆包AI SDK在某些环境下可能无法正常下载，系统采用了以下解决方案：

1. **添加国内镜像**: 配置阿里云Maven仓库，提高依赖下载成功率
2. **HTTP替代方案**: 使用Spring WebFlux直接调用AI API，不依赖特定SDK
3. **多重备份**: 提供HTTP模式和简化模式两套方案，确保系统可用性

### Maven仓库配置
```xml
<!-- 阿里云公共仓库 -->
<repository>
    <id>aliyun</id>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
</repository>
```

### AI接口使用方法
```bash
# 深度思考模式（优先豆包AI，自动降级）
curl -X POST "http://localhost:8080/api/ai/chat" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 联网模式（优先豆包AI，自动降级）
curl -X POST "http://localhost:8080/api/ai/chat-network" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 强制使用HTTP模式
curl -X POST "http://localhost:8080/api/ai/chat-http" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 简化模式（本地模拟，始终可用）
curl -X POST "http://localhost:8080/api/ai/chat-simple" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 检查AI服务状态
curl -X GET "http://localhost:8080/api/ai/status"
```

### 实际AI API集成
在生产环境中，可以在HttpAiUtil类中配置真实的AI API：

```java
// 支持的AI服务商
- OpenAI GPT系列
- 百度文心一言  
- 阿里通义千问
- 腾讯混元
- 字节豆包
```

### Maven依赖刷新
```bash
# 清理并重新下载依赖
mvn clean install

# 强制更新依赖
mvn clean install -U
```

### 依赖库版本
- volcengine-java-sdk-ark-runtime: LATEST（豆包AI SDK）
- spring-boot-starter-webflux: 自动版本管理（用于HTTP AI调用）
- okhttp: 4.12.0  
- rxjava: 3.1.8
- fastjson: 1.2.83
- hutool-all: 5.8.22
- lombok: 自动版本管理

### 豆包AI配置
- API Key: f829290f-bb20-4692-904a-b812e0da770b（请替换为你自己的密钥）
- 深度思考模型: doubao-1-5-thinking-vision-pro-250428
- 联网机器人: bot-20250514171832-w4g9f

### MyBatis Plus 使用说明
- Mapper 接口需继承 `BaseMapper<T>`
- 实体类使用 `@TableName` 注解指定表名
- 主键使用 `@TableId` 注解，推荐使用雪花算法
- 逻辑删除字段使用 `@TableLogic` 注解
- 分页查询使用 `IPage<T>` 接口
