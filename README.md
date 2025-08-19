# AI面试系统后端服务

## 项目简介

这是一个基于Spring Boot开发的AI面试系统后端服务，集成了豆包AI功能，为用户提供智能化的面试问答服务。

## 技术栈

- **Java**: 17
- **Spring Boot**: 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis Plus 3.5.5
- **AI集成**: 豆包AI
- **构建工具**: Maven

## 环境要求

### 数据库环境
1. 安装MySQL 8.0
2. 创建数据库：
```sql
CREATE DATABASE ai_interview CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Java环境
- JDK 17或以上版本
- Maven 3.6或以上版本

## 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd ai-interview-backend
```

### 2. 配置数据库
编辑 `src/main/resources/application.yml` 文件，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ai_interview?useSSL=false&serverTimezone=UTC&characterEncoding=utf8&allowPublicKeyRetrieval=true
    username: root
    password: your_password
```

### 3. 安装依赖并启动
```bash
# 安装依赖
mvn clean install

# 启动应用
mvn spring-boot:run
```

## API接口

应用启动后，可以通过以下接口访问服务：

### 基础接口
- `GET /api/hello` - 返回欢迎信息
- `GET /api/status` - 返回服务状态

### AI对话接口
- `POST /api/ai/chat` - AI问答（深度思考模式）
- `POST /api/ai/chat-network` - AI问答（联网模式）
- `POST /api/ai/chat-http` - AI问答（HTTP模式）
- `POST /api/ai/chat-simple` - AI问答（简化模式）
- `GET /api/ai/status` - AI服务状态

### 使用示例
```bash
curl -X POST "http://localhost:8080/api/ai/chat" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "question=你好，请介绍一下Java的特点"
```

## 数据库配置

项目使用MySQL 8.0作为数据库，配置信息如下：
- **数据库**: ai_interview
- **端口**: 3306
- **编码**: utf8mb4
- **时区**: UTC
- **自动建表**: 支持（ddl-auto: update）

## 开发说明

### 项目结构
```
src/main/java/com/example/aiinterview/
├── AiInterviewApplication.java     # 主启动类
├── controller/                     # 控制器层
├── service/                        # 业务逻辑层
├── entity/                         # 实体类
├── mapper/                         # 数据访问层
├── config/                         # 配置类
└── util/                           # 工具类
```

### 开发命令
```bash
# 编译项目
mvn compile

# 运行测试
mvn test

# 打包应用
mvn package

# 清理构建文件
mvn clean
```

## 部署说明

### 生产环境配置
1. 修改数据库配置为生产环境数据库
2. 配置豆包AI的正式API密钥
3. 调整JVM参数和日志级别

### 打包部署
```bash
# 打包为jar文件
mvn clean package -DskipTests

# 运行jar包
java -jar target/ai-interview-backend-1.0.0.jar
```

## 许可证

本项目采用MIT许可证。