# CosId 分布式 ID 生成器

## 概述

CosId 是一个高性能的分布式 ID 生成器，采用类似 Snowflake 的设计思路，但在位分配和编码方式上进行了优化。它生成的 ID 由时间戳、机器 ID 和序列号三部分组成，并使用 Base62 编码输出，确保了 ID 的全局唯一性和时间有序性。

## 位分配结构

CosId 采用 80 位（10 字节）的二进制结构，具体分配如下：

- 时间戳：44 位
  - 以自定义 epoch 为起点的毫秒数
  - 可使用约 557 年
- 机器 ID：20 位
  - 最多支持 1,048,575 个节点
  - 可根据实际需求分配机器 ID
- 序列号：16 位
  - 同一毫秒内可生成 65,536 个不同的 ID
  - 当序列号用尽时自动等待下一毫秒

## 特性

1. **全局唯一性**：通过机器 ID 和序列号的组合确保在分布式环境下的唯一性
2. **时间有序**：ID 中包含时间戳，天然支持按时间排序
3. **高性能**：使用位运算进行 ID 组装，性能优异
4. **安全处理**：
   - 时钟回拨检测和处理
   - 序列号溢出处理
   - 机器 ID 范围检查

## 使用示例

```java
// 创建生成器实例
long customEpoch = Instant.parse("2020-01-01T00:00:00Z").toEpochMilli();
CosIdGenerator generator = new CosIdGenerator(0xABCDEL, customEpoch);

// 生成ID
Object id = generator.generate(); // 返回Base62编码的ID字符串
```

## 时钟回拨处理

CosId 内置了时钟回拨检测机制。当检测到系统时钟发生回拨时，生成器会抛出异常以防止生成重复的 ID。在实际应用中，建议在外层添加适当的重试机制。

```java
try {
    Object id = generator.generate();
} catch (RuntimeException e) {
    // 处理时钟回拨异常
    if (e.getMessage().equals("Clock moved backwards!")) {
        // 添加重试逻辑
    }
}
```

## 性能考虑

1. 使用 synchronized 确保线程安全
2. 序列号用尽时会自旋等待下一毫秒
3. 使用 ByteBuffer 进行高效的字节操作
4. Base62 编码输出，保证 ID 的可读性和紧凑性

## 最佳实践

1. 选择合适的 epoch 时间，建议使用项目开始时间或系统上线时间
2. 合理分配机器 ID，确保在分布式环境中唯一
3. 在高并发场景下，注意监控序列号使用情况
4. 建议使用 Spring 等容器管理 CosIdGenerator 实例
