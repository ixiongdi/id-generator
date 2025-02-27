package icu.congee.uuid;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.util.Enumeration;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UUIDv1Generator {
    // 时间基准：1582-10-15 00:00:00 UTC（RFC 4122标准）
    private static final long START_EPOCH = 0x01B21DD213814000L;
    private static final AtomicLong lastTimestamp = new AtomicLong(0);
    private static final AtomicInteger clockSequence = new AtomicInteger(new SecureRandom().nextInt(0x3FFF));
    
    // 节点信息缓存（MAC地址或伪随机）
    private static final byte[] NODE_ID = new byte[6];
    static {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (!ni.isLoopback() && ni.getHardwareAddress() != null) {
                    byte[] mac = ni.getHardwareAddress();
                    System.arraycopy(mac, 0, NODE_ID, 0, 6);
                    break;
                }
            }
        } catch (Exception e) {
            new SecureRandom().nextBytes(NODE_ID); // 伪随机回退方案
        }
        NODE_ID[0] |= 0x01; // 设置组播位符合RFC规范
    }

    // 批量生成接口
    public static UUID[] generateBatch(int batchSize) {
        UUID[] uuids = new UUID[batchSize];
        long baseTime = getAdjustedTimestamp();
        for (int i = 0; i < batchSize; i++) {
            uuids[i] = generate(baseTime + i);
        }
        return uuids;
    }

    // 原子化时间戳处理
    private static synchronized long getAdjustedTimestamp() {
        long ts = (System.currentTimeMillis() * 10000) + (System.nanoTime() % 10000) + START_EPOCH;
        if (ts <= lastTimestamp.get()) {
            clockSequence.incrementAndGet();
            ts = lastTimestamp.incrementAndGet();
        } else {
            lastTimestamp.set(ts);
        }
        return ts;
    }

    // 单例生成核心
    private static UUID generate(long timestamp) {
        long msb = ((timestamp & 0xFFFFFFFFFFFF0000L) << 16) 
                 | ((timestamp & 0x0000000000000FFFL) << 48)
                 | 0x1000L; // 设置版本号1
        
        long lsb = ((clockSequence.get() & 0x3FFFL) << 48)
                 | 0x8000000000000000L // 设置变体号10b
                 | ((NODE_ID[0] & 0xFFL) << 40)
                 | ((NODE_ID[1] & 0xFFL) << 32)
                 | ((NODE_ID[2] & 0xFFL) << 24)
                 | ((NODE_ID[3] & 0xFFL) << 16)
                 | ((NODE_ID[4] & 0xFFL) << 8)
                 | (NODE_ID[5] & 0xFFL);
        
        return new UUID(msb, lsb);
    }

    // 测试用例
    public static void main(String[] args) {
        UUID uuid = generate(getAdjustedTimestamp());
        System.out.println("UUIDv1: " + uuid);
        System.out.println("Version: " + uuid.version()); // 应输出7
        System.out.println("Variant: " + uuid.variant()); // 应输出2
    }
}