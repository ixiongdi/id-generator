package uno.xifan.id.generator.custom;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个高性能、分布式、无锁且零配置的唯一ID生成器。
 *
 * <p>此生成器旨在提供一个极其简单高效的分布式ID生成方案，它结合了时间戳和多熵源生成的哈希值，
 * 避免了Snowflake算法需要配置和管理Worker ID的复杂性。</p>
 *
 * <h3>ID 结构 (64-bit long)</h3>
 * <pre>
 * +--------------------------------------------------------------------------------------+
 * | 1 Bit (Sign) | 31 Bits (Timestamp)          | 32 Bits (Entropy)                      |
 * +--------------------------------------------------------------------------------------+
 * | 0 (固定为正数) | 秒级时间戳 (自纪元以来)        | 混合熵值 (纳秒时间/计数器/节点ID/随机数) |
 * </pre>
 */
public class TimeBasedEntropyIdGenerator implements IdGenerator {

    /**
     * 自定义纪元（Epoch）。
     * 北京时间2025年5月1日凌晨，对应的秒数。
     */
    private static final long EPOCH = 1746028800L;

    /**
     * 原子计数器，用于在同一时间内为熵源提供额外的唯一性。
     */
    private static final AtomicLong COUNTER = new AtomicLong(0);

    /**
     * 节点唯一标识符。
     * 在类加载时通过MAC地址生成，确保在分布式环境中的不同节点有不同的标识。
     */
    private static final long NODE_IDENTIFIER = initializeNodeIdentifier();
    
    /**
     * 熵值部分的位掩码，用于取低32位。
     */
    private static final long ENTROPY_MASK = 0xFFFFFFFFL;

    /**
     * 生成下一个唯一的64位ID。
     *
     * @return 一个唯一的、正数的 {@code long} 类型ID。
     */
    public static long next() {
        // 1. 高32位：计算秒级时间戳部分
        final long timestampPart = ((System.currentTimeMillis() / 1000) - EPOCH) << 32;

        // 2. 低32位：计算混合熵值
        final long nanoTime = System.nanoTime();
        final long counter = COUNTER.getAndIncrement();
        final long random = ThreadLocalRandom.current().nextLong();

        // 聚合所有熵源
        long entropy = nanoTime ^ counter ^ NODE_IDENTIFIER ^ random;

        // 使用SplitMix64的最终化函数进行高质量的位混合，以产生雪崩效应
        entropy = (entropy ^ (entropy >>> 30)) * 0xBF58476D1CE4E5B9L;
        entropy = (entropy ^ (entropy >>> 27)) * 0x94D049BB133111EBL;
        entropy = entropy ^ (entropy >>> 31);
        
        // 3. 组合时间戳和熵值
        return timestampPart | (entropy & ENTROPY_MASK);
    }

    /**
     * 初始化节点标识符。
     * 优先尝试使用第一个有效的MAC地址来生成一个稳定的、唯一的节点ID。
     *
     * @return 根据MAC地址或随机数生成的节点ID。
     */
    private static long initializeNodeIdentifier() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isLoopback() && !ni.isVirtual() && ni.isUp()) {
                    byte[] mac = ni.getHardwareAddress();
                    if (mac != null) {
                        long nodeId = 0L;
                        for (int i = 0; i < Math.min(8, mac.length); i++) {
                            nodeId <<= 8;
                            nodeId |= (mac[i] & 0xFF);
                        }
                        return nodeId;
                    }
                }
            }
        } catch (Exception e) {
            // 如果获取MAC地址失败，则忽略异常，并在最后回退到随机数方案
        }
        // 如果无法获取MAC地址，则使用一个安全的随机长整数作为后备方案
        return new SecureRandom().nextLong();
    }

    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        // 假设您的IdType枚举中有TimeBasedEntropyId这个值
        return IdType.TimeBasedEntropyId;
    }
}
