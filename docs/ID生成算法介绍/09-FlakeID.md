# FlakeID (`icu.congee.id.generator.flakeid.FlakeId`)

`FlakeId` 是一个用于在分布式环境中生成 K-有序、无冲突 ID 的生成器。它生成的 ID 是一个 64 位的字节数组 (`byte[8]`)。

## 1. ID 结构 (64 位)

FlakeID 将一个 64 位的唯一 ID 分解为以下几个部分：

- **时间戳 (Timestamp)**: 42 位
  - 表示自选定纪元（epoch）以来经过的毫秒数。
  - `time = new Date().getTime() - epoch`
  - 42 位的时间戳可以支持大约 139 年 (`2^42 / (1000 * 60 * 60 * 24 * 365)`)。
- **生成器 ID (Generator ID)**: 10 位
  - 用于唯一标识一个 ID 生成器实例。
  - 可以由一个 10 位的 `id` 直接指定 (0-1023)。
  - 或者由一个 5 位的数据中心 ID (`datacenter`, 0-31) 和一个 5 位的机器 ID (`worker`, 0-31) 组合而成: `id = (datacenter << 5) | worker`。
- **序列号 (Sequence Number)**: 12 位
  - 表示在同一毫秒内生成的 ID 的序列号。
  - 每毫秒内可以生成 `2^12 = 4096` 个唯一 ID (0-4095)。
  - 当同一毫秒内的序列号用尽时，生成器会等待到下一毫秒。

整体结构可以视为：
`[ 42位时间戳 | 10位生成器ID | 12位序列号 ]`

ID 最终以 `byte[8]` 的形式返回，其组装方式细节请参考源代码中 `ByteBuffer` 的操作。大致逻辑是将 42 位时间戳、10 位生成器 ID 和 12 位序列号按特定顺序和位移填充到 8 字节数组中。

## 2. 配置方式 (`FlakeId.Options`)

可以通过 `FlakeId.Options` 类配置生成器：

- `id` (Long): 生成器标识符 (0-1023)。如果设置了此项，则 `datacenter` 和 `worker` 会被忽略。
- `datacenter` (Long): 数据中心标识符 (0-31)。
- `worker` (Long): 工作节点标识符 (0-31)。
- `epoch` (Long): 自定义纪元时间戳 (毫秒)。默认为 0，即使用标准的 Unix 纪元 (1970-01-01T00:00:00Z)。设置一个较近的 epoch 可以延长 ID 的可用时间。
- `seqMask` (Long): 序列掩码。默认为 `0xFFF` (对应 12 位序列号，0-4095)。可以自定义以调整序列号的位数。

## 3. ID 生成算法

1.  **获取当前时间**: 计算 `time = new Date().getTime() - epoch`。
2.  **时钟回拨检测**:
    - 如果 `time < lastTime` (其中 `lastTime` 是上次生成 ID 的时间戳)，则抛出异常，拒绝生成 ID，提示时钟已回拨。
3.  **同一毫秒内处理**:
    - 如果 `time == lastTime`:
      - 检查 `overflow` 标志。如果为 `true` (表示当前毫秒的序列号已用尽)，则调用 `waitForNextMillis()` 等待到下一毫秒，然后重新尝试生成。
      - 序列号递增: `seq = (seq + 1) & seqMask`。
      - 如果序列号 `seq` 在递增后变为 0 (表示环绕，当前毫秒序列号再次用尽)，则设置 `overflow = true`，调用 `waitForNextMillis()` 等待到下一毫秒，然后重新尝试生成。
4.  **新的毫秒处理**:
    - 如果 `time > lastTime`:
      - 重置 `overflow = false`。
      - 重置 `seq = 0`。
5.  **更新最后时间戳**: `lastTime = time`。
6.  **组装 ID**: 根据上述的 ID 结构，将时间戳、生成器 ID 和序列号组合成一个 64 位的 `byte[8]`。

### `waitForNextMillis()` 方法:

- 该方法会阻塞当前线程，直到系统时间进入下一毫秒 (`new Date().getTime() > lastTime`)。
- 在等待期间，它会以 1 毫秒的间隔休眠 (`Thread.sleep(1)`)。
- 进入新的毫秒后，它会更新 `lastTime` 为当前新的毫秒值，并重置 `overflow` 标志为 `false`，序列号 `seq` 为 `0`。

## 4. 主要特性

- **K-有序**: 生成的 ID 在单个生成器实例内大致按时间排序。全局排序取决于时间戳和生成器 ID。
- **唯一性/无冲突**:
  - 在单个生成器实例内，通过序列号和时钟处理保证唯一。
  - 在分布式环境中，如果每个生成器实例配置了唯一的生成器 ID (`id` 或 `datacenter`+`worker` 组合)，则可以保证全局唯一。
- **分布式适用**: 设计用于分布式系统。
- **可配置性**: 允许自定义纪元、生成器 ID 和序列长度。
- **时钟依赖与处理**:
  - 严重依赖系统时钟的单调递增。
  - 能检测并处理时钟回拨 (通过抛出异常)。
  - 能处理同一毫秒内序列号耗尽的情况 (通过等待到下一毫秒)。
- **ID 格式**: 返回 `byte[8]` 类型的 ID。

## 5. 与标准 Snowflake 算法的比较

FlakeID 的设计思想与 Twitter Snowflake 算法非常相似，主要区别可能在于：

- **ID 格式**: FlakeID 返回 `byte[8]`，而 Snowflake 通常直接返回 `long` 类型。FlakeID 内部通过 `ByteBuffer` 操作来组装这个字节数组。
- **配置灵活性**: FlakeID 提供了 `seqMask` 选项，允许更灵活地配置序列号的位数，而标准 Snowflake 通常固定为 12 位。
- **生成器 ID 组合**: FlakeID 明确支持直接设置 10 位 `id` 或通过 5 位 `datacenter` + 5 位 `worker` 组合。
- **溢出处理**: FlakeID 使用一个 `overflow` 标志来显式管理同一毫秒内序列号用尽并等待到下一毫秒的情况。

总的来说，FlakeID 是 Snowflake 思想的一种实现，提供了相似的特性和保证。
