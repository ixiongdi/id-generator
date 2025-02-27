package icu.congee;

import java.util.concurrent.atomic.AtomicLong;

public class InstagramIdGenerator {
    // 自定义起始时间：2011年1月1日 00:00:00 UTC 的毫秒数
    private static final long EPOCH = 1293840000000L;
    private static final int SHARD_ID_BITS = 13;    // 分片 ID 占用 13 位
    private static final int SEQUENCE_BITS = 10;    // 序列号占用 10 位
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1; // 最大序列号：1023

    private final AtomicLong lastTimestamp = new AtomicLong(-1L); // 上次生成 ID 的时间戳
    private final AtomicLong sequence = new AtomicLong(0L);       // 当前序列号

    /**
     * 根据给定的分片 ID 生成一个唯一的 64 位 ID。
     *
     * @param shardId 分片 ID（范围：0 到 8191）
     * @return 唯一的 64 位 ID
     */
    public long generateId(long shardId) {
        // 检查分片 ID 是否超出范围
        if (shardId < 0 || shardId >= (1L << SHARD_ID_BITS)) {
            throw new IllegalArgumentException("分片 ID 超出范围 (0-8191)");
        }

        long timestamp;
        long currentSequence;

        // 同步块，确保线程安全
        synchronized (this) {
            // 获取当前时间戳（自 EPOCH 以来的毫秒数）
            timestamp = System.currentTimeMillis() - EPOCH;

            // 检查时钟是否回退
            if (timestamp < lastTimestamp.get()) {
                throw new RuntimeException("时钟回退，无法生成 ID");
            }

            // 如果当前时间戳与上次相同，增加序列号
            if (timestamp == lastTimestamp.get()) {
                currentSequence = sequence.incrementAndGet() & MAX_SEQUENCE;
                // 如果序列号溢出（超过 1023），等待下一毫秒
                if (currentSequence == 0) {
                    timestamp = waitNextMillis(lastTimestamp.get());
                }
            } else {
                // 时间戳变化时，重置序列号
                sequence.set(0);
                currentSequence = 0;
            }

            // 更新最后时间戳
            lastTimestamp.set(timestamp);
        }

        // 组合时间戳、分片 ID 和序列号生成 64 位 ID
        return (timestamp << (SHARD_ID_BITS + SEQUENCE_BITS)) |  // 时间戳左移 23 位
               (shardId << SEQUENCE_BITS) |                      // 分片 ID 左移 10 位
               currentSequence;                                  // 添加序列号
    }

    /**
     * 等待直到下一毫秒。
     *
     * @param lastTimestamp 上次时间戳
     * @return 新的时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis() - EPOCH;
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis() - EPOCH;
        }
        return timestamp;
    }

    // 提取 ID 中的时间戳
    public static long getTimestamp(long id) {
        return (id >> (SHARD_ID_BITS + SEQUENCE_BITS)) + EPOCH;
    }

    // 提取 ID 中的分片 ID
    public static long getShardId(long id) {
        return (id >> SEQUENCE_BITS) & ((1L << SHARD_ID_BITS) - 1);
    }

    // 提取 ID 中的序列号
    public static long getSequence(long id) {
        return id & ((1L << SEQUENCE_BITS) - 1);
    }

    // 示例使用
    public static void main(String[] args) {
        InstagramIdGenerator generator = new InstagramIdGenerator();
        long shardId = 1341; // 示例分片 ID
        for (int i = 0; i < 5; i++) {
            long id = generator.generateId(shardId);
            System.out.printf("ID: %d, Timestamp: %d, Shard ID: %d, Sequence: %d%n",
                    id, getTimestamp(id), getShardId(id), getSequence(id));
        }
    }
}