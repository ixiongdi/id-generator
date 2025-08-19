package uno.xifan.id.generator.uuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.net.NetworkInterface;
import java.time.Instant;
import java.security.SecureRandom;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UUIDv1生成器 (基于时间的DCE安全实现)
 * 
 * 该类用于生成符合RFC 4122规范的UUIDv1。该版本包含60位时间戳（100ns精度）、
 * 14位时钟序列和48位节点ID，适用于需要时间排序的场景。
 * 
 * UUIDv1结构规范：
 * <ul>
 * <li>time_low - 32位 (时间戳的低32位)</li>
 * <li>time_mid - 16位 (时间戳的中间16位)</li>
 * <li>version - 4位 (0001b)</li>
 * <li>time_high - 12位 (时间戳的高12位)</li>
 * <li>variant - 2位 (RFC 4122规范，10b)</li>
 * <li>clock_seq - 14位 (时钟序列)</li>
 * <li>node - 48位 (MAC地址或随机生成)</li>
 * </ul>
 * 
 * 平台兼容性说明：
 * <ul>
 * <li>时间精度基于系统时钟，使用100ns精度的时间戳</li>
 * <li>节点ID优先使用系统MAC地址，无法获取时使用随机值</li>
 * <li>时钟序列使用原子计数器保证并发安全</li>
 * <li>支持时钟回拨场景的处理</li>
 * </ul>
 *
 * @author ixiongdi
 * @version 1.2
 * @since 2024-05-01
 */
public class UUIDv1Generator implements IdGenerator {
    // 基准时间常量（1582-10-15 00:00:00 UTC到1970-01-01的秒数差）
    private static final long START_EPOCH_SECONDS = -12219292800L;
    private static final int NANOS_PER_100NS = 100;

    // 线程安全的状态管理
    private static volatile long lastTimestamp = 0;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static volatile int clockSequence = SECURE_RANDOM.nextInt() & 0x3FFF; // 14位时钟序列
    private static final long NODE_ID = initializeNodeId();
    // 移除不再需要的全局锁对象

    /**
     * 生成UUIDv1主方法
     */
    public static synchronized UUID next() {
        long timestamp = getPreciseTimestamp();
        int sequence = updateClockSequence(timestamp);
        return constructUUID(timestamp, sequence, NODE_ID);
    }

    /**
     * 获取精确时间戳（100纳秒单位）
     */
    private static long getPreciseTimestamp() {
        Instant now = Instant.now();
        long seconds = now.getEpochSecond() - START_EPOCH_SECONDS;
        int nanos = now.getNano();
        return (seconds * 10_000_000) + (nanos / NANOS_PER_100NS);
    }

    /**
     * 管理时钟序列（处理时间回退）
     */
    private static synchronized int updateClockSequence(long currentTimestamp) {
        if (currentTimestamp > lastTimestamp) {
            lastTimestamp = currentTimestamp;
            return clockSequence;
        } else if (currentTimestamp < lastTimestamp) {
            clockSequence = SECURE_RANDOM.nextInt() & 0x3FFF;
            return clockSequence;
        } else {
            // 时间相同则递增序列
            clockSequence = (clockSequence + 1) & 0x3FFF;
            return clockSequence;
        }
    }

    /**
     * 初始化节点ID（优先MAC地址，否则随机生成）
     */
    private static long initializeNodeId() {
        byte[] nodeBytes = new byte[6];
        try {
            NetworkInterface network = NetworkInterface.getNetworkInterfaces().nextElement();
            if (network != null) {
                byte[] mac = network.getHardwareAddress();
                if (mac != null && mac.length >= 6) {
                    System.arraycopy(mac, 0, nodeBytes, 0, 6);
                    nodeBytes[0] |= 0x01; // 设置多播位
                    return bytesToLong(nodeBytes);
                }
            }
        } catch (Exception ignored) {
            // 忽略异常，使用随机节点
        }

        new SecureRandom().nextBytes(nodeBytes);
        nodeBytes[0] |= 0x01; // 强制设置多播位
        return bytesToLong(nodeBytes);
    }

    /**
     * 将6字节数组转换为long（48位有效数据）
     */
    private static long bytesToLong(byte[] bytes) {
        if (bytes.length != 6)
            throw new IllegalArgumentException("需要6字节数组");
        return ((bytes[0] & 0xFFL) << 40)
                | ((bytes[1] & 0xFFL) << 32)
                | ((bytes[2] & 0xFFL) << 24)
                | ((bytes[3] & 0xFFL) << 16)
                | ((bytes[4] & 0xFFL) << 8)
                | (bytes[5] & 0xFFL);
    }

    /**
     * 构建UUID字段
     */
    private static UUID constructUUID(long timestamp, int clockSeq, long nodeId) {
        // 拆分时间戳字段
        long timeLow = timestamp & 0xFFFFFFFFL;
        long timeMid = (timestamp >>> 32) & 0xFFFFL;
        long timeHigh = (timestamp >>> 48) & 0x0FFFL;

        // 高位组合（版本号1）
        long msb = (timeLow << 32) | (timeMid << 16) | (timeHigh | 0x1000L);

        // 低位组合（变体RFC 4122）
        long lsb = ((0x8000L | (clockSeq & 0x3FFFL)) << 48) | (nodeId & 0xFFFFFFFFFFFFL);

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