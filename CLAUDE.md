# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个 AI 面试系统的后端服务，基于 Spring Boot 3.2.0 开发，使用 Java 17。项目为前后端分离架构，为前端提供接口，不要使用restful风格，我不喜欢他。
我希望每当我有需求提出或者改动的时候，如果和该文档有冲突，你要及时修改文档。如果是文档中没有记录的，请按需记录到文档中，readme文件也应该适时更新。

## 核心技术栈

- **框架**: Spring Boot 3.2.0
- **Java 版本**: 17
- **数据库**: H2 内存数据库（开发环境）
- **ORM 框架**: MyBatis Plus 3.5.5
- **构建工具**: Maven
- **端口**: 8080

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

### 数据库访问
- H2 控制台: http://localhost:8080/h2-console
- 数据库连接: jdbc:h2:mem:testdb
- 用户名: sa
- 密码: password

## 项目架构

### 包结构
```
com.example.aiinterview/
├── AiInterviewApplication.java     # 主启动类
├── config/                         # 配置类
│   └── MybatisPlusConfig.java      # MyBatis Plus 配置
└── controller/                     # REST 控制器层
    └── HelloController.java        # 基础 API 接口
```

### API 接口设计
- 基础路径: `/api`
- 不要使用rest风格
- CORS 配置: 允许 http://localhost:5173 的跨域请求
- 当前可用接口:
  - `GET /api/hello` - 返回欢迎信息
  - `GET /api/status` - 返回服务状态

### 关键配置
- 服务端口: 8080
- 应用名称: ai-interview-backend
- 配置文件: application.yml（YAML 格式）
- 数据库: H2 内存数据库，开启 Web 控制台
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

### 扩展建议
后续开发可能需要添加的模块：
- `service/` - 业务逻辑层
- `entity/` - 数据实体类
- `mapper/` - MyBatis Plus 数据访问层
- `dto/` - 数据传输对象
- `config/` - 配置类（已创建）

### MyBatis Plus 使用说明
- Mapper 接口需继承 `BaseMapper<T>`
- 实体类使用 `@TableName` 注解指定表名
- 主键使用 `@TableId` 注解，推荐使用雪花算法
- 逻辑删除字段使用 `@TableLogic` 注解
- 分页查询使用 `IPage<T>` 接口
