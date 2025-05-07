package icu.congee.id.generator.uuid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
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
 *        &#064;copyright (c) 2025 ixiongdi. All rights reserved.
 */
public class UUIDv6Generator implements IdGenerator {
    // 格里高利历偏移量（从 1582-10-15 到 1970-01-01 的 100 纳秒间隔数）
    private static final long GREGORIAN_OFFSET = 122192928000000000L;
    private static final SecureRandom random = new SecureRandom();
    private static long lastTimestamp = 0L;
    private static int lastClockSeq = -1;

    /**
     * 生成 UUIDv6。
     * 
     * @return UUIDv6 实例
     */
    public synchronized static UUID next() {
        // 获取当前时间戳（60 位，100 纳秒单位）
        long timestamp = getCurrentTimestamp();

        // 处理时钟回拨
        if (timestamp < lastTimestamp) {
            timestamp = lastTimestamp + 1; // 时钟回拨时递增时间戳
        }
        lastTimestamp = timestamp;

        // 分割时间戳
        long timeHigh = (timestamp >>> 28) & 0xFFFFFFFFL; // 最显著的 32 位
        long timeMid = (timestamp >>> 12) & 0xFFFFL; // 接下来的 16 位
        long timeLow = timestamp & 0xFFFL; // 最不显著的 12 位

        // 版本号: 6 (0b0110)
        int version = 6;

        // 变种: 0b10
        int variant = 0b10;

        // 处理时钟序列（14位）
        int clockSeq;
        if (timestamp == lastTimestamp) {
            // 相同时间戳下，时钟序列自增
            clockSeq = (lastClockSeq + 1) & 0x3FFF;
            if (clockSeq == 0) {
                // 时钟序列达到最大值，等待下一个时间戳
                timestamp = tilNextMillis(lastTimestamp);
                clockSeq = random.nextInt() & 0x3FFF;
            }
        } else {
            // 不同时间戳，随机生成时钟序列
            clockSeq = random.nextInt() & 0x3FFF;
        }
        lastClockSeq = clockSeq;

        // 生成随机节点ID（48位）
        long randomNodeId = random.nextLong() & 0xFFFFFFFFFFFFL;

        // 组装高 64 位 (msb): time_high (32) | time_mid (16) | ver (4) | time_low (12)
        long msb = (timeHigh << 32) | (timeMid << 16) | (version << 12) | timeLow;

        // 组装低 64 位 (lsb): var (2) | clock_seq (14) | node (48)
        long lsb = ((long) variant << 62) | ((long) clockSeq << 48) | randomNodeId;

        return new UUID(msb, lsb);
    }

    /**
     * 获取当前时间戳，以 100 纳秒为单位，从 1582-10-15 00:00:00 UTC 开始。
     * 
     * @return 60 位时间戳
     */
    private static long getCurrentTimestamp() {
        Instant now = Instant.now();
        long seconds = now.getEpochSecond(); // 自 1970-01-01 00:00:00 UTC 起的秒数
        int nanos = now.getNano(); // 当前秒内的纳秒数
        long total_100ns = seconds * 10_000_000L + (nanos / 100); // 转换为 100 纳秒单位
        return total_100ns + GREGORIAN_OFFSET; // 加上格里高利历偏移量
    }

    /**
     * 获取 48 位节点 ID，优先使用 MAC 地址，否则使用随机值。
     * 
     * @return 48 位节点 ID
     */
    private static long getNodeId() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nic = interfaces.nextElement();
                byte[] mac = nic.getHardwareAddress();
                if (mac != null && mac.length == 6) {
                    long node = 0;
                    for (byte b : mac) {
                        node = (node << 8) | (b & 0xFF);
                    }
                    return node;
                }
            }
        } catch (Exception e) {
            // 如果获取 MAC 地址失败，则使用随机值
        }
        // 生成 48 位随机节点 ID
        return random.nextLong() & 0xFFFFFFFFFFFFL;
    }

    /**
     * 等待下一个毫秒级时间戳
     * 
     * @param lastTimestamp 上一次生成ID的时间戳
     * @return 下一个时间戳
     */
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    @Override
    public Object generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv6;
    }
}