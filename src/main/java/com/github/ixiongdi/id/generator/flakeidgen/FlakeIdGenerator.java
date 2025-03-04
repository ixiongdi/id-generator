package com.github.ixiongdi.id.generator.flakeidgen;

import java.util.concurrent.atomic.AtomicLong;

public class FlakeIdGenerator {
    // 时间戳位数（原始JS代码约43位，此处调整为41位以兼容标准Snowflake结构）
    private static final long TIMESTAMP_BITS = 41L;
    // 机器ID位数（10位，支持1024个节点）
    private static final long WORKER_ID_BITS = 10L;
    // 序列号位数（12位，每毫秒4096个ID）
    private static final long SEQUENCE_BITS = 12L;

    // 时间戳偏移量（机器ID + 序列号）
    private static final long TIMESTAMP_SHIFT = WORKER_ID_BITS + SEQUENCE_BITS;
    // 机器ID偏移量（序列号）
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    // 自定义起始时间（类似JS的epoch参数）
    private final long epoch;
    // 机器ID（由datacenter和worker组合生成）
    private final long workerId;
    // 序列号掩码（防止溢出）
    private final long sequenceMask = ~(-1L << SEQUENCE_BITS);
    
    // 上一次生成ID的时间戳和序列号
    private volatile long lastTimestamp = -1L;
    private final AtomicLong sequence = new AtomicLong(0);

    public FlakeIdGenerator(long datacenter, long worker, long epoch) {
        // 校验参数范围（参考JS的位掩码逻辑）
        if (datacenter > 31 || worker > 31) 
            throw new IllegalArgumentException("Datacenter/Worker超出范围");
        // 合并datacenter和worker为10位机器ID（类似JS的id生成逻辑）
        this.workerId = (datacenter << 5) | worker;
        this.epoch = epoch;
    }

    public synchronized long nextId() {
        long currentTime = System.currentTimeMillis() - epoch;

        // 时钟回拨检测（与JS逻辑一致）
        if (currentTime < lastTimestamp) {
            throw new RuntimeException("时钟回拨 " + (lastTimestamp - currentTime) + "ms");
        }

        // 同一毫秒内递增序列号
        if (currentTime == lastTimestamp) {
            long seq = sequence.incrementAndGet() & sequenceMask;
            if (seq == 0) { // 序列号溢出
                currentTime = waitNextMillis(currentTime);
            }
        } else {
            sequence.set(0);
        }
        lastTimestamp = currentTime;

        // 组合ID（参考JS的位操作逻辑）
        return (currentTime << TIMESTAMP_SHIFT) 
             | (workerId << WORKER_ID_SHIFT) 
             | sequence.get();
    }

    private long waitNextMillis(long currentTime) {
        long newTime;
        do {
            newTime = System.currentTimeMillis() - epoch;
        } while (newTime <= currentTime);
        return newTime;
    }
}