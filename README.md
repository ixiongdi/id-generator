# Java ID Generator

[![Maven Central](https://img.shields.io/maven-central/v/icu.congee/id-generator-core.svg)](https://search.maven.org/search?q=g:icu.congee%20AND%20a:id-generator-core)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue.svg)](https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)

本项目汇集了互联网上绝大部分的ID生成算法加上一些自己原创的算法。旨在提供简单、易用、全面的ID生成解决方案

## 特性

- 本项目的无协调版本，也就是core包里的ID生成器，仅依赖Java8+，无其他任何依赖
- 本项目的分布式版本，也就是spring-redis里的ID生成器，依赖Java21+、Spring 3、Redisson
- 提供多达38种ID生成算法，包含Snowflake和UUID系列

## 快速开始

不依赖任何第三方组件的ID生成算法

添加Maven依赖
```xml
 <dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-core</artifactId>
    <version>0.6.0</version>
</dependency>
```

```java
package icu.congee.id;

import icu.congee.id.generator.custom.TimeBasedEntropyIdGenerator;
import icu.congee.id.generator.uuid.UUIDv7Generator;

public class Main {

    public static void main(String[] args) {
        // 原创算法（基于时间戳的多熵源ID生成）
        TimeBasedEntropyIdGenerator timeBasedEntropyIdGenerator = new TimeBasedEntropyIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(timeBasedEntropyIdGenerator.generate());
        }
        // UUIDv7（基于时间戳和随机数的ID生成）
        UUIDv7Generator uuiDv7Generator = new UUIDv7Generator();
        for (int i = 0; i < 10; i++) {
            System.out.println(uuiDv7Generator.generate());
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

分布式的ID生成算法，依赖Java 21+、Spring Boot 3.4.5+ 和Redisson 3.46.0+

添加Maven依赖
```xml
 <dependency>
    <groupId>icu.congee</groupId>
    <artifactId>id-generator-spring-redis</artifactId>
    <version>0.6.0</version>
</dependency>
```

```java
package icu.congee.id.generator.demo;

import icu.congee.id.generator.distributed.mist.MistIdGenerator;
import icu.congee.id.generator.distributed.uuid.UUIDv8Generator;
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
@ComponentScans(value = {@ComponentScan("icu.congee.id.generator"),})
@MapperScan("icu.congee.id.generator.demo.mapper")
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
            System.out.println(mistIdGenerator.generate());
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