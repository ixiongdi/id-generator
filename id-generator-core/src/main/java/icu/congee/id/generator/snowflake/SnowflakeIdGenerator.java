package icu.congee.id.generator.snowflake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

/** Snowflake ID生成器实现 */
public class SnowflakeIdGenerator implements IdGenerator {

    private static final long EPOCH = 1640995200000L; // 2022-01-01 00:00:00

    // 位数分配
    private static final int TIMESTAMP_BITS = 41; // 时间戳占41位
    private static final int WORKER_ID_BITS = 10; // 工作节点ID占10位
    private static final int SEQUENCE_BITS = 12; // 序列号占12位

    // 最大值
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    // 位移
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    private final long workerId; // 工作节点ID
    private final AtomicLong sequence; // 序列号
    private long lastTimestamp; // 上次生成ID的时间戳

    public SnowflakeIdGenerator(long workerId) {
        // 校验workerId的合法性
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException("Worker ID can't be greater than " + MAX_WORKER_ID + " or less than 0");
        }
        this.workerId = workerId;
        this.sequence = new AtomicLong(0L);
        this.lastTimestamp = -1L;
    }

    public synchronized long next() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + 
                    (lastTimestamp - timestamp) + " milliseconds");
        }

        // 如果是同一时间生成的，则进行序列号递增
        if (lastTimestamp == timestamp) {
            sequence.set((sequence.get() + 1) & MAX_SEQUENCE);
            // 序列号已经达到最大值
            if (sequence.get() == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列重置
            sequence.set(0L);
        }

        lastTimestamp = timestamp;

        // 组合ID（时间戳部分 | 工作节点ID部分 | 序列号部分）
        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT) |
                (workerId << WORKER_ID_SHIFT) |
                sequence.get();
    }

    private long timeGen() {
        return Instant.now().toEpochMilli();
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    @Override
    public Object generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.Snowflake;
    }
}