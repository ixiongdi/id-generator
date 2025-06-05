# TtsId 生成算法

## 一、算法概述
TtsId（Timestamp + Type + Sequence ID）是一种结合时间戳、业务类型标识和序列号的复合ID生成算法，主要用于需要按业务类型分类管理且具备时序性的分布式系统场景。其核心设计目标是在保证全局唯一性的同时，通过可解析的字段结构提升业务可追溯性。

## 二、核心特性
- **业务可解析**：ID中显式包含业务类型标识，可直接从ID值推断业务场景（如订单、用户、日志等）
- **时序保证**：基于毫秒级时间戳生成，同业务类型下ID严格递增
- **灵活扩展**：各字段长度可配置，支持不同业务场景的差异化需求
- **低碰撞率**：通过序列号字段解决同一时间戳内的并发冲突问题
- **存储友好**：采用数值型ID（长整型或大整数），相比字符串ID节省存储空间

## 三、数据结构设计
典型TtsId采用64位长整型，字段分配示例：

| 字段         | 位数 | 描述                                                                 | 取值范围           |
|--------------|------|----------------------------------------------------------------------|--------------------|
| 符号位       | 1    | 固定为0（保证正数）                                                   | 0                  |
| 时间戳       | 41   | 毫秒级时间戳（相对于自定义纪元时间）                                   | 约69年（2^41/1e3/3600/24/365） |
| 业务类型标识 | 10   | 支持1024种不同业务类型（可根据需求调整位数）                           | 0-1023             |
| 序列号       | 12   | 单时间戳内最大并发量4096（可根据业务并发量调整位数）                   | 0-4095             |

计算公式：`ID = (timestamp - epoch) << 22 | (typeId << 12) | sequence`

## 四、与其他算法对比
| 特性         | TtsId              | Snowflake            | TimeBasedBusinessId   |
|--------------|--------------------|----------------------|-----------------------|
| 业务可解析   | 是（显式类型字段） | 否                   | 是（自定义格式）       |
| 严格递增性   | 是（同类型）       | 是                   | 部分（按时间分段）     |
| 字段灵活性   | 高（各段可配置）   | 固定（时间+机器+序列）| 高（自定义字段组合）   |
| 最大并发量   | 4096/ms（默认）    | 4096/ms（默认）       | 取决于序列号位数       |
| 存储类型     | 长整型             | 长整型               | 字符串/长整型         |

## 五、使用注意事项
1. **纪元时间设置**：建议选择系统上线时间作为纪元，避免时间戳过早溢出（如设置为2020-01-01 00:00:00）
2. **业务类型分配**：需建立全局业务类型注册表，避免不同服务使用相同类型ID导致冲突
3. **序列号管理**：单节点需使用原子计数器保证序列号递增，跨节点需通过分布式锁或中心服务协调
4. **时间同步要求**：依赖节点本地时间，需部署NTP服务保证各节点时间偏差小于1ms
5. **字段扩展策略**：当业务类型超过当前位数限制时，可通过调整时间戳/序列号位数重新分配字段（需版本兼容设计）
6. **冲突检测机制**：建议在ID生成服务中增加缓存，记录最近生成的ID防止时钟回拨导致的重复

## 六、典型应用场景
- 电商系统：区分订单ID（type=100）、用户ID（type=200）、物流单号（type=300）等
- 日志系统：按日志级别（type=0-9）或服务模块（type=10-99）分类存储
- 物联网平台：标识不同设备类型（传感器=400，控制器=500）的上报数据

## 七、示例代码（Java）
```java
public class TtsIdGenerator {
    private static final long EPOCH = 1577836800000L; // 2020-01-01 00:00:00
    private static final int TYPE_BITS = 10;
    private static final int SEQ_BITS = 12;
    private static final long MAX_SEQ = (1L << SEQ_BITS) - 1;

    private final long typeId;
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public TtsIdGenerator(long typeId) {
        if (typeId < 0 || typeId >= (1L << TYPE_BITS)) {
            throw new IllegalArgumentException("Invalid typeId: " + typeId);
        }
        this.typeId = typeId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + "ms");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQ;
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << (TYPE_BITS + SEQ_BITS))
             | (typeId << SEQ_BITS)
             | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
```

## 八、总结
TtsId算法通过融合时间戳、业务类型和序列号，在保证ID唯一性和时序性的同时，显著提升了业务可追溯能力。适用于需要按业务维度分类管理ID的中高并发分布式系统，相比传统Snowflake算法增加了业务语义信息，是业务场景驱动的ID生成优化方案。