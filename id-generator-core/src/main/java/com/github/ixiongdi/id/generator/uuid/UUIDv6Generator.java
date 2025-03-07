package com.github.ixiongdi.id.generator.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UUIDv6生成器
 * <p>
 * 该类用于生成符合UUIDv6规范的UUID。UUIDv6是一种基于时间的UUID版本，
 * 它是对UUIDv1的改进，将时间字段重新排序以提供更好的排序特性，同时保持唯一性保证。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class UUIDv6Generator {
    // 常量定义，用于位掩码和版本/变体的标识
    /** 格里高利时间戳掩码，占用 60 位 */
    private static final long GREGORIAN_TIMESTAMP_MASK = 0xFFFFFFFFFFFFFL;

    /** UUID 版本 6 的标识符 */
    private static final long VERSION_IDENTIFIER = 0x6000L;

    /** UUID 变体 2 的标识符（RFC 4122规范） */
    private static final long VARIANT_IDENTIFIER = 0x8000000000000000L;

    /** 时钟序列掩码，占用 14 位 */
    private static final long CLOCK_SEQUENCE_MASK = 0x3FFF;

    /** 节点ID掩码，占用 48 位 */
    private static final long NODE_ID_MASK = 0xFFFFFFFFFFFFL;

    // 线程本地的序列生成器，用于确保线程间的唯一性
    private static final ThreadLocal<ThreadLocalSequence> threadLocalSequence = ThreadLocal
            .withInitial(ThreadLocalSequence::new);

    // 节点ID，默认使用随机生成的值
    private static final long NODE_ID = ThreadLocalRandom.current().nextLong() & NODE_ID_MASK;

    /**
     * 生成一个新的UUIDv6
     * <p>
     * 该方法创建并返回一个新的UUIDv6实例，其中包含基于当前时间的时间戳、时钟序列和节点ID。
     * UUIDv6的结构如下：
     * - 最高有效位(MSB)：60位时间戳 + 4位版本号(6)
     * - 最低有效位(LSB)：2位变体标识 + 14位时钟序列 + 48位节点ID
     * </p>
     *
     * @return 新生成的UUIDv6实例
     */
    public static UUID next() {
        // 获取当前线程的序列
        ThreadLocalSequence seq = threadLocalSequence.get();

        // 获取当前时间戳（毫秒）并转换为100纳秒为单位的格里高利时间戳
        // 从1582-10-15 00:00:00开始计算（格里高利历元）
        // 0x01B21DD213814000L是从1582-10-15到1970-01-01的100纳秒数
        long timestamp = (System.currentTimeMillis() * 10000 + 0x01B21DD213814000L) & GREGORIAN_TIMESTAMP_MASK;

        // 获取时钟序列，每次调用递增
        long clockSequence = seq.sequence++ & CLOCK_SEQUENCE_MASK;

        // 构建最高有效位(MSB)
        // 将时间戳放在高60位，版本号(6)放在低4位
        long msb = (timestamp << 4) | VERSION_IDENTIFIER;

        // 构建最低有效位(LSB)
        // 变体位(2位) + 时钟序列(14位) + 节点ID(48位)
        long lsb = VARIANT_IDENTIFIER | (clockSequence << 48) | NODE_ID;

        // 使用构建好的MSB和LSB创建并返回一个新的UUID实例
        return new UUID(msb, lsb);
    }

    /** 线程本地序列持有者。每个线程拥有独立的序列号，以避免线程间的竞争。 */
    private static class ThreadLocalSequence {
        /** 序列号初始值，使用随机值以减少冲突可能性 */
        long sequence = ThreadLocalRandom.current().nextLong() & CLOCK_SEQUENCE_MASK;
    }
}