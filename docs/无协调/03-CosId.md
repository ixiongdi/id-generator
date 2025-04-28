# CosId

## 简介

CosId 是一款通用、灵活、高性能的分布式 ID 生成器。 <mcreference link="https://cosid.ahoo.me/" index="2">2</mcreference> <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference> 它旨在解决在分布式系统中生成唯一 ID 的各种挑战，例如时钟回拨、机器 ID 分配等问题，并提供友好的使用体验。 <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

## 核心特性

- **通用性**：支持多种主流的分布式 ID 算法，如 SnowflakeId、SegmentId (包括 SegmentChainId)。 <mcreference link="https://cosid.ahoo.me/" index="2">2</mcreference>
- **灵活性**：可以通过简单配置切换不同的 ID 生成算法实现，并支持自定义配置以满足特定场景需求。 <mcreference link="https://cosid.ahoo.me/" index="2">2</mcreference>
- **高性能**：经过极致优化设计，例如 `SegmentChainId` 的性能可达到近似 `AtomicLong` 的 TPS (每秒事务处理量) 级别。 <mcreference link="https://cosid.ahoo.me/" index="2">2</mcreference>
- **易用性**：提供简洁的 API 和多种开箱即用的组件，如机器 ID 分配器、号段分发器等。 <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

## 主要 ID 生成器

CosId 主要提供以下几种 ID 生成器：

### 1. SnowflakeId

标准的 Snowflake 算法将一个 64 位的 long 型数字划分为：时间戳（通常 41 位）、机器 ID（通常 10 位）和序列号（通常 12 位）。 <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>
CosId 对 SnowflakeId 进行了增强，主要解决了以下问题： <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

- **时钟回拨问题**：提供了时钟同步器机制，如 `DefaultClockBackwardsSynchronizer`，通过自旋等待或在超过阈值时抛出异常来处理时钟回拨。 <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>
- **机器 ID 分配问题**：提供了多种 `MachineIdDistributor` 实现： <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>
  - `ManualMachineIdDistributor`：手动配置机器 ID。
  - `StatefulSetMachineIdDistributor`：利用 Kubernetes StatefulSet 的稳定标识作为机器 ID。
  - `RedisMachineIdDistributor`：使用 Redis 来存储和分发机器 ID。

### 2. SegmentId (号段模式)

`SegmentId` 通过每次获取一个 ID 号段（Step）来减少与号段分发器的网络 IO 请求频率，从而提升性能。 <mcreference link="https://www.oschina.net/news/173144/cosld-1-4-5-released" index="3">3</mcreference> <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

- **IdSegmentDistributor (号段分发器)**：负责存储和分发号段，支持多种实现： <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

  - `RedisIdSegmentDistributor`：基于 Redis。
  - `JdbcIdSegmentDistributor`：基于 JDBC，支持各种关系型数据库。
  - `ZookeeperIdSegmentDistributor`：基于 Zookeeper。
  - `MongoIdSegmentDistributor`：基于 MongoDB。

- **SegmentChainId (推荐)**：是对 `SegmentId` 的增强，采用无锁（lock-free）设计。 <mcreference link="https://www.oschina.net/news/173144/cosld-1-4-5-released" index="3">3</mcreference> <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference> 它通过 `PrefetchWorker` 维护一个安全距离（safeDistance），并支持基于饥饿状态的动态扩容/收缩，性能极高。 <mcreference link="https://www.oschina.net/news/173144/cosld-1-4-5-released" index="3">3</mcreference> <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

### 3. CosIdGenerator

`CosIdGenerator` 是一种单机 ID 生成器，性能非常高（官方宣称是 `UUID.randomUUID()` 的三倍），生成的 ID 基于时间且全局趋势递增。 <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>

## 使用场景

分布式 ID 生成器在以下场景中至关重要： <mcreference link="https://www.oschina.net/news/173144/cosld-1-4-5-released" index="3">3</mcreference>

- **数据库分库分表**：当业务规模增长，需要对数据库进行水平拆分时，需要全局唯一的 ID 来作为主键，并指导数据分片。
- **分布式系统中的唯一标识**：如订单 ID、消息 ID、会话 ID 等，确保在整个集群中的唯一性。
- **需要趋势递增 ID 的场景**：某些业务场景或数据库索引（如 MySQL InnoDB 的 B+树）对 ID 的有序性有要求，趋势递增的 ID 有助于提高查询和插入性能。 <mcreference link="https://www.oschina.net/news/173144/cosld-1-4-5-released" index="3">3</mcreference>

## 如何选择

- 如果需要极高的单机生成性能且不依赖外部存储，可以考虑 `CosIdGenerator`。
- 如果系统部署在 Kubernetes 环境中，`SnowflakeId` 配合 `StatefulSetMachineIdDistributor` 是一个不错的选择。
- 对于需要极高性能且能容忍一定程度中心化依赖（如 Redis 或 DB）的场景，`SegmentChainId` 是首选。
- 如果对 ID 有严格的连续性或特定步长要求，可能需要更细致地配置 `SegmentId`。

## 本项目中的 CosId

在当前项目中，`icu.congee.id.generator.distributed.cosid.CosId` 类定义了一个 ID 的结构，包含了时间戳 (`timestamp`)、机器 ID (`machineId`) 和序列号 (`sequence`)，以及它们各自的位数配置。这与 Snowflake 算法的核心思想一致，可以看作是 Snowflake ID 的一种具体表现形式或其组成部分。

```java
// c:\Users\76932\ktnb\id-generater\id-generator-spring-redis\src\main\java\icu\congee\id\generator\distributed\cosid\CosId.java
@Data
public class CosId implements Id {

    private long timestamp;
    private long machineId;
    private long sequence;

    // 位数配置
    private int timestampBits;
    private int machineBits;
    private int sequenceBits;

    public CosId(
            long timestamp,
            long machineId,
            long sequence,
            int timestampBits,
            int machineBits,
            int sequenceBits) {
        this.timestamp = timestamp;
        this.machineId = machineId;
        this.sequence = sequence;
        this.timestampBits = timestampBits;
        this.machineBits = machineBits;
        this.sequenceBits = sequenceBits;
    }

    // ... 其他方法
}
```

这个结构体可以被 CosId 框架中的 SnowflakeId 生成器使用和填充。

更多关于 CosId 的详细信息和配置，请参考官方文档：

- CosId 官网: <mcurl name="CosId Official Website" url="https://cosid.ahoo.me/"></mcurl> <mcreference link="https://cosid.ahoo.me/" index="2">2</mcreference>
- CosId GitHub: <mcurl name="CosId GitHub Repository" url="https://github.com/Ahoo-Wang/CosId"></mcurl> <mcreference link="https://github.com/Ahoo-Wang/CosId" index="5">5</mcreference>
