package icu.congee.uuid;

import java.net.NetworkInterface;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDv1Generator {
    // 单例实例
    private static final UUIDv1Generator INSTANCE = new UUIDv1Generator();
    
    // RFC 4122时间起点偏移量（1582-10-15至1970-01-01的100ns差值）
    private static final long START_EPOCH = 0x01B21DD213814000L;
    
    // 时钟序列和节点配置
    private final short clockSequence;
    private final byte[] nodeIdentifier;
    private final AtomicLong lastTimestamp = new AtomicLong(0);

    // 私有构造器
    private UUIDv1Generator() {
        this.clockSequence = generateClockSequence();
        this.nodeIdentifier = getNodeIdentifier();
    }

    public static UUIDv1Generator getInstance() {
        return INSTANCE;
    }

    // 核心生成方法（线程安全）
    public synchronized String generate() {
        long timestamp = (System.currentTimeMillis() * 10_000) + START_EPOCH;
        
        // 处理时间回拨
        if (timestamp <= lastTimestamp.get()) {
            timestamp = lastTimestamp.incrementAndGet();
        } else {
            lastTimestamp.set(timestamp);
        }

        // 构建UUID字节数组
        ByteBuffer buffer = ByteBuffer.allocate(16)
            .putLong((timestamp << 16) | 0x1000L) // 时间戳+版本号[1,8](@ref)
            .putShort((short) ((clockSequence & 0x3FFF) | 0x8000)) // 变体+时钟序列[1,4](@ref)
            .put(nodeIdentifier); // 48位节点标识[1,8](@ref)

        return formatUUID(buffer.array());
    }

    // 时钟序列生成（基于安全随机数）
    private short generateClockSequence() {
        return (short) new SecureRandom().nextInt(0x3FFF);
    }

    // 节点标识获取（MAC地址优先，失败时伪随机）
    private byte[] getNodeIdentifier() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                if (!network.isLoopback() && network.isUp()) {
                    byte[] mac = network.getHardwareAddress();
                    if (mac != null && mac.length == 6) {
                        mac[0] &= 0xFE; // 清除多播位[8](@ref)
                        return mac;
                    }
                }
            }
        } catch (Exception e) {
            byte[] node = new byte[6];
            new SecureRandom().nextBytes(node);
            node[0] |= 0x01; // 设置本地管理位[8](@ref)
            return node;
        }
        return new byte[6]; // Fallback
    }

    // 标准化UUID字符串格式
    private String formatUUID(byte[] bytes) {
        return String.format("%08x-%04x-%04x-%04x-%012x",
            ByteBuffer.wrap(bytes, 0, 4).getInt(),
            ByteBuffer.wrap(bytes, 4, 2).getShort(),
            ByteBuffer.wrap(bytes, 6, 2).getShort(),
            ByteBuffer.wrap(bytes, 8, 2).getShort(),
            ByteBuffer.wrap(bytes, 10, 6).getLong() & 0x0000FFFFFFFFFFFFL
        );
    }
}