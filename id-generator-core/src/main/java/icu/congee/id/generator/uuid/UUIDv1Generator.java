package icu.congee.id.generator.uuid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UUIDv1生成器 (基于时间的DCE安全实现)
 * <p>
 * 该类用于生成符合RFC 4122规范的UUIDv1。该版本包含60位时间戳（100ns精度）、
 * 14位时钟序列和48位节点ID，适用于需要时间排序的场景。
 * </p>
 * 
 * <p>UUIDv1结构规范：
 * <ul>
 *   <li>时间戳 - 60位 (从1582-10-15开始的100ns间隔)</li>
 *   <li>版本号 - 4位 (0001b)</li>
 *   <li>时钟序列 - 14位 (防止时间回拨冲突)</li>
 *   <li>节点ID - 48位 (MAC地址或随机生成)</li>
 *   <li>变体标识 - 2位 (RFC 4122规范)</li>
 * </ul>
 * </p>
 *
 * <p>平台兼容性说明：
 * <ul>
 *   <li>时间精度依赖系统时钟的毫秒级支持</li>
 *   <li>节点ID默认使用随机值，真实MAC地址需自行实现</li>
 *   <li>时钟序列使用线程本地存储保证并发安全</li>
 * </ul>
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class UUIDv1Generator implements IdGenerator {
    // 常量定义
    private static final long VERSION = 0x1000L;
    private static final long VARIANT = 0x8000000000000000L;
    private static final long TIMESTAMP_MASK = 0x0FFFFFFFFFFFFFFFL;
    
    // 线程本地时钟序列
    private static final ThreadLocal<Short> sequence = ThreadLocal.withInitial(
        () -> (short) ThreadLocalRandom.current().nextInt(0, 0x3FFF)
    );

    // 随机生成48位节点ID
    private static final long NODE_ID = ThreadLocalRandom.current().nextLong() & 0x0000FFFFFFFFFFFFL;

    /**
     * 生成符合RFC 4122的UUIDv1
     */
    public static UUID next() {
        // 获取当前时间戳（转换为100ns单位的格里高利历）
        long timestamp = (System.currentTimeMillis() * 10_000 + 0x01B21DD213814000L) & TIMESTAMP_MASK;
        
        // 构建最高有效位(MSB)
        long msb = (timestamp << 4) | VERSION;
        
        // 构建最低有效位(LSB)
        long lsb = VARIANT
                 | ((sequence.get() & 0x3FFFL) << 48)
                 | NODE_ID;

        return new UUID(msb, lsb);
    }

    @Override
    public String generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv1;
    }
}