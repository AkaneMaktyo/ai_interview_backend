# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 AI 面试系统的后端服务，基于 Spring Boot 3.2.0 开发，使用 Java 17。项目为前后端分离架构，为前端提供接口，不要使用restful风格，我不喜欢他。

## 最新进展 (2025-08-22)

✅ **已完成的功能模块：**
- Spring Boot 应用成功启动和运行 
- MySQL 数据库连接和7张核心表创建
- 用户管理完整CRUD功能（使用Spring JDBC + Lombok）
- 数据库连接测试接口
- 基础API接口（hello、status、test）

✅ **技术架构重构 (2025-08-22)：**
- ~~移除了MyBatis Plus（版本兼容性问题），改用Spring JDBC~~ → **重新迁移回MyBatis Plus 3.5.3.1**
- 全面使用Lombok简化代码（@Data, @RequiredArgsConstructor）
- 采用构造器注入替代字段注入，符合最佳实践
- 代码行数控制良好，实体类仅34行，符合200行限制
- **完成从Spring JDBC到MyBatis Plus的全面迁移**，提供更强大的ORM功能

✅ **代码质量提升 (2025-08-22)：**
- 为所有实体类字段添加了详细的中文注释，包括字段用途和取值范围说明
- ~~为所有Service层SQL语句添加了详细注释，说明每条SQL的具体作用~~ → **已迁移至MyBatis Plus，不再使用手写SQL**
- 实体类注释涵盖：User、Question、InterviewSession、AnswerRecord、KnowledgeTag
- ~~Service类注释涵盖：UserService中所有CRUD操作的SQL语句~~ → **现使用MyBatis Plus内置方法**
- 注释风格统一，使用JavaDoc标准格式，提高代码可维护性

✅ **豆包AI官方集成 (2025-08-22)：**
- 替换HTTP模拟方式，使用豆包AI官方SDK（volcengine-java-sdk-ark-runtime）
- 创建DoubaoUtil工具类，支持标准模式、深度思考模式、联网模式
- 新增AiController提供完整的AI对话接口
- 配置豆包AI参数：API Key、模型ID、机器人ID等
- 支持SSE流式响应，提供实时打字效果
- 移除HttpAiUtil和SimpleAiUtil等模拟实现

✅ **AI接口升级 (2025-08-22)：**
- `POST /api/ai/chat` - AI标准对话（使用豆包官方API）
- `POST /api/ai/chat/thinking` - AI深度思考模式
- `POST /api/ai/chat/network` - AI联网模式
- `GET /api/ai/status` - AI服务状态检查
- `POST /api/ai/test` - AI连接测试
- `GET /api/ai/connections` - 当前活跃连接统计
- 支持SSE（Server-Sent Events）流式响应

✅ **跨域配置优化 (2025-08-22)：**
- 移除所有Controller中的重复@CrossOrigin注解
- 新增CorsConfig统一配置类，支持全局CORS处理
- 支持localhost和127.0.0.1的多端口访问
- 特别优化SSE流式响应的跨域配置

✅ **时间字段标准化 (2025-08-22)：**
- 统一所有实体类时间字段命名：createdAt → createTime, updatedAt → updateTime
- 新增MyBatisPlusConfig配置类，实现时间字段自动填充
- 移除实体类构造函数中的手动时间设置，完全依赖MyBatis Plus自动填充
- 配置@TableField(fill = FieldFill.INSERT)用于createTime自动插入
- 配置@TableField(fill = FieldFill.INSERT_UPDATE)用于updateTime自动更新
- 提升数据一致性，避免手动设置时间导致的错误

✅ **当前可用API接口：**
- `GET /api/hello` - 服务状态检查
- `GET /api/status` - 详细运行状态  
- `POST /api/test` - 基础功能测试
- `GET /api/database/test-connection` - 数据库连接测试
- `GET /api/database/tables` - 查看数据库表结构
- `GET /api/database/user-count` - 获取用户统计
- **用户管理接口：**
  - `GET /api/users/list` - 获取所有用户
  - `GET /api/users/get?id={id}` - 根据ID获取用户
  - `POST /api/users/create` - 创建新用户（username, email, nickname, level）
  - `POST /api/users/update` - 更新用户信息  
  - `POST /api/users/delete` - 删除用户
  - `GET /api/users/count` - 获取用户总数
- **AI对话接口（豆包官方API）：**
  - `POST /api/ai/chat` - AI标准对话
  - `POST /api/ai/chat/thinking` - AI深度思考模式
  - `POST /api/ai/chat/network` - AI联网模式
  - `GET /api/ai/status` - AI服务状态检查
  - `POST /api/ai/test` - AI连接测试
  - `GET /api/ai/connections` - 活跃连接统计
我希望每当我有需求提出或者改动的时候，如果和该文档有冲突，你要及时修改文档。如果是文档中没有记录的，请按需记录到文档中，readme文件也应该适时更新。

## 核心技术栈

- **框架**: Spring Boot 3.2.0
- **Java 版本**: 17
- **数据库**: MySQL 8.0
- **ORM 框架**: ~~MyBatis Plus 3.5.5~~ → **MyBatis Plus 3.5.3.1**（重新启用，替换Spring JDBC）
- **构建工具**: Maven
- **端口**: 8080
- **AI 集成**: 豆包AI（官方SDK volcengine-java-sdk-ark-runtime）
- **缓存**: Spring Data Redis
- **工具库**: **Lombok**（@Data、@RequiredArgsConstructor）、Hutool、FastJSON

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
com.zsj.aiinterview/
├── AiInterviewApplication.java     # 主启动类（已添加@MapperScan）
├── entity/                         # 实体类（使用MyBatis Plus注解）
│   ├── User.java                   # 用户实体（@TableName("users")）
│   ├── Question.java               # 题目实体（@TableName("questions")）
│   ├── InterviewSession.java       # 面试会话实体（@TableName("interview_sessions")）
│   ├── AnswerRecord.java           # 答题记录实体（@TableName("answer_records")）
│   └── KnowledgeTag.java           # 知识点标签实体（@TableName("knowledge_tags")）
├── mapper/                         # MyBatis Plus Mapper接口
│   ├── UserMapper.java             # 用户Mapper接口
│   ├── QuestionMapper.java         # 题目Mapper接口
│   ├── InterviewSessionMapper.java # 面试会话Mapper接口
│   ├── AnswerRecordMapper.java     # 答题记录Mapper接口
│   └── KnowledgeTagMapper.java     # 知识点标签Mapper接口
├── service/                        # 业务逻辑层（继承ServiceImpl）
│   ├── UserService.java            # 用户服务（MyBatis Plus风格）
│   ├── QuestionService.java        # 题目服务
│   ├── InterviewSessionService.java# 面试会话服务
│   ├── AnswerRecordService.java    # 答题记录服务
│   └── KnowledgeTagService.java    # 知识点标签服务
├── controller/                     # REST 控制器层
│   ├── BasicController.java        # 基础 API 接口
│   ├── DatabaseTestController.java # 数据库测试接口
│   ├── UserController.java         # 用户管理CRUD接口
│   └── AiController.java           # AI对话接口（豆包官方API）
└── util/                           # 工具类
    ├── DoubaoUtil.java             # 豆包AI官方SDK工具类
    └── ResponseUtil.java           # 响应工具类
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
- **优先使用Lombok注解**：
  - 实体类使用`@Data`自动生成getter/setter/toString/equals/hashCode
  - 使用`@RequiredArgsConstructor`进行构造器依赖注入
  - 必需依赖使用`private final`字段
  - 避免使用`@Autowired`字段注入，改用构造器注入
- **MyBatis Plus最佳实践**：
  - 实体类使用MyBatis Plus注解：`@TableName`、`@TableId`、`@TableField`
  - Service层继承`ServiceImpl<Mapper, Entity>`，使用内置CRUD方法
  - Mapper接口继承`BaseMapper<Entity>`，自动获得基础操作
  - 使用`QueryWrapper`和`LambdaQueryWrapper`构建复杂查询条件
  - 配置自动填充时间字段：`@TableField(fill = FieldFill.INSERT)`

### 扩展建议
后续开发可能需要添加的模块：
- `service/` - 业务逻辑层（✅ 已创建所有Service类，使用MyBatis Plus）
- `entity/` - 数据实体类（✅ 已创建所有核心实体，添加MyBatis Plus注解）
- `mapper/` - MyBatis Plus Mapper接口（✅ 已创建所有Mapper接口）
- `dto/` - 数据传输对象
- `config/` - 配置类（需要添加MyBatis Plus自动填充配置）
- `util/` - 工具类（已创建基础结构）

## AI功能模块

### AI集成说明
- **豆包AI官方SDK**: 使用火山引擎豆包AI官方SDK（volcengine-java-sdk-ark-runtime）
- **多种对话模式**: 标准模式、深度思考模式、联网模式
- **流式响应**: 使用SSE（Server-Sent Events）提供实时打字效果
- **智能降级**: SDK不可用时提供友好的错误提示
- **超时设置**: 标准模式5分钟，思考/联网模式10分钟
- **连接管理**: 支持多个并发连接，自动清理过期连接

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

### 豆包AI配置
- API Key: f829290f-bb20-4692-904a-b812e0da770b（请替换为你自己的密钥）
- 基础URL: https://ark.cn-beijing.volces.com/api/v3
- 标准模型: doubao-pro-4k
- 深度思考模型: doubao-1-5-thinking-vision-pro-250428
- 联网机器人: bot-20250514171832-w4g9f

### AI接口使用方法
```bash
# 标准对话模式
curl -X POST "http://localhost:8080/api/ai/chat" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 深度思考模式
curl -X POST "http://localhost:8080/api/ai/chat/thinking" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 联网模式
curl -X POST "http://localhost:8080/api/ai/chat/network" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你的问题"

# 检查AI服务状态
curl -X GET "http://localhost:8080/api/ai/status"

# 测试AI连接
curl -X POST "http://localhost:8080/api/ai/test"

# 查看活跃连接
curl -X GET "http://localhost:8080/api/ai/connections"
```

### MyBatis Plus 使用说明
- Mapper 接口需继承 `BaseMapper<T>`
- 实体类使用 `@TableName` 注解指定表名
- 主键使用 `@TableId` 注解，推荐使用雪花算法
- 逻辑删除字段使用 `@TableLogic` 注解
- 分页查询使用 `IPage<T>` 接口
