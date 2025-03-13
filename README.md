# Java ID Generator (BroId)

[![Maven Central](https://img.shields.io/maven-central/v/icu.congee/id-generator-core.svg)](https://search.maven.org/search?q=g:icu.congee%20AND%20a:id-generator-core)
[![License](https://img.shields.io/badge/license-MIT%20AND%20Apache--2.0-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

旨在提供简单、全面、高性能、基于最佳实践的本机和分布式 ID 生成器。

本项目汇集了全网几乎所有的Id生成算法，并提供了标准实现、严格实现、快速实现，以及各种算法可自定义部分的实现。

## 特性

- **零依赖**：不依赖任何第三方库，仅需要Java 8+即可
- **高性能**：全部ID生成器都有高性能版本，满足高并发要求
- **多版本**：中心化部署为可选项
- **全面覆盖**：支持16种不同的ID生成算法
- **最佳实践**：基于互联网上流行的ID生成方案，结合实战经验总结
- **符合RFC标准**：实现了最新的UUID标准(RFC 9562)

## ID生成器特性对比

| ID          | 唯一性 | 单调性 | 去中心化 | 长度 | 可靠性 | 性能 | 不可猜测性 | 业务含义 | 易用性 | 可使用年限 | 隐私性 | 随机性 |
|-------------|---------|---------|-----------|--------|---------|--------|-------------|-----------|---------|------------|---------|----------|
| UUIDv1      | ★★★☆☆    | ★★★☆☆    | ★★★★★      | ★★★☆☆   | ★★★★☆    | ★★★★★   | ★★☆☆☆      | ★☆☆☆☆    | ★★★★☆  | ★★★★★     | ★☆☆☆☆  | ★★☆☆☆   |
| UUIDv2      | ★★★☆☆    | ★★★☆☆    | ★★★★★      | ★★★☆☆   | ★★★★☆    | ★★★★★   | ★★☆☆☆      | ★★☆☆☆    | ★★★★☆  | ★★★★★     | ★☆☆☆☆  | ★★☆☆☆   |
| UUIDv3      | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★☆☆      | ★★★☆☆    | ★★★★★  | ★★★★★     | ★★★☆☆  | ★★★☆☆   |
| UUIDv4      | ★★★★★    | ★☆☆☆☆    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★★★      | ★☆☆☆☆    | ★★★★★  | ★★★★★     | ★★★★★  | ★★★★★   |
| UUIDv5      | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★☆☆      | ★★★☆☆    | ★★★★★  | ★★★★★     | ★★★☆☆  | ★★★☆☆   |
| UUIDv6      | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★☆☆      | ★★☆☆☆    | ★★★★☆  | ★★★★★     | ★★☆☆☆  | ★★★☆☆   |
| UUIDv7      | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★★☆      | ★★☆☆☆    | ★★★★★  | ★★★★★     | ★★★★☆  | ★★★★☆   |
| ULID        | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★★☆      | ★★☆☆☆    | ★★★★★  | ★★★★★     | ★★★★☆  | ★★★★☆   |
| SnowflakeID | ★★★★★    | ★★★★★    | ★★★☆☆      | ★★★★★   | ★★★★☆    | ★★★★★   | ★★★☆☆      | ★★★★☆    | ★★★☆☆  | ★★★★☆     | ★★★☆☆  | ★★★☆☆   |
| CosId       | ★★★★★    | ★★★★★    | ★★★★★      | ★★★☆☆   | ★★★★★    | ★★★★★   | ★★★★☆      | ★★★★☆    | ★★★★★  | ★★★★★     | ★★★★☆  | ★★★★☆   |

## 快速开始

### Maven

```xml
<dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-core</artifactId>
    <version>0.1.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'icu.congee:id-generator-core:0.1.0'
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

## 支持的ID生成器

本项目支持以下ID生成算法：

### UUID系列
- **UUIDv1** - 基于时间和MAC地址的UUID
- **UUIDv2** - DCE安全UUID
- **UUIDv3** - 基于名字和MD5的UUID
- **UUIDv4** - 随机UUID
- **UUIDv5** - 基于名字和SHA-1的UUID
- **UUIDv6** - 基于时间的UUID（改进版UUIDv1）
- **UUIDv7** - 基于Unix时间戳的UUID（最新RFC标准）
- **UUIDv8** - 自定义UUID

### 其他ID生成器
- **Snowflake** - Twitter的雪花算法
- **ULID** - 可排序的UUID
- **ObjectId** - MongoDB的ObjectId
- **Sonyflake** - Sony的分布式ID生成器
- **CombGuid** - 结合GUID和时间戳的ID
- **Xid** - 全局唯一ID生成器
- **CosId** - 腾讯云的分布式ID生成器
- **Cuid2** - 安全、可排序的ID
- **FlakeId** - 分布式ID生成器

## 高级用法

### 自定义UUID v7生成器

```java
// 使用增强时钟精度的UUID v7生成器
UUIDv7Generator precisionGenerator = new IncreasedClockPrecisionUUIDv7Generator();
UUID uuid = precisionGenerator.generate();

// 使用单调随机的UUID v7生成器
UUIDv7Generator monotonicGenerator = new MonotonicRandomUUIDv7Generator();
UUID uuid = monotonicGenerator.generate();
```

### 分布式ID生成

```java
// 使用Redis作为中央节点的分布式ID生成器
// 需要添加id-generator-spring-redis依赖
RedisSnowflakeIdGenerator redisSnowflake = new RedisSnowflakeIdGenerator(redisTemplate);
long id = redisSnowflake.nextId();
```

## 性能基准测试

本项目包含了各种ID生成器的性能基准测试，可以通过以下方式运行：

```bash
mvn clean package -DskipTests
java -jar id-generator-benchmark/target/id-generator-benchmark.jar
```

## 项目结构

- **id-generator-core** - 核心ID生成器实现
- **id-generator-spring-redis** - 基于Spring Redis的分布式ID生成器
- **id-generator-spring-redis-demo** - Spring Redis分布式ID生成器示例
- **id-generator-benchmark** - 性能基准测试
- **id-generator-web** - Web API接口
- **id-generator-bom** - Bill of Materials
- **id-generator-dependencies** - 依赖管理

## 贡献指南

欢迎贡献代码、报告问题或提出改进建议。请遵循以下步骤：

1. Fork本仓库
2. 创建您的特性分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add some amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 打开一个Pull Request

## 许可证

本项目采用双重许可：

- [MIT License](LICENSE)
- [Apache License 2.0](LICENSE-APACHE)

## 联系方式

- 作者：熊迪
- 邮箱：ixiongdi@gmail.com
- GitHub：[https://github.com/ixiongdi](https://github.com/ixiongdi)

## 致谢

感谢所有为各种ID生成算法做出贡献的开发者和组织。