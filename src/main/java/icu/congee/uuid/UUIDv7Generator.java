package icu.congee.uuid;

import icu.congee.IdGenerator;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class UUIDv7Generator implements IdGenerator {
    // 时间戳掩码，用于提取低 48 位时间戳
    private static final long TIMESTAMP_MASK = 0xFFFFFFFFFFFFL;
    // 版本号 7 的二进制表示，占 4 位
    private static final long VERSION_7 = 0x7000L;
    // 12 位随机数的最大值（2^12 - 1）
    private static final int RANDOM_12_BITS_MAX = 4096;
    // 62 位随机数的掩码
    private static final long RANDOM_62_BITS_MASK = 0x3FFFFFFFFFFFFFFFL;
    // RFC 4122 标准的变体号掩码
    private static final long VARIANT_RFC_4122 = 0x8000000000000000L;

    /**
     * 生成自定义的 UUID v7 版本
     * 
     * UUID v7 的结构：
     * - 前 48 位：时间戳（毫秒级）
     * - 接下来 4 位：版本号（设置为 7）
     * - 接下来 12 位：随机数
     * - 后 62 位：随机数
     * - 变体号：设置为 RFC 4122 标准的变体号（10xx）
     * 
     * @return 生成的 UUID v7 实例
     */
    public static UUID next() {
        // 获取当前线程的 ThreadLocalRandom 实例，避免重复查找
        ThreadLocalRandom random = ThreadLocalRandom.current();
        // 获取当前时间戳（毫秒级）
        long timestamp = System.currentTimeMillis();
        // 提取时间戳的低 48 位，并左移 16 位
        long timestampPart = (timestamp & TIMESTAMP_MASK) << 16;
        // 生成 12 位随机数
        long random12Bits = random.nextInt(RANDOM_12_BITS_MAX);
        // 组合时间戳、版本号和 12 位随机数
        long mostSignificantBits = timestampPart | VERSION_7 | random12Bits;
        // 生成 62 位随机数
        long leastSignificantBits = random.nextLong() & RANDOM_62_BITS_MASK;
        // 设置变体号为 RFC 4122 标准的变体号（10xx）
        leastSignificantBits |= VARIANT_RFC_4122;

        return new UUID(mostSignificantBits, leastSignificantBits);
    }

    @Override
    public Object generate() {
        return next();
    }
}