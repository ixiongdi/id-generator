# UUIDv7

## 算法概述

UUIDv7 是 UUID（通用唯一标识符）规范的第 7 个版本，它是一种基于时间的 UUID 格式，旨在解决之前 UUID 版本的一些局限性。UUIDv7 主要特点是将时间戳作为最高有效位部分，这使得生成的 ID 按时间顺序排列，同时保持了 UUID 的全局唯一性。

UUIDv7 是在 RFC 4122 的基础上进行的扩展，目前已经在 IETF 的 UUID 新版本草案中定义。它结合了时间戳的有序性和随机性的唯一性，适用于需要时间排序的分布式系统。

## ID 结构

UUIDv7 的 128 位结构如下：

- **最高有效位(MSB)**：

  - 48 位：Unix 时间戳（毫秒级）
  - 4 位：版本号（固定为 7）
  - 12 位：序列号（处理时钟回拨和相同毫秒内的多次生成）

- **最低有效位(LSB)**：
  - 2 位：变体标识（固定为 RFC 4122 规范的值）
  - 62 位：随机数据

标准格式表示为：`xxxxxxxx-xxxx-7xxx-yxxx-xxxxxxxxxxxx`，其中：

- `7` 表示版本号
- `y` 表示变体（通常为 8、9、A 或 B）

## 实现原理

本项目中的 UUIDv7 实现采用以下步骤：

1. **时间戳获取**：使用`System.currentTimeMillis()`获取当前时间的毫秒级时间戳
2. **时钟回拨处理**：
   - 如果当前时间戳小于或等于上次生成的时间戳，增加序列号
   - 否则重置序列号并更新上次时间戳
3. **MSB 构建**：
   - 将时间戳左移 16 位
   - 添加版本号 7（0x7000）
   - 添加 12 位序列号
4. **LSB 构建**：
   - 设置变体位为 2（RFC 4122 规范）
   - 添加 62 位随机数
5. **UUID 创建**：使用构建的 MSB 和 LSB 创建新的 UUID 实例

## 优缺点

### 优点

- **时间有序性**：生成的 ID 按时间顺序排列，便于数据库索引和排序
- **高性能**：生成速度快，不依赖外部协调
- **全局唯一性**：结合时间戳和随机数，保证了分布式环境下的唯一性
- **可预测的排序**：基于时间的排序使得 ID 在时间维度上可预测
- **无需中央协调**：不需要中央服务器分配 ID，适合分布式系统

### 缺点

- **时钟依赖**：依赖系统时钟，时钟回拨可能导致问题（虽然有序列号缓解）
- **ID 长度**：36 字符的标准表示形式相对较长
- **存储空间**：比纯数字 ID 占用更多存储空间

## 适用场景

- **分布式系统**：需要在多节点环境下生成唯一 ID
- **时序数据**：需要按时间顺序处理或查询的数据
- **数据库主键**：特别适合需要时间排序的数据库表
- **日志系统**：记录事件并按时间顺序排序
- **消息队列**：需要保证消息的时间顺序

## 代码示例

```java
public class UUIDv7Generator implements IdGenerator {
    private static volatile long lastTimestamp = 0L;
    private static final LongAdder sequence = new LongAdder();

    /**
     * 生成一个新的UUIDv7
     */
    public static synchronized UUID next() {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();

        // 处理时钟回拨和相同时间戳的情况
        if (currentTime <= lastTimestamp) {
            sequence.increment();
        } else {
            sequence.reset();
            lastTimestamp = currentTime;
        }

        // 构建最高有效位(MSB)
        long msb = currentTime << 16 | 0x7000 | (sequence.sum() & 0xFFF);

        // 构建最低有效位(LSB)
        long lsb = 0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;

        return new UUID(msb, lsb);
    }

    @Override
    public String generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv7;
    }
}
```

## 示例输出

```
01889a0e-d2bd-7c77-8a5e-d45e506ec5c3
01889a0e-d2bd-7c78-b1c5-a3c4a1a4d7c2
01889a0e-d2bd-7c79-8e5a-f1c2d3b4e5f6
```

## 参考资料

- [UUID 新版本草案 - IETF](https://datatracker.ietf.org/doc/html/draft-peabody-uuid-urn-namespace)
- [RFC 4122 - UUID 规范](https://tools.ietf.org/html/rfc4122)
