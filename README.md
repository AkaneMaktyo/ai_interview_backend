# AI Interview Backend

这是一个 AI 面试系统的后端服务，基于 Spring Boot 3.2.0 开发，使用 Java 17。

## 🚀 项目状态

**当前版本**: v1.0.0 - 最小可行版本  
**最后更新**: 2025-08-21  
**运行状态**: ✅ 正常运行  

## 📋 已完成功能

- ✅ Spring Boot 应用成功启动和运行 
- ✅ MySQL 数据库连接和7张核心表创建
- ✅ 用户管理完整CRUD功能（Spring JDBC + Lombok）
- ✅ 数据库连接测试接口
- ✅ 基础API接口（hello、status、test）

## 🛠️ 技术栈

- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **ORM**: Spring JDBC（已移除MyBatis Plus解决兼容性问题）
- **构建工具**: Maven
- **代码简化**: Lombok（@Data、@RequiredArgsConstructor）

## 📦 项目结构

```
src/main/java/com/example/aiinterview/
├── AiInterviewApplication.java     # 主启动类
├── entity/
│   └── User.java                   # 用户实体（使用Lombok）
├── service/
│   └── UserService.java            # 用户服务（构造器注入）
└── controller/
    ├── BasicController.java        # 基础API
    ├── DatabaseTestController.java # 数据库测试
    └── UserController.java         # 用户CRUD
```

## 🚀 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE ai_interview CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 中的数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_interview?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
    username: root
    password: root
```

3. 运行建表脚本：
```bash
mysql -u root -p ai_interview < create_tables.sql
```

### 运行应用

```bash
# 克隆项目
git clone <your-repo-url>
cd ai-interview-backend

# 编译项目
mvn compile

# 运行应用
mvn spring-boot:run
```

应用将在 `http://localhost:8080` 启动。

## 📡 API 接口

### 基础接口

- `GET /api/hello` - 服务状态检查
- `GET /api/status` - 详细运行状态
- `POST /api/test` - 基础功能测试

### 数据库测试

- `GET /api/database/test-connection` - 数据库连接测试
- `GET /api/database/tables` - 查看数据库表结构
- `GET /api/database/user-count` - 获取用户统计

### 用户管理

- `GET /api/users/list` - 获取所有用户
- `GET /api/users/get?id={id}` - 根据ID获取用户
- `POST /api/users/create` - 创建新用户
  - 参数: `username`, `email`, `nickname`（可选）, `level`（可选）
- `POST /api/users/update` - 更新用户信息
  - 参数: `id`, `username`（可选）, `email`（可选）, `nickname`（可选）, `level`（可选）
- `POST /api/users/delete` - 删除用户
  - 参数: `id`
- `GET /api/users/count` - 获取用户总数

## 🧪 测试示例

```bash
# 测试应用状态
curl http://localhost:8080/api/hello

# 获取所有用户
curl http://localhost:8080/api/users/list

# 创建新用户
curl -X POST "http://localhost:8080/api/users/create" \
  -d "username=testuser&email=test@example.com&nickname=测试用户"

# 测试数据库连接
curl http://localhost:8080/api/database/test-connection
```

## 📝 开发规范

- 使用 Lombok 简化代码，避免手写 getter/setter
- 优先使用构造器注入（`@RequiredArgsConstructor`）
- 实体类使用 `@Data` 注解
- 每个文件不超过 200 行（TypeScript/JavaScript）或 500 行（Java）
- 每个文件夹不超过 8 个文件

## 🗄️ 数据库表结构

系统包含以下核心表：
- `users` - 用户信息
- `knowledge_tags` - 知识点标签
- `questions` - 题目信息
- `answer_records` - 答题记录
- `wrong_questions` - 错题集
- `learning_stats` - 学习统计
- `interview_sessions` - 面试会话

## 🔧 故障排除

### 常见问题

1. **端口占用**: 如果8080端口被占用，请修改`application.yml`中的端口配置
2. **数据库连接失败**: 检查MySQL服务是否启动，用户名密码是否正确
3. **编译失败**: 确保Java 17和Maven版本正确

### 日志查看

应用日志级别设置为DEBUG，可以查看详细的执行信息。

## 📈 后续计划

- [ ] AI面试功能集成
- [ ] 题目推荐算法
- [ ] 学习进度跟踪
- [ ] 错题复习机制
- [ ] 性能统计分析

## 📄 许可证

MIT License

---

更多详细信息请参考 `CLAUDE.md` 文件。