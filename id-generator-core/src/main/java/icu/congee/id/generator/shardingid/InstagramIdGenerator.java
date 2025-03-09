package icu.congee.id.generator.shardingid;

import java.util.concurrent.atomic.AtomicInteger;

public class InstagramIdGenerator {
    // 自定义的时间起点（epoch），这里以2011年1月1日为例
    private static final long CUSTOM_EPOCH = 1314220021721L; // 2011-01-01 00:00:00 UTC的时间戳（毫秒）
    // 时间戳部分占用的位数
    private static final int TIMESTAMP_BITS = 41;
    // 分片ID部分占用的位数
    private static final int SHARD_ID_BITS = 13;
    // 序列号部分占用的位数
    private static final int SEQUENCE_BITS = 10;

    // 分片ID的最大值
    private static final int MAX_SHARD_ID = (1 << SHARD_ID_BITS) - 1;
    // 序列号的最大值
    private static final int MAX_SEQUENCE = (1 << SEQUENCE_BITS) - 1;

    // 时间戳的偏移量
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + SHARD_ID_BITS;
    // 分片ID的偏移量
    private static final long SHARD_ID_LEFT_SHIFT = SEQUENCE_BITS;

    // 序列号的原子变量，用于线程安全地生成序列号
    private AtomicInteger sequence = new AtomicInteger(0);
    // 上一次的时间戳
    private long lastTimestamp = -1;

    // 分片ID
    private int shardId;

    public InstagramIdGenerator(int shardId) {
        if (shardId < 0 || shardId > MAX_SHARD_ID) {
            throw new IllegalArgumentException("Shard ID exceeds its bit limit");
        }
        this.shardId = shardId;
    }

    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            // 如果当前时间戳小于上次的时间戳，说明发生了时间回拨，等待直到时间追上
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - currentTimestamp) + " milliseconds");
        }

        if (currentTimestamp == lastTimestamp) {
            // 如果当前时间戳与上次相同，则更新序列号
            int currentSequence = sequence.getAndIncrement();
            if (currentSequence > MAX_SEQUENCE) {
                // 如果序列号超出最大值，等待下一毫秒
                currentTimestamp = waitForNextMillis(lastTimestamp);
            }
        } else {
            // 如果是新的时间戳，重置序列号
            sequence.set(0);
        }

        lastTimestamp = currentTimestamp;

        // 生成ID
        return ((currentTimestamp - CUSTOM_EPOCH) << TIMESTAMP_LEFT_SHIFT)
                | (shardId << SHARD_ID_LEFT_SHIFT)
                | sequence.getAndIncrement();
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    public static void main(String[] args) {
        InstagramIdGenerator idGenerator = new InstagramIdGenerator(5); // 设置分片ID为5
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.generateId();
            System.out.println("Generated ID: " + id);
            System.out.println("Decomposed ID: " + decomposeId(id));
        }
    }

    private static String decomposeId(long id) {
        long timestamp = (id >> TIMESTAMP_LEFT_SHIFT) + CUSTOM_EPOCH;
        int shardId = (int) ((id >> SHARD_ID_LEFT_SHIFT) & ((1 << SHARD_ID_BITS) - 1));
        int sequence = (int) (id & ((1 << SEQUENCE_BITS) - 1));
        return "Timestamp: " + timestamp + ", Shard ID: " + shardId + ", Sequence: " + sequence;
    }
}