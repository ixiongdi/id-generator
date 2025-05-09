# ID Generator Core

## 简介

ID Generator Core 是一个高性能、可扩展的分布式 ID 生成器库，提供多种 ID 生成策略，满足不同场景下的唯一标识需求。本库支持雪花算法(Snowflake)、分片 ID(ShardingID)、CUID、UUID 等多种 ID 生成方案，可用于分布式系统、微服务架构中的全局唯一 ID 生成。

## 特性

- **多种 ID 生成策略**：支持 20+种 ID 生成算法，满足不同业务场景需求
- **高性能**：优化的实现确保高吞吐量和低延迟
- **分布式友好**：适用于分布式系统和集群环境
- **可定制化**：提供自定义 ID 生成器接口，支持业务定制需求
- **线程安全**：所有生成器实现均保证线程安全
- **零外部依赖**：核心模块不依赖外部组件，可独立运行

## 支持的 ID 生成器

本库支持以下 ID 生成策略：

| ID 生成器  | 描述                                | 特点                     |
| ---------- | ----------------------------------- | ------------------------ |
| Snowflake  | Twitter 开发的分布式 ID 生成算法    | 有序、高性能、时间相关   |
| ShardingID | Instagram 使用的分片 ID 生成器      | 分片友好、有序           |
| CUID       | 支持 CUIDv1 和 CUIDv2 的生成器      | 安全、唯一、URL 友好     |
| UUID       | 支持 UUIDv1-v8 多个版本             | 通用、唯一、标准化       |
| Sonyflake  | Sony 的分布式 ID 生成器             | 类雪花算法、更长使用寿命 |
| ULID       | 可排序的通用唯一标识符              | 有序、兼容 UUID          |
| KSUID      | K-Sortable 唯一标识符               | 可排序、时间相关         |
| XID        | 全局唯一 ID 生成器                  | 小巧、高性能             |
| ObjectID   | MongoDB 风格的对象 ID               | 时间相关、紧凑           |
| NanoID     | 小型、安全、URL 友好的 ID           | 安全、紧凑               |
| 自定义 ID  | 支持时间戳、随机数等组合的自定义 ID | 灵活、可定制             |

## 安装

### Maven

```xml
<dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-core</artifactId>
    <version>0.6.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'icu.congee:id-generator-core:0.6.0'
```

## 快速开始

### Snowflake ID 生成器

```java
// 创建Snowflake ID生成器
SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1, 1);

// 生成ID
long id = generator.nextId();
System.out.println("Generated Snowflake ID: " + id);
```

### CUID 生成器

```java
// 创建CUIDv2生成器
CUIDv2Generator generator = new CUIDv2Generator();

// 生成CUID
String cuid = generator.nextId();
System.out.println("Generated CUID: " + cuid);
```

### UUID 生成器

```java
// 创建UUIDv4生成器
UUIDv4Generator generator = new UUIDv4Generator();

// 生成UUID
UUID uuid = generator.nextId();
System.out.println("Generated UUID: " + uuid);
```

### 自定义 ID 生成器

```java
// 创建基于时间戳的随机ID生成器
TimeBasedRandomIdGenerator generator = new TimeBasedRandomIdGenerator();

// 生成ID
String id = generator.nextId();
System.out.println("Generated Custom ID: " + id);
```

## Snowflake ID 对比其他 ID 生成器

| 特性       | Snowflake | UUID | 数据库自增 ID | 号段模式 |
| ---------- | --------- | ---- | ------------- | -------- |
| 有序性     | ✓         | ×    | ✓             | ✓        |
| 唯一性     | ✓         | ✓    | ✓             | ✓        |
| 趋势递增   | ✓         | ×    | ✓             | ✓        |
| 分布式友好 | ✓         | ✓    | ×             | ✓        |
| 性能       | 高        | 高   | 低            | 中       |
| 依赖       | 时钟同步  | 无   | 数据库        | 数据库   |

## 许可证

本项目采用 MIT 许可证。详情请参阅 [LICENSE](../LICENSE) 文件。

## 贡献

欢迎提交问题报告和拉取请求。有关更多信息，请参阅 [CONTRIBUTING.md](../CONTRIBUTING.md)。

## 相关项目

- [id-generator-spring-redis](../id-generator-spring-redis) - Spring Boot 和 Redis 集成
- [id-generator-web](../id-generator-web) - Web API 服务

## 联系方式

如有任何问题或建议，请联系项目维护者：

- 熊迪 (ixiongdi@gmail.com)
- GitHub: [https://github.com/ixiongdi/id-generator](https://github.com/ixiongdi/id-generator)
