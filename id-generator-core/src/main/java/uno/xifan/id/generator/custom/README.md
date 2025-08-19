# 自定义 ID 生成器

本文档详细介绍了两种自定义 ID 生成器的实现原理和使用方法：基于时间戳的随机 ID 生成器（TimeBasedRandomIdGenerator）和基于时间戳的业务 ID 生成器（TimeBasedBusinessIdGenerator）。

## TimeBasedRandomIdGenerator

### 实现原理

生成 64 位长整型 ID，结构如下：

- 高 32 位：当前时间戳（秒级，相对于自定义纪元 2022-02-22 14:22:22 GMT-05:00）
- 低 32 位：随机数

### 特点

1. **高性能**：采用无锁设计，使用 ThreadLocalRandom 保证线程安全
2. **时间递增**：高 32 位使用时间戳，保证 ID 总体递增
3. **高并发**：通过随机数保证同一秒内 ID 唯一性
4. **纪元设计**：使用自定义纪元时间，优化时间戳位数利用

### 使用示例

```java
// 方式1：使用实例方法
TimeBasedRandomIdGenerator generator = new TimeBasedRandomIdGenerator();
Long id = generator.generate();

// 方式2：使用静态方法
Long id = TimeBasedRandomIdGenerator.next();
```

## TimeBasedBusinessIdGenerator

### 实现原理

生成 16 位数字格式的业务 ID，结构如下：

- 前 12 位：时间戳（格式：yyMMddHHmmss）
- 后 4 位：序列号（0-9999）

### 特点

1. **可读性**：ID 包含完整时间信息，便于业务追踪
2. **严格递增**：同一秒内通过序列号保证递增
3. **容错设计**：
   - 时间回拨处理：使用上一次的时间戳
   - 序列号溢出处理：等待进入下一秒
4. **同步保证**：使用 synchronized 和 AtomicInteger 确保线程安全

### 使用示例

```java
// 方式1：使用实例方法
TimeBasedBusinessIdGenerator generator = new TimeBasedBusinessIdGenerator();
Long id = generator.generate();

// 方式2：使用静态方法
Long id = TimeBasedBusinessIdGenerator.next();
```

## 性能对比

| 特性       | TimeBasedRandomIdGenerator | TimeBasedBusinessIdGenerator |
| ---------- | -------------------------- | ---------------------------- |
| ID 长度    | 64 位长整型                | 16 位数字字符串              |
| 并发性能   | 极高（无锁设计）           | 中等（有同步锁）             |
| 递增性     | 时间戳部分递增             | 严格递增                     |
| 每秒生成数 | 数百万                     | 最多 9999                    |
| 可读性     | 较低                       | 很高                         |

## 选择建议

- **TimeBasedRandomIdGenerator**：适用于需要高并发、高性能的场景，如分布式系统的唯一标识
- **TimeBasedBusinessIdGenerator**：适用于需要可读性强、严格递增的业务场景，如订单号生成
