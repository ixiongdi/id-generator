# ID Generator Spring Redis

基于 Spring Boot 和 Redis 的分布式 ID 生成器实现，提供高性能、可靠的分布式唯一 ID 生成服务。

## 功能特点

- 基于雪花算法（Snowflake）的 ID 生成
- 使用 Redis 实现分布式机器 ID 分配
- 支持 Spring Boot 自动配置
- 高性能、低延迟的 ID 生成
- 时钟回拨处理机制

## 快速开始

### Maven 依赖

```xml
<dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-spring-redis</artifactId>
    <version>${latest.version}</version>
</dependency>
```

### 配置说明

在 application.properties 或 application.yml 中配置 Redis 连接信息：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### 使用示例

```java
@Autowired
private SnowflakeIdGenerator snowflakeIdGenerator;

// 生成ID
SnowflakeId id = snowflakeIdGenerator.generate();
```

## 核心功能

### 雪花算法 ID 生成器

雪花算法生成的 ID 由以下部分组成：

- 时间戳（毫秒级）
- 机器 ID（由 Redis 分配）
- 序列号（同一毫秒内的自增序列）

主要特点：

- 保证全局唯一性
- 趋势递增
- 包含时间信息
- 高性能（每毫秒可生成 4096 个不同的 ID）

### Redis 分布式机器 ID 分配

使用 Redis 实现机器 ID 的分布式分配，确保在分布式环境下每个节点获得唯一的机器 ID。主要特点：

- 自动分配机器 ID
- 支持节点动态扩展
- 保证机器 ID 唯一性

## 注意事项

- 确保系统时钟的准确性，避免时钟回拨
- Redis 连接配置正确，确保可用性
- 合理配置机器 ID 的分配范围

## 示例项目

可以参考 [id-generator-spring-redis-demo](../id-generator-spring-redis-demo) 项目，了解具体的使用方式和配置示例。
