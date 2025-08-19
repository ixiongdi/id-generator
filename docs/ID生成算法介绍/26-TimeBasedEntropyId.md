# TimeBasedEntropyId 生成器 (`uno.xifan.id.generator.custom.TimeBasedEntropyIdGenerator`)

`TimeBasedEntropyIdGenerator` 是一个简单高效的分布式唯一 ID 生成器实现。它结合了时间戳和多熵源生成的哈希值，提供了一种轻量级但功能强大的 ID 生成方案。

## 核心特性

- **简单高效**: 实现极其简洁，仅需几行代码即可生成唯一 ID，无需复杂配置。
- **分布式友好**: 通过多熵源（时间戳、MAC 地址、计数器、安全随机数）确保在分布式环境中的唯一性。
- **时序性**: ID 的高 32 位基于时间戳，保证了 ID 的粗略时序性，便于排序和分析。
- **无协调依赖**: 不依赖外部系统或中心化节点进行协调，适合无协调环境。

## ID 结构 (64 位)

`TimeBasedEntropyIdGenerator` 生成的 ID 是一个 64 位的 `long` 类型整数。其结构如下：

- **时间戳部分**: 高 32 位，基于秒级时间戳与自定义纪元的差值。
- **熵值部分**: 低 32 位，由 `EntropyKey` 类生成的多熵源哈希值的绝对值。

ID 构成公式：`ID = (System.currentTimeMillis() / 1000 - epoch) << 32 | (new EntropyKey().hashCode() & 0x7FFFFFFFL)`

### 1. 时间戳部分

- **位数**: 32 位
- **计算方式**: `System.currentTimeMillis() / 1000 - epoch`
  - 当前系统时间（毫秒）转换为秒级
  - 减去自定义纪元时间 `epoch`（1746028800，约为 2025-04-30）
- **特点**:
  - 使用秒级时间戳，而非毫秒级，减小了时间戳部分的值
  - 32 位时间戳部分可以使用约 69 年

### 2. 熵值部分

- **位数**: 32 位
- **来源**: `EntropyKey` 类的哈希码的绝对值
- **熵源组成**:
  - **时间戳**: 纳秒级时间戳 (`System.nanoTime()`)
  - **计数器**: 原子递增计数器 (`AtomicLong`)
  - **节点标识**: 基于 MAC 地址的节点 ID
  - **安全随机数**: 使用 `SecureRandom` 生成的随机值
- **特点**:
  - 多熵源结合，大幅降低冲突概率

## 优势

1. **实现极简**: 核心生成逻辑仅一行代码，易于理解和维护。
2. **无外部依赖**: 不依赖数据库、缓存或其他外部系统。
3. **高性能**: 生成过程无锁、无 I/O 操作，性能极高。
4. **冲突概率低**: 通过多熵源组合，即使在高并发分布式环境下，冲突概率也极低。
5. **时序可追踪**: ID 高位包含时间信息，便于按时间排序和追踪。

## 局限性

1. **非严格单调递增**: 虽然包含时间戳，但同一秒内生成的多个 ID 之间不保证严格递增。

## 适用场景

- **分布式系统**: 适合需要生成唯一标识符的分布式应用。
- **高并发环境**: 无锁设计使其适合高并发场景。
- **无协调需求**: 特别适合不方便或不需要节点间协调的系统。
- **对 ID 长度敏感的应用**: 生成的是数值型 ID，比 UUID 等字符串 ID 更节省空间。

## 代码实现

```java
public class TimeBasedEntropyIdGenerator implements IdGenerator {

    private static final int epoch = 1746028800;

    public static long next() {
        return (System.currentTimeMillis() / 1000 - epoch) << 32 | (new EntropyKey().hashCode() & 0x7FFFFFFFL);
    }

    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.TimeBasedEntropyId;
    }
}
```

熵源类 `EntropyKey` 的核心实现：

```java
@Data
public class EntropyKey {

    private static final AtomicLong COUNTER = new AtomicLong(0);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final long NODE = initializeNodeIdentifier(); // 基于MAC地址

    private final long timestamp;         // 纳秒级时间戳
    private final long counter;           // 递增计数器
    private final long node;              // 节点标识
    private final long secureRandom;      // 安全随机整数

    public EntropyKey() {
        this.timestamp = System.nanoTime();
        this.counter = COUNTER.getAndIncrement();
        this.node = NODE;
        this.secureRandom = SECURE_RANDOM.nextLong();
    }

    // 其他实现细节...
}
```

## 使用示例

```java
// 创建生成器实例
TimeBasedEntropyIdGenerator generator = new TimeBasedEntropyIdGenerator();

// 生成ID
Long id = generator.generate();
System.out.println(id); // 例如: 3222468131246802

// 或使用静态方法
long id2 = TimeBasedEntropyIdGenerator.next();
System.out.println(id2); // 例如: 3222467286060751
```

## 与其他算法比较

| 特性         | TimeBasedEntropyId | Snowflake | UUID         | 数据库自增 ID |
| ------------ | ------------------ | --------- | ------------ | ------------- |
| 位数         | 64 位              | 64 位     | 128 位       | 取决于配置    |
| 包含时间信息 | 是                 | 是        | UUIDv1包含时间信息 | 否            |
| 单调递增     | 部分（按秒）       | 是        | 否           | 是            |
| 分布式友好   | 是                 | 需配置机器ID | 是           | 否            |
| 性能         | 极高               | 高        | 高           | 受数据库QPS限制  |
| 实现复杂度   | 低                 | 中（需配置机器ID） | 低（标准库实现） | 低（数据库内置） |
| 外部依赖     | 无                 | 无        | 无           | 数据库        |

## 总结

TimeBasedEntropyId 算法提供了一种简单高效的分布式 ID 生成方案，特别适合那些需要无协调、高性能 ID 生成的场景。它通过结合时间戳和多熵源哈希值，在保持实现简单的同时，提供了良好的唯一性保证和粗略的时序性。
