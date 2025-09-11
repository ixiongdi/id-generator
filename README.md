# Java ID Generator

<div align="center">

[![Maven Central](https://img.shields.io/maven-central/v/uno.xifan/id-generator-core.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:uno.xifan%20AND%20a:id-generator-core)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build](https://github.com/ixiongdi/id-generator/actions/workflows/ci.yml/badge.svg)](https://github.com/ixiongdi/id-generator/actions/workflows/ci.yml)
[![CodeQL](https://github.com/ixiongdi/id-generator/actions/workflows/codeql.yml/badge.svg)](https://github.com/ixiongdi/id-generator/actions/workflows/codeql.yml)
[![Codecov](https://codecov.io/gh/ixiongdi/id-generator/branch/master/graph/badge.svg)](https://codecov.io/gh/ixiongdi/id-generator)
[![Java Version](https://img.shields.io/badge/Java-8%2B%20(core)%20%7C%2017%2B%20(spring)-blue.svg)](https://www.oracle.com/java/technologies/)
[![GitHub stars](https://img.shields.io/github/stars/ixiongdi/id-generator.svg?style=social&label=Star)](https://github.com/ixiongdi/id-generator/stargazers)
[![GitHub forks](https://img.shields.io/github/forks/ixiongdi/id-generator.svg?style=social&label=Fork)](https://github.com/ixiongdi/id-generator/network/members)

</div>

> 🚀 **一个高性能、功能丰富的分布式ID生成器库**
> 
> 本项目汇集了互联网上绝大部分的ID生成算法，加上一些自己原创的算法，旨在提供简单、易用、全面的ID生成解决方案。

## ✨ 特性

- 🔥 **纯Java实现** - 核心模块仅依赖JDK 8+，无任何第三方依赖
- 🌐 **分布式支持** - 提供基于Redis的分布式ID生成方案
- 🎯 **38种算法** - 涵盖Snowflake、UUID系列、自定义算法等
- ⚡ **高性能** - 经过JMH基准测试优化，支持高并发场景
- 🔌 **多框架集成** - 支持Spring、Vert.x、Solon等主流框架
- 📦 **模块化设计** - 按需引入，减少依赖冲突
- 🛡️ **类型安全** - 完善的类型定义和异常处理
- 📚 **文档完善** - 详细的使用文档和示例代码

## 📚 目录

- [特性](#-特性)
- [模块矩阵](#-模块矩阵)
- [快速开始](#-快速开始)
  - [无协调版本](#无协调版本-core)
  - [分布式版本](#分布式版本-spring-redis)
- [ID算法介绍](#id算法介绍)
- [性能基准](#-性能基准)
- [贡献指南](#-贡献指南)
- [许可证](#-许可证)
- [联系方式](#-联系方式)

## 📦 模块矩阵

| 模块 | 描述 | JDK版本 | 依赖 |
|--------|------|----------|------|
| `id-generator-core` | ⭐ 纯Java核心算法，38种ID生成策略 | 8+ | 无 |
| `id-generator-spring-redis` | 🌐 分布式ID生成（Redisson） | 17+ | Spring Boot 3 |
| `id-generator-web` | 🌍 示例Web应用，演示各种算法 | 17+ | Spring Boot 3 |
| `id-generator-service-*` | 🔗 多框架接入（Spring MVC/WebFlux、Vert.x、Solon、Feat） | 17+ | 对应框架 |
| `id-generator-benchmark` | ⚡ JMH基准测试，性能对比分析 | 17+ | JMH |
| `id-generator-bom`/`id-generator-dependencies` | 📦 BOM与依赖版本管理 | - | - |

## 🚀 快速开始

### 无协调版本 (Core)

不依赖任何第三方组件的ID生成算法，适合单机部署和轻量级应用。

**添加Maven依赖：**
```xml
 <dependency>
    <groupId>uno.xifan</groupId>
    <artifactId>id-generator-core</artifactId>
    <version>0.8.0</version>
</dependency>
```

**代码示例：**

```java
package uno.xifan.id;

import uno.xifan.id.generator.custom.TimeBasedEntropyIdGenerator;
import uno.xifan.id.generator.uuid.UUIDv7Generator;
import uno.xifan.id.util.IdUtil;

public class Main {

    public static void main(String[] args) {
        // 原创算法（基于时间戳的多熵源ID生成）
        TimeBasedEntropyIdGenerator timeBasedEntropyIdGenerator = new TimeBasedEntropyIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(timeBasedEntropyIdGenerator.generate());
        }
        // UUID v7（基于时间戳和随机数的ID生成）
        UUIDv7Generator uuiDv7Generator = new UUIDv7Generator();
        for (int i = 0; i < 10; i++) {
            System.out.println(uuiDv7Generator.generate());
        }

        // 有一些ID生成直接封装在了IdUtil里，可以这样使用
        for (int i = 0; i < 10; i++) {
            System.out.println(IdUtil.entropy());
        }
        for (int i = 0; i < 10; i++) {
            System.out.println(IdUtil.uuid7());
        }
    }
}

```

输出：
```text
3222468131246802
3222467286060751
3222468347649837
3222467291790908
3222468029585554
3222467216329747
3222466768071402
3222468509150539
3222468637611075
3222467796534475
0196b425-a94b-7000-aacc-caa1d49a210f
0196b425-a94c-7000-be40-66f951a9012f
0196b425-a94c-7001-8282-399074543e76
0196b425-a94c-7002-90f8-0f55c90d8368
0196b425-a94c-7003-8406-96214faca186
0196b425-a94c-7004-ad60-4bebba94e498
0196b425-a94c-7005-9caf-bbe80f017786
0196b425-a94c-7006-81e2-319b9cd05903
0196b425-a94c-7007-b20a-26734d9ae89f
0196b425-a94c-7008-a976-43bb6d906a6b
```

想要了解`TimeBasedEntropyIdGenerator`算法的更多，请参考[TimeBasedEntropyIdGenerator](./docs/ID生成算法介绍/26-TimeBasedEntropyId.md)

想要了解`UUIDv7Generator`算法的更多，请参考[UUIDv7Generator](./docs/ID生成算法介绍/35-UUIDv7.md)

### 分布式版本 (Spring Redis)

适合分布式系统的ID生成算法，依赖Java 17+、Spring Boot 3.4.5+和Redisson 3.46.0+。

**添加Maven依赖：**
```xml
 <dependency>
    <groupId>uno.xifan</groupId>
    <artifactId>id-generator-spring-redis</artifactId>
    <version>0.8.0</version>
</dependency>
```

```java
package uno.xifan.id.generator.demo;

import uno.xifan.id.generator.distributed.mist.MistIdGenerator;
import uno.xifan.id.generator.distributed.uuid.UUIDv8Generator;
import jakarta.annotation.Resource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

/**
 * ID生成器示例应用
 */
@SpringBootApplication
//@EnableScheduling
@ComponentScans(value = {@ComponentScan("uno.xifan.id.generator"),})
@MapperScan("uno.xifan.id.generator.demo.mapper")
public class IdGeneratorDemoApplication implements CommandLineRunner {
    @Resource
    private MistIdGenerator mistIdGenerator;
    @Resource
    private UUIDv8Generator uuiDv8Generator;

    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 薄雾算法（基于原子自增和随机数的ID生成算法）
        for (int i = 0; i < 10; i++) {
            System.out.println(mistIdGenerator.generate().toLong());
        }
        // UUIDv8（基于时间戳、循环计数器、节点ID的生成算法，符合UUID最新标准）
        for (int i = 0; i < 10; i++) {
            System.out.println(uuiDv8Generator.generate().toUUID());
        }
    }
}

```
输出
```text
7965496382
7965506879
7965493701
7965491581
7965493715
7965502035
7965504511
7965506549
7965506767
7965492591
26c94be6-f1da-8dae-8000-bd28f61a66b1
26c94be6-f1f5-87e4-8001-bd28f61a66b1
26c94be6-f1f5-87e4-8002-bd28f61a66b1
26c94be6-f1f5-87e4-8003-bd28f61a66b1
26c94be6-f1f5-87e4-8004-bd28f61a66b1
26c94be6-f1f5-87e4-8005-bd28f61a66b1
26c94be6-f1f5-87e4-8006-bd28f61a66b1
26c94be6-f1f5-87e4-8007-bd28f61a66b1
26c94be6-f1f5-87e4-8008-bd28f61a66b1
26c94be6-f1f5-87e4-8009-bd28f61a66b1
```

想要了解`MistIdGenerator`算法的更多，请参考[MistIdGenerator](./docs/ID生成算法介绍/14-MIST_ID.md)

想要了解`UUIDv8Generator`算法的更多，请参考[UUIDv8Generator](./docs/ID生成算法介绍/36-UUIDv8.md)

## 📊 ID算法介绍

本项目提供了38种不同的ID生成算法，每种算法都有其特定的优势和适用场景：

### ✨ 推荐算法

- **TimeBasedEntropyId** - 原创算法，基于时间戳的多熵源ID生成
- **UUIDv7** - 基于时间戳和随机数的最新UUID标准
- **UUIDv8** - 自定义UUID格式，支持分布式环境
- **Snowflake** - 经典分布式ID算法
- **MistId** - 薄雾算法，基于原子自增和随机数

### 🔍 全部算法列表

<details>
<summary>点击查看全部算法 (共38种)</summary>

| 类别 | 算法名称 | 描述 | 特点 |
|------|----------|------|------|
| **UUID系列** | UUIDv1 | 基于时间和MAC地址 | 唯一性强 |
| | UUIDv4 | 基于随机数 | 简单实用 |
| | UUIDv7 | 基于时间戳 | 可排序 |
| | UUIDv8 | 自定义格式 | 灵活性高 |
| **Snowflake系列** | Snowflake | 经典雪花算法 | 高性能 |
| | SonyFlake | Sony版本 | 低重复率 |
| | ElasticFlake | 弹性雪花 | 自适应 |
| **自定义系列** | TimeBasedEntropyId | 原创算法 | 多熵源 |
| | MistId | 薄雾算法 | 高并发 |
| **其他** | ... | ... | ... |

</details>

详细的算法介绍请查看文档：[ID生成算法介绍](./docs/ID生成算法介绍/)

## ⚡ 性能基准

我们使用JMH进行了全面的性能测试，以下是在典型环境下的性能表现：

### 测试环境
- **CPU**: Intel i7-12700K
- **内存**: 32GB DDR4
- **JDK**: OpenJDK 17
- **测试方式**: JMH Throughput mode

### 主要算法性能对比

| 算法 | QPS (ops/sec) | 平均延迟 (ns) | 内存占用 |
|------|---------------|-----------------|----------|
| TimeBasedEntropyId | ~12M | ~85 | 极低 |
| UUIDv7 | ~8M | ~125 | 低 |
| Snowflake | ~15M | ~67 | 极低 |
| UUIDv4 | ~5M | ~200 | 低 |

> 📈 运行基准测试：`mvn exec:java -pl id-generator-benchmark`

## 🔗 集成示例

我们提供了多个框架的集成示例：

- **Spring Boot** - [id-generator-web](./id-generator-web/)
- **Spring WebFlux** - [id-generator-service-spring-webflux](./id-generator-service-spring-webflux/)
- **Vert.x** - [id-generator-service-vertx](./id-generator-service-vertx/)
- **Solon** - [id-generator-service-solon](./id-generator-service-solon/)

## 📝 文档导航

- [🚀 快速开始](./docs/quick-start.md)
- [📊 算法指南](./docs/algorithms-guide.md)
- [⚡ 性能优化](./docs/performance-tuning.md)
- [🔌 框架集成](./docs/framework-integration.md)
- [❓ 常见问题](./docs/faq.md)

## 🐛 报告问题

如果您遇到任何问题或有改进建议，请通过以下方式反馈：

- 🐛 **Bug报告**: [点击创建](https://github.com/ixiongdi/id-generator/issues/new?template=bug_report.yml)
- ✨ **功能请求**: [点击创建](https://github.com/ixiongdi/id-generator/issues/new?template=feature_request.yml)
- ⚡ **性能问题**: [点击创建](https://github.com/ixiongdi/id-generator/issues/new?template=performance_issue.yml)
- ❓ **使用问题**: [点击创建](https://github.com/ixiongdi/id-generator/issues/new?template=question.yml)

## 🤝 贡献指南

欢迎所有形式的贡献！请阅读我们的[贡献指南](CONTRIBUTING.md)了解如何参与项目开发。

### 开发者

感谢以下贡献者对本项目的支持：

<!-- 自动生成的贡献者列表 -->
<a href="https://github.com/ixiongdi/id-generator/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=ixiongdi/id-generator" />
</a>

## 💼 商业支持

如果您需要专业的商业支持或定制服务，请联系：

- 📧 **邮箱**: ixiongdi@gmail.com
- 🌐 **网站**: [https://xifan.uno](https://xifan.uno)

## ⭐ Star History

如果这个项目对您有帮助，请给我们一个 Star ⭐！

[![Star History Chart](https://api.star-history.com/svg?repos=ixiongdi/id-generator&type=Date)](https://star-history.com/#ixiongdi/id-generator&Date)

## 📜 许可证

本项目采用 [MIT](LICENSE) 许可证。

## 📞 联系方式

- **作者**: Andy Xiong (熊迪)
- **邮箱**: ixiongdi@gmail.com
- **GitHub**: [@ixiongdi](https://github.com/ixiongdi)
- **组织**: 稀饭科技

---

<div align="center">

**❤️ 感谢您的关注和支持！**

如果这个项目对您有帮助，请考虑给我们一个 [⭐ Star](https://github.com/ixiongdi/id-generator/stargazers) 或 [💙 赞助](https://github.com/sponsors/ixiongdi)

</div>
