# MIST_ID 算法

## 算法简介

MIST_ID（薄雾算法）是一种高性能的分布式 ID 生成方案，通过结合自增数和随机因子来生成唯一标识符。该算法设计简洁，易于实现，同时提供了良好的性能和可扩展性。

## ID 结构

### 标准实现

MIST_ID 的标准实现采用 64 位长整型，结构如下：

```
[自增数部分][随机因子A][随机因子B]
```

- 自增数部分：占据高位，通过 AtomicLong 实现原子递增
- 随机因子 A：8 位，取值范围 0-255
- 随机因子 B：8 位，取值范围 0-255

通过位运算将这三部分组合在一起，形成最终的 ID。

### 分布式实现

MIST_ID 的分布式实现基于 Redis 的 RAtomicLong，结构如下：

```
[自增数部分][随机数部分]
```

- 自增数部分：通过 Redis 的 RAtomicLong 实现分布式递增
- 随机数部分：16 位，通过随机数生成器生成

## 算法特点

### 优点

1. **高性能**：标准实现使用 AtomicLong 和 ThreadLocalRandom，分布式实现采用预填充队列机制，都能提供极高的性能
2. **简单易用**：算法实现简单，易于理解和维护
3. **分布式友好**：提供基于 Redis 的分布式实现，支持多节点部署
4. **ID 唯一性**：通过自增数和随机因子的组合，保证 ID 的唯一性
5. **安全性选项**：分布式实现支持使用 SecureRandom 提高随机性安全性

### 缺点

1. **依赖性**：分布式实现依赖 Redis 服务
2. **时间特性**：不包含时间信息, 但支持排序
3. **ID 长度**：生成的 ID 为纯数字，可能较长

## 适用场景

- 需要高性能 ID 生成的分布式系统
- 对 ID 生成速度有较高要求的应用
- 不需要 ID 包含时间信息的场景
- 需要在分布式环境下保证 ID 唯一性的系统

## 代码实现


### 分布式实现

```java
package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MistIdGenerator implements IdGenerator {

   private final Random random;
   private final RIdGenerator generator;

   public MistIdGenerator(RedissonClient redisson, @Value("${id.generator.mist.name:IdGenerator:MistIdGenerator:current}") String name, @Value("${id.generator.mist.value:-1}") long initialValue, @Value("${id.generator.mist.secret:false}") boolean useSecureRandom, @Value("${id.generator.mist.bufferSize:1000}") int bufferSize) {
      this.random = useSecureRandom ? new SecureRandom() : ThreadLocalRandom.current();
      this.generator = redisson.getIdGenerator(name);
      this.generator.tryInit(initialValue, bufferSize);
   }


   @Override
   public MistId generate() {
      return new MistId(generator.nextId(), random.nextInt(0, 65535));
   }

   @Override
   public IdType idType() {
      return IdType.MIST_ID;
   }

}
```

## 使用示例


### 分布式实现

```java
// 注入分布式实现
@Resource
private MistIdGenerator mistIdGenerator;

// 生成ID
Long id = mistIdGenerator.generate().toLong();
System.out.println("生成的分布式ID: " + id);
```

## 性能考虑

**分布式实现**：
   - 使用预填充队列机制，减少 Redis 访问频率
   - 当队列容量低于阈值时自动填充，保证 ID 生成的高效性
   - 支持配置缓冲区大小，可根据实际需求调整
   - 提供安全随机选项，可在安全性和性能之间进行权衡

## 配置参数（分布式实现）

| 参数名                       | 默认值                                       | 说明         |
| ---------------------------- |-------------------------------------------| ------------ |
| id.generator.mist.name       | IdGenerator:MistIdGenerator:current | Redis 中存储的键名 |
| id.generator.mist.value      | 0                                         | 初始值 |
| id.generator.mist.secret     | false                                     | 是否使用安全随机数 |
| id.generator.mist.bufferSize | 1000                                      | 缓冲区大小   |
