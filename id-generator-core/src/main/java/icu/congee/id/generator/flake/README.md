# FlakeIdGenerator

## 概述

FlakeIdGenerator 是一个基于分布式 ID 生成算法的实现，它能够生成全局唯一的、趋势递增的 ID。该实现特别适用于分布式系统中的 ID 生成需求，通过巧妙的位运算和时间戳的结合，保证了 ID 的唯一性和性能。

## 特性

- 全局唯一：通过时间戳、工作节点 ID 和序列号的组合保证 ID 的唯一性
- 趋势递增：ID 按时间趋势递增，便于数据库存储和查询优化
- 高性能：使用位运算进行 ID 生成，性能高效
- 自动化节点识别：基于 MAC 地址自动生成工作节点 ID
- 时钟回拨处理：内置时钟回拨检测和处理机制

## ID 结构

- 时间戳部分：基于自定义纪元（2021-01-01 00:00:00 UTC）的毫秒数
- 工作节点 ID：48 位，基于 MAC 地址生成
- 序列号：16 位，同一毫秒内的自增序列

## 核心参数

```java
private static final long EPOCH = 1609459200000L; // 2021-01-01 00:00:00 UTC
private static final int WORKER_ID_BITS = 48;    // 工作节点ID位数
private static final int SEQUENCE_BITS = 16;     // 序列号位数
```

## 使用示例

```java
// 创建生成器实例
FlakeIdGenerator generator = new FlakeIdGenerator();

// 生成ID
long id = generator.generateFlakeId();

// 批量生成ID示例
for (int i = 0; i < 10; i++) {
    System.out.println(generator.generateFlakeId());
}
```

## 工作节点 ID 生成机制

FlakeIdGenerator 使用 MAC 地址作为工作节点 ID 的基础，这样可以保证在分布式环境中每个节点的 ID 都是唯一的：

1. 获取第一个可用的非回环网络接口
2. 读取网络接口的 MAC 地址
3. 将 MAC 地址转换为长整型作为工作节点 ID
4. 如果无法获取 MAC 地址，则使用默认值 0

## 时钟回拨处理

为了处理服务器时钟回拨问题，FlakeIdGenerator 实现了以下策略：

1. 检测到时钟回拨时抛出异常
2. 当前时间戳等于上次生成 ID 的时间戳时，增加序列号
3. 序列号超过最大值时，等待下一毫秒

## 性能考虑

- 使用位运算进行 ID 组装，性能高效
- synchronized 关键字保证线程安全
- 序列号使用位运算控制循环，避免超出范围

## 注意事项

1. 确保服务器时间同步，避免时钟回拨
2. 在分布式环境中，确保网络接口配置正确
3. 注意序列号的溢出处理

## 实现接口

FlakeIdGenerator 实现了 IdGenerator 接口，可以无缝集成到现有系统中：

```java
@Override
public Long generate() {
    return INSTANCE.generateFlakeId();
}

@Override
public IdType idType() {
    return IdType.Flake;
}
```
