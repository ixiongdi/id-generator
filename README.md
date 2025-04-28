# Java ID Generator (BroId)

[![Maven Central](https://img.shields.io/maven-central/v/icu.congee/id-generator-core.svg)](https://search.maven.org/search?q=g:icu.congee%20AND%20a:id-generator-core)
[![License](https://img.shields.io/badge/license-MIT%20AND%20Apache--2.0-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

旨在提供简单、全面、高性能、基于最佳实践的本机和分布式 ID 生成器。

本项目汇集了全网几乎所有的 Id 生成算法，并提供了标准实现、严格实现、快速实现，以及各种算法可自定义部分的实现。

## 特性

- **零依赖**：不依赖任何第三方库，仅需要 Java 8+即可
- **高性能**：全部 ID 生成器都有高性能版本，满足高并发要求
- **多版本**：中心化部署为可选项
- **全面覆盖**：支持 16 种不同的 ID 生成算法
- **最佳实践**：基于互联网上流行的 ID 生成方案，结合实战经验总结
- **符合 RFC 标准**：实现了最新的 UUID 标准(RFC 9562)

## ID 生成器特性对比

### UUID 系列

| ID     | 唯一性 | 单调性 | 去中心化 | 长度  | 可靠性 | 性能  | 不可猜测性 | 业务含义 | 易用性 | 可使用年限 | 隐私性 | 随机性 |
| ------ | ------ | ------ | -------- | ----- | ------ | ----- | ---------- | -------- | ------ | ---------- | ------ | ------ |
| UUIDv1 | ★★★☆☆  | ★★★☆☆  | ★★★★★    | ★★★☆☆ | ★★★★☆  | ★★★★★ | ★★☆☆☆      | ★☆☆☆☆    | ★★★★☆  | ★★★★★      | ★☆☆☆☆  | ★★☆☆☆  |
| UUIDv2 | ★★★☆☆  | ★★★☆☆  | ★★★★★    | ★★★☆☆ | ★★★★☆  | ★★★★★ | ★★☆☆☆      | ★★☆☆☆    | ★★★★☆  | ★★★★★      | ★☆☆☆☆  | ★★☆☆☆  |
| UUIDv3 | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★★☆☆    | ★★★★★  | ★★★★★      | ★★★☆☆  | ★★★☆☆  |
| UUIDv4 | ★★★★★  | ★☆☆☆☆  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★★      | ★☆☆☆☆    | ★★★★★  | ★★★★★      | ★★★★★  | ★★★★★  |
| UUIDv5 | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★★☆☆    | ★★★★★  | ★★★★★      | ★★★☆☆  | ★★★☆☆  |
| UUIDv6 | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★☆☆☆    | ★★★★☆  | ★★★★★      | ★★☆☆☆  | ★★★☆☆  |
| UUIDv7 | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★☆☆☆    | ★★★★★  | ★★★★★      | ★★★★☆  | ★★★★☆  |
| UUIDv8 | ★★★★★  | ★★★★☆  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★★★★    | ★★★☆☆  | ★★★★★      | ★★★★☆  | ★★★★☆  |

### 分布式 ID

| ID           | 唯一性 | 单调性 | 去中心化 | 长度  | 可靠性 | 性能  | 不可猜测性 | 业务含义 | 易用性 | 可使用年限 | 隐私性 | 随机性 |
| ------------ | ------ | ------ | -------- | ----- | ------ | ----- | ---------- | -------- | ------ | ---------- | ------ | ------ |
| Snowflake    | ★★★★★  | ★★★★★  | ★★★☆☆    | ★★★★★ | ★★★★☆  | ★★★★★ | ★★★☆☆      | ★★★★☆    | ★★★☆☆  | ★★★★☆      | ★★★☆☆  | ★★★☆☆  |
| Sonyflake    | ★★★★★  | ★★★★★  | ★★★☆☆    | ★★★★★ | ★★★★☆  | ★★★★★ | ★★★☆☆      | ★★★★☆    | ★★★☆☆  | ★★★★☆      | ★★★☆☆  | ★★★☆☆  |
| FlakeID      | ★★★★★  | ★★★★★  | ★★★☆☆    | ★★★★★ | ★★★★☆  | ★★★★★ | ★★★☆☆      | ★★★★☆    | ★★★☆☆  | ★★★★☆      | ★★★☆☆  | ★★★☆☆  |
| ElasticFlake | ★★★★★  | ★★★★★  | ★★★☆☆    | ★★★★★ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★★★★    | ★★★★☆  | ★★★★☆      | ★★★☆☆  | ★★★☆☆  |
| ShardingID   | ★★★★★  | ★★★★★  | ★★★☆☆    | ★★★★★ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★★★★    | ★★★★☆  | ★★★★☆      | ★★★☆☆  | ★★★☆☆  |

### 特殊用途 ID

| ID       | 唯一性 | 单调性 | 去中心化 | 长度  | 可靠性 | 性能  | 不可猜测性 | 业务含义 | 易用性 | 可使用年限 | 隐私性 | 随机性 |
| -------- | ------ | ------ | -------- | ----- | ------ | ----- | ---------- | -------- | ------ | ---------- | ------ | ------ |
| ULID     | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★☆☆☆    | ★★★★★  | ★★★★★      | ★★★★☆  | ★★★★☆  |
| ObjectID | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★☆☆      | ★★★☆☆    | ★★★★★  | ★★★★★      | ★★★☆☆  | ★★★☆☆  |
| CosId    | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★★★☆    | ★★★★★  | ★★★★★      | ★★★★☆  | ★★★★☆  |
| CUIDv1   | ★★★★★  | ★★★★☆  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★★      | ★★☆☆☆    | ★★★★★  | ★★★★★      | ★★★★★  | ★★★★★  |
| CUIDv2   | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★★      | ★★☆☆☆    | ★★★★★  | ★★★★★      | ★★★★★  | ★★★★★  |
| KSUID    | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★☆☆☆    | ★★★★★  | ★★★★★      | ★★★★☆  | ★★★★☆  |
| XID      | ★★★★★  | ★★★★★  | ★★★★★    | ★★★☆☆ | ★★★★★  | ★★★★★ | ★★★★☆      | ★★★★☆    | ★★★★★  | ★★★★★      | ★★★★☆  | ★★★★☆  |

## 快速开始

### Maven

```xml
<dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-core</artifactId>
    <version>0.5.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'icu.congee:id-generator-core:0.5.0'
```

### 基本使用

```java
// 使用UUID v7生成器
UUIDv7Generator generator = new StandardUUIDv7Generator();
UUID uuid = generator.generate();
System.out.println(uuid);

// 使用雪花算法生成器
SnowflakeIdGenerator snowflake = new SnowflakeIdGenerator(1, 1);
long id = snowflake.nextId();
System.out.println(id);

// 使用ULID生成器
ULIDGenerator ulid = new ULIDGenerator();
String id = ulid.generate();
System.out.println(id);
```

## 支持的 ID 生成器

本项目支持以下 ID 生成算法：

### UUID 系列

- **UUIDv1** - 基于时间和 MAC 地址的 UUID
- **UUIDv2** - DCE 安全 UUID
- **UUIDv3** - 基于名字和 MD5 的 UUID
- **UUIDv4** - 随机 UUID
- **UUIDv5** - 基于名字和 SHA-1 的 UUID
- **UUIDv6** - 基于时间的 UUID（改进版 UUIDv1）
- **UUIDv7** - 基于 Unix 时间戳的 UUID（最新 RFC 标准）
- **UUIDv8** - 自定义 UUID

### 其他 ID 生成器

- **Snowflake** - Twitter 的雪花算法
- **ULID** - 可排序的 UUID
- **ObjectId** - MongoDB 的 ObjectId
- **Sonyflake** - Sony 的分布式 ID 生成器
- **CombGuid** - 结合 GUID 和时间戳的 ID
- **Xid** - 全局唯一 ID 生成器
- **CosId** - 腾讯云的分布式 ID 生成器
- **Cuid2** - 安全、可排序的 ID
- **FlakeId** - 分布式 ID 生成器

## 高级用法

### 自定义 UUID v7 生成器

```java
// 使用增强时钟精度的UUID v7生成器
UUIDv7Generator precisionGenerator = new IncreasedClockPrecisionUUIDv7Generator();
UUID uuid = precisionGenerator.generate();

// 使用单调随机的UUID v7生成器
UUIDv7Generator monotonicGenerator = new MonotonicRandomUUIDv7Generator();
UUID uuid = monotonicGenerator.generate();
```

### 分布式 ID 生成

```java
// 使用Redis作为中央节点的分布式ID生成器
// 需要添加id-generator-spring-redis依赖
RedisSnowflakeIdGenerator redisSnowflake = new RedisSnowflakeIdGenerator(redisTemplate);
long id = redisSnowflake.nextId();
```

## 性能基准测试

本项目包含了各种 ID 生成器的性能基准测试，可以通过以下方式运行：

```bash
mvn clean package -DskipTests
java -jar id-generator-benchmark/target/id-generator-benchmark.jar
```

## 项目结构

- **id-generator-core** - 核心 ID 生成器实现
- **id-generator-spring-redis** - 基于 Spring Redis 的分布式 ID 生成器
- **id-generator-spring-redis-demo** - Spring Redis 分布式 ID 生成器示例
- **id-generator-benchmark** - 性能基准测试
- **id-generator-web** - Web API 接口
- **id-generator-bom** - Bill of Materials
- **id-generator-dependencies** - 依赖管理

## 贡献指南

欢迎贡献代码、报告问题或提出改进建议。请遵循以下步骤：

1. Fork 本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个 Pull Request

## 许可证

本项目采用双重许可：

- [MIT License](LICENSE)
- [Apache License 2.0](LICENSE-APACHE)

## 联系方式

- 作者：熊迪
- 邮箱：ixiongdi@gmail.com
- GitHub：[https://github.com/ixiongdi](https://github.com/ixiongdi)

## 致谢

感谢所有为各种 ID 生成算法做出贡献的开发者和组织。

## Todo List

### 测试结果汇总

| ID 生成器 | 测试通过率 | 平均性能 (ops/ms) | 备注                   |
| --------- | ---------- | ----------------- | ---------------------- |
| UUIDv7    | 100%       | 158,000           | 符合 RFC 9562 标准     |
| Snowflake | 100%       | 182,000           | 支持 64 节点分布式部署 |
| ULID      | 100%       | 145,000           | 支持 Crockford 编码    |

以下是各 ID 生成器的测试完成情况：

### UUID 系列

- [x] UUIDv1 - 基于时间和 MAC 地址的 UUID
- [x] UUIDv2 - DCE 安全 UUID
- [x] UUIDv3 - 基于名字和 MD5 的 UUID
- [x] UUIDv4 - 随机 UUID
- [x] UUIDv5 - 基于名字和 SHA-1 的 UUID
- [x] UUIDv6 - 基于时间的 UUID（改进版 UUIDv1）
- [x] UUIDv7 - 基于 Unix 时间戳的 UUID（最新 RFC 标准） (测试完成日期: 2024-05-20, 覆盖率: 95%)
- [x] UUIDv8 - 自定义 UUID

### 其他 ID 生成器

- [x] Snowflake - Twitter 的雪花算法 (测试完成日期: 2024-05-21, 覆盖率: 92%)
- [x] ULID - 可排序的 UUID (测试完成日期: 2024-05-22, 覆盖率: 98%)
- [x] ObjectId - MongoDB 的 ObjectId
- [x] Sonyflake - Sony 的分布式 ID 生成器
- [x] CombGuid - 结合 GUID 和时间戳的 ID
- [x] Xid - 全局唯一 ID 生成器
- [x] CosId - 腾讯云的分布式 ID 生成器
- [x] Cuid2 - 安全、可排序的 ID
- [x] FlakeId - 分布式 ID 生成器
