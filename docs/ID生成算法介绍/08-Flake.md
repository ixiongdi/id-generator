# Flake ID 生成器 (`uno.xifan.id.generator.flake.FlakeIdGenerator`)

`FlakeIdGenerator` 是一个用于生成分布式唯一 ID 的 Java 实现。其设计灵感来源于 Twitter 的 Snowflake 算法，但在具体实现上有所不同，尤其是在 ID 的构成上。

## 核心特性

- **唯一性**: 在单个工作节点内，通过序列号和时间戳管理确保 ID 的唯一性。如果工作节点 ID（基于 MAC 地址）全局唯一，则生成的 ID 也全局唯一。
- **高性能**: ID 生成过程主要依赖位运算和本地状态，性能较高。`generateFlakeId` 方法使用 `synchronized` 保证线程安全。
- **工作节点 ID 自动配置**: 工作节点 ID 尝试从网络接口的 MAC 地址自动生成。
- **时钟回拨处理**: 检测到时钟回拨时会抛出运行时异常。

## ID 结构 (64 位)

`FlakeIdGenerator` 生成的 ID 是一个 64 位的 `long` 类型整数。其结构如下：

- **工作节点 ID (`workerId`)**: 高 48 位。
- **序列号 (`sequence`)**: 低 16 位。

**重要说明**: 与许多类似 Snowflake 的实现不同，此 `FlakeIdGenerator` 的最终 ID**不直接包含时间戳信息**。时间戳在生成过程中用于控制序列号的分配和确保 ID 的顺序性（在同一节点内），但时间戳本身的值不作为 ID 的一部分存储。

ID 构成公式可以简化理解为：`ID = (workerId << 16) | sequence`

### 1. 工作节点 ID (`workerId`)

- **位数**: 48 位
- **来源**:
  - 程序尝试获取第一个“UP”且非“LOOPBACK”状态的网络接口的 MAC 地址。
  - MAC 地址（通常为 6 字节，即 48 位）直接用作 `workerId`。
  - 如果无法获取 MAC 地址（例如，没有网络接口或发生异常），`workerId` 默认为 `0`。
- **最大值**: `2^48 - 1`

### 2. 序列号 (`sequence`)

- **位数**: 16 位
- **范围**: 0 到 65535 (`2^16 - 1`)
- **行为**:
  - 在同一毫秒内，每次调用生成方法，序列号递增 1。
  - 如果在同一毫秒内序列号达到最大值 (65535)，再次请求 ID 时，生成器会等待直到下一毫秒。
  - 当时间戳进入下一毫秒时，序列号重置为 0。

### 3. 时间戳 (`timestamp`) 与纪元 (`EPOCH`)

虽然时间戳不直接嵌入最终 ID，但它在生成逻辑中至关重要：

- **纪元 (`EPOCH`)**: `1609459200000L` (即 UTC 2021-01-01 00:00:00)。这是一个固定的起始时间点。
- **当前时间戳**: 使用 `System.currentTimeMillis()` 获取。
- **作用**:
  - **时钟回拨检测**: 如果当前时间戳小于上一次生成 ID 时的时间戳，系统会抛出 `RuntimeException("时钟回拨异常")`。
  - **序列号管理**:
    - 当时间戳与上次相同时，序列号递增。
    - 当时间戳前进到下一毫秒时，序列号重置为 0。
    - 如果同一毫秒内序列号耗尽，会自旋等待到下一毫秒 (`waitNextMillis` 方法)。

## 生成过程

1.  获取当前毫秒级时间戳 (`currentTimestamp`)。
2.  **时钟回拨检查**:
    - 如果 `currentTimestamp < lastTimestamp` (上一次生成 ID 的时间戳)，抛出异常。
3.  **序列号处理**:
    - 如果 `currentTimestamp == lastTimestamp`:
      - `sequence = (sequence + 1) & MAX_SEQUENCE` (其中 `MAX_SEQUENCE` 是 `2^16 - 1`)。
      - 如果 `sequence` 溢出变为 0 (意味着当前毫秒的序列号已用尽)，则调用 `waitNextMillis(lastTimestamp)` 等待直到下一毫秒，并将 `currentTimestamp` 更新为这个新的毫秒值。序列号在进入新的毫秒后会在下一步被重置。
    - 如果 `currentTimestamp > lastTimestamp` (进入新的毫秒):
      - `sequence = 0`。
4.  更新 `lastTimestamp = currentTimestamp`。
5.  **组合 ID**:
    - `long id = (workerId << SEQUENCE_BITS) | sequence;`
    - (原始代码中的 `((currentTimestamp - EPOCH) << (WORKER_ID_BITS + SEQUENCE_BITS))` 部分，由于 `WORKER_ID_BITS + SEQUENCE_BITS = 48 + 16 = 64`，对于 64 位 `long` 类型，左移 64 位会导致该项变为 0，因此不影响最终 ID 值。)

## 潜在问题与考虑

- **Worker ID 冲突**: 如果多个节点获取到相同的 MAC 地址（理论上不太可能，但在虚拟化环境中或特定配置下可能需要注意）或者都无法获取 MAC 地址（都默认为 0），则可能产生重复 ID。
- **ID 有序性**:
  - 在单个节点内，ID 是严格按时间递增的。
  - 全局来看，由于 ID 的高位是 `workerId`，ID 首先按 `workerId` 排序，然后在同一 `workerId` 内按时间排序。它不是全局严格按时间排序的（不同 workerId 的 ID，即使后者时间戳更晚，也可能因为 workerId 较小而整体 ID 值更小）。
- **时钟依赖**: 严重依赖系统时钟的准确性。尽管有回拨检测，但频繁的时钟调整可能影响服务。

## 与 `uno.xifan.id.generator.flakeid.FlakeIdGenerator` 的区别

本项目中还存在一个 `uno.xifan.id.generator.flakeid.FlakeIdGenerator` (注意包名和类名后缀 `id`)。两者都受 Snowflake 启发，但 ID 结构不同：

- `flake.FlakeIdGenerator` (本文档描述的): ID = 48 位 WorkerID + 16 位序列号。时间戳不直接在 ID 中。
- `flakeid.FlakeIdGenerator`: 通常 ID 结构为 时间戳 + 生成器标识 + 序列号 (例如，其 README 中提到的是 42 位时间戳 + 10 位生成器 ID + 12 位序列号)。

请根据具体需求选择合适的 ID 生成器。
