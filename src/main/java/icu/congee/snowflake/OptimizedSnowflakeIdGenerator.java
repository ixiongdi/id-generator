package icu.congee.snowflake;

import java.util.concurrent.atomic.AtomicLong;

public class OptimizedSnowflakeIdGenerator {
    // 起始时间戳（2021-01-01 00:00:00）
    private final static long START_TIMESTAMP = 1609459200000L;
    // 各部分位数定义
    private final static long SEQUENCE_BITS = 12L;    // 序列号占12位
    private final static long WORKER_ID_BITS = 5L;    // 机器ID占5位
    private final static long DATACENTER_ID_BITS = 5L; // 数据中心ID占5位
    // 最大值计算
    private final static long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private final static long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private final static long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    // 左移位计算
    private final static long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private final static long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private final static long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long workerId;       // 工作机器ID
    private final long datacenterId;   // 数据中心ID
    private final AtomicLong sequence = new AtomicLong(0L); // 使用 AtomicLong 管理序列号
    private volatile long lastTimestamp = -1L; // 上次生成时间

    public OptimizedSnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException("Datacenter ID 超出范围");
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID 超出范围");
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    public long nextId() {
        long timestamp = System.currentTimeMillis();
        long currentSequence;

        while (true) {
            // 处理时钟回拨
            if (timestamp < lastTimestamp) {
                // 等待时钟恢复
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                timestamp = System.currentTimeMillis();
                continue;
            }

            // 获取并递增序列号
            currentSequence = sequence.getAndIncrement();
            if (currentSequence <= MAX_SEQUENCE) {
                break; // 序列号未用尽，跳出循环
            }

            // 序列号用尽，等待下一毫秒
            timestamp = waitNextMillis(lastTimestamp);
            sequence.set(0L); // 重置序列号
        }

        lastTimestamp = timestamp;
        // 计算最终ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | currentSequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}