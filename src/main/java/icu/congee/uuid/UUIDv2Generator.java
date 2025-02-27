package icu.congee.uuid;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDv2Generator {
    // 时间基准（1582-10-15 00:00:00 至 1970-01-01 的毫秒差）
    private static final long START_EPOCH = 0x01B21DD213814000L;
    private static final AtomicLong lastTimestamp = new AtomicLong();
    private static final AtomicInteger clockSeq = new AtomicInteger(new SecureRandom().nextInt(0x3FFF));
    
    // 模拟 POSIX 参数（需根据实际业务设置）
    private static final int DOMAIN = 0x200;   // DCE 安全域标识
    private static final int UID = 1000;       // 模拟用户 ID
    private static final int GID = 1001;       // 模拟组 ID
    
    // 节点信息（MAC 地址或随机数）
    private static final byte[] NODE_ID = loadNodeId();

    // 核心生成逻辑（线程安全）
    public static UUID generate() {
        long timestamp = getAdjustedTimestamp();
        
        // 构造高位（MSB）
        long msb = ((timestamp << 2) & 0xFFFFFFFF00000000L)  // 时间戳低32位
                 | ((timestamp >> 30) & 0x00000000FFFF0000L) // 时间戳中16位
                 | 0x2000L                                   // 版本号 0x2
                 | ((timestamp >> 48) & 0x0000000000000FFFL);// 时间戳高12位
        msb |= ((UID & 0xFFFFL) << 32) | ((GID & 0xFFFFL) << 16);

        // 构造低位（LSB）
        long lsb = (0x8000L << 48)                          // 变体号 10b
                 | ((clockSeq.get() & 0x3FFFL) << 48)
                 | ((NODE_ID[0] & 0xFFL) << 40)
                 | ((NODE_ID[1] & 0xFFL) << 32)
                 | ((NODE_ID[2] & 0xFFL) << 24)
                 | ((NODE_ID[3] & 0xFFL) << 16)
                 | ((NODE_ID[4] & 0xFFL) << 8)
                 | (NODE_ID[5] & 0xFFL);

        return new UUID(msb, lsb);
    }

    // 时间戳生成（无锁 CAS 算法）
    private static long getAdjustedTimestamp() {
        long currentTime = (System.currentTimeMillis() * 10_000) + 
                          ((System.nanoTime() % 1_000_000) / 100) + START_EPOCH;
        while (true) {
            long prev = lastTimestamp.get();
            if (currentTime > prev && lastTimestamp.compareAndSet(prev, currentTime)) {
                return currentTime;
            }
            clockSeq.incrementAndGet(); // 时钟回拨时递增序列
        }
    }

    // 获取 MAC 地址（纯 Java 实现）
    private static byte[] loadNodeId() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isLoopback() && ni.getHardwareAddress() != null) {
                    byte[] mac = ni.getHardwareAddress();
                    mac[0] |= 0x01; // 设置组播位
                    return mac;
                }
            }
        } catch (Exception e) {
            byte[] randomNode = new byte[6];
            new SecureRandom().nextBytes(randomNode); // 隐私保护回退
            return randomNode;
        }
        return new byte[6]; // 默认返回空节点
    }

    // 测试用例
    public static void main(String[] args) {
        UUID uuid = generate();
        System.out.println("UUIDv2: " + uuid);
        System.out.println("Version: " + uuid.version()); // 应输出7
        System.out.println("Variant: " + uuid.variant()); // 应输出2
    }
}