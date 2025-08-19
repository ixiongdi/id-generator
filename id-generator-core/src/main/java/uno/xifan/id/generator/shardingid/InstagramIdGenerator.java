package uno.xifan.id.generator.shardingid;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Instagram风格的分布式ID生成器
 * <p>
 * 该生成器基于Instagram的Snowflake ID生成算法的变体实现，用于生成分布式环境下的唯一标识符。
 * ID由以下部分组成：
 * - 41位时间戳（毫秒级，以自定义epoch为基准）
 * - 13位分片ID（最多支持8192个分片）
 * - 10位序列号（每毫秒最多生成1024个ID）
 * </p>
 * 
 * @author ixiongdi
 * @since 1.0
 */
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

    /**
     * 构造一个Instagram风格的ID生成器
     *
     * @param shardId 分片ID，用于标识不同的生成器实例，取值范围[0, 8191]
     * @throws IllegalArgumentException 当分片ID超出有效范围时抛出
     */
    public InstagramIdGenerator(int shardId) {
        if (shardId < 0 || shardId > MAX_SHARD_ID) {
            throw new IllegalArgumentException("Shard ID exceeds its bit limit");
        }
        this.shardId = shardId;
    }

    /**
     * 生成一个唯一的分布式ID
     * <p>
     * 该方法是线程安全的，使用synchronized关键字确保并发安全。
     * 当检测到时钟回拨时，会抛出异常以确保ID的单调递增性。
     * </p>
     *
     * @return 生成的唯一ID
     * @throws RuntimeException 当检测到时钟回拨时抛出
     */
    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            // 如果当前时间戳小于上次的时间戳，说明发生了时间回拨，等待直到时间追上
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for "
                    + (lastTimestamp - currentTimestamp) + " milliseconds");
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

    /**
     * 获取当前时间戳
     *
     * @return 当前时间的毫秒级时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一个毫秒
     *
     * @param lastTimestamp 上一次生成ID时的时间戳
     * @return 新的时间戳
     */
    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * 测试方法，演示ID生成器的使用方式
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        InstagramIdGenerator idGenerator = new InstagramIdGenerator(5); // 设置分片ID为5
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.generateId();
            System.out.println("Generated ID: " + id);
            System.out.println("Decomposed ID: " + decomposeId(id));
        }
    }

    /**
     * 分解ID，用于调试和测试
     *
     * @param id 要分解的ID
     * @return 包含时间戳、分片ID和序列号的字符串表示
     */
    private static String decomposeId(long id) {
        long timestamp = (id >> TIMESTAMP_LEFT_SHIFT) + CUSTOM_EPOCH;
        int shardId = (int) ((id >> SHARD_ID_LEFT_SHIFT) & ((1 << SHARD_ID_BITS) - 1));
        int sequence = (int) (id & ((1 << SEQUENCE_BITS) - 1));
        return "Timestamp: " + timestamp + ", Shard ID: " + shardId + ", Sequence: " + sequence;
    }
}