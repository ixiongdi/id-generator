# ID Generator Spring AI MCP Server

这是一个基于 Model Context Protocol (MCP) 的 Spring Boot 应用，为 AI 客户端提供 ID 生成工具服务。本项目已升级到 Spring AI 1.0.1 版本，使用现代化的 `@Tool` 注解实现。

## 功能特性

- 支持多种 ID 生成算法
- 遵循 MCP 协议标准 (JSON-RPC 2.0)
- RESTful API 接口
- Spring Boot 自动配置
- 完整的工具列表和描述
- Spring AI 1.0.1 @Tool 注解支持
- 自动工具发现机制
- 兼容传统 MCP 客户端

## 支持的 ID 类型

1. **UUID 系列**
   - UUID v1: 基于时间和MAC地址
   - UUID v2: DCE 安全版本
   - UUID v4: 随机生成
   - UUID v6: 重排序的时间有序
   - UUID v7: 时间有序（推荐）

2. **分布式 ID**
   - ULID: 时间排序的唯一标识符
   - KSUID: K-Sortable 唯一标识符
   - Elasticflake: 弹性雪花算法

3. **其他格式**
   - Nano ID: 小巧的 URL 安全 ID
   - ObjectId: MongoDB 风格的 ID
   - CUID: 防冲突唯一标识符 (v1, v2)
   - Push ID: Firebase 风格的 Push ID
   - XID: eXtensible IDentifier
   - COMB GUID: 顺序 GUID
   - SID: 可排序标识符

4. **特殊 ID**
   - Business ID: 基于时间的业务ID
   - Entropy ID: 基于时间的熵ID
   - JavaScript 安全 ID: JavaScript安全的数字ID

## 快速开始

### 启动服务

```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 上启动。

### API 端点

1. **传统 MCP 端点**
   - 初始化: `GET /mcp/initialize`
   - 列出工具: `GET /mcp/tools/list`
   - 调用工具: `POST /mcp/tools/call`

2. **Spring AI 端点**
   - 列出工具: `GET /mcp/ai/tools`
   - 调用工具: `POST /mcp/ai/tools/{toolName}`

### 示例请求

生成 UUID v7 (传统方式):
```json
{
  "jsonrpc": "2.0",
  "id": "1",
  "method": "tools/call",
  "params": {
    "name": "generate_uuid",
    "arguments": {
      "version": "v7"
    }
  }
}
```

生成 ULID (传统方式):
```json
{
  "jsonrpc": "2.0",
  "id": "2",
  "method": "tools/call",
  "params": {
    "name": "generate_ulid",
    "arguments": {}
  }
}
```

调用 Spring AI 工具 (现代方式):
```bash
# 列出所有可用工具
curl http://localhost:8080/mcp/ai/tools

# 调用特定工具
curl -X POST http://localhost:8080/mcp/ai/tools/generate_uuid \
  -H "Content-Type: application/json" \
  -d '{"version": "v7"}'
```

## Spring AI 1.0.1 集成

本项目充分利用了 Spring AI 1.0.1 的新特性：

1. **@Tool 注解**
   - 使用 `@Tool` 注解标记方法
   - 自动工具发现和注册
   - 类型安全的参数处理
   - 丰富的元数据支持

2. **自动配置**
   - Spring AI BOM 管理依赖版本
   - 自动工具扫描
   - 函数回调机制

3. **双模式支持**
   - 保持对传统 MCP 客户端的兼容
   - 提供现代化的 Spring AI API

## MCP 协议集成

这个服务器实现了 Model Context Protocol，可以被支持 MCP 的 AI 客户端（如 Claude Desktop、VS Code 等）作为工具提供者使用。

## 开发

### 构建项目

```bash
mvn clean compile
```

### 运行测试

```bash
mvn test
```

### 打包

```bash
mvn clean package
```

## 技术栈

- Spring Boot 3.x
- Spring AI 1.0.1
- Spring Web MVC
- Jackson JSON 处理
- Maven 构建工具
- Java 21+

## 许可证

MIT License