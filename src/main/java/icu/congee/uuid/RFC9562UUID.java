package icu.congee.uuid;

import java.net.NetworkInterface;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicLong;

public class RFC9562UUID {

    // Gregorian epoch offset (1582-10-15 00:00:00) to Unix epoch (1970-01-01 00:00:00)
    private static final long GREGORIAN_OFFSET = 122192928000000000L;
    private static final AtomicLong lastTimestamp = new AtomicLong(0);
    private static volatile int clockSequence = initializeClockSequence();
    private static final byte[] nodeId = initializeNodeId();

    // 基类方法
    protected static byte[] getBytesFromLong(long value, int bytes) {
        byte[] result = new byte[bytes];
        for (int i = bytes - 1; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    // UUIDv1实现
    public static java.util.UUID generateV1() {
        long timestamp = getGregorianTimestamp();
        long sequenceAndNode = ((long) clockSequence & 0x3FFF) << 48 | getNodeIdLong();

        // 组合UUID字段
        long msb = ((timestamp & 0x0FFF000000000000L) << 4)  // time_high (12 bits)
                 | ((timestamp & 0xFFFF00000000L) << 20)      // time_mid (16 bits)
                 | ((timestamp & 0xFFFFFFFFL) << 32)          // time_low (32 bits)
                 | (1L << 12);                                // version (4 bits)

        long lsb = (sequenceAndNode & 0x3FFFFFFFFFFFFFFFL)    // clock_seq + node
                 | (0x2L << 62);                              // variant (2 bits)

        return new java.util.UUID(msb, lsb);
    }

    private static synchronized long getGregorianTimestamp() {
        long current = (System.currentTimeMillis() * 10000) + GREGORIAN_OFFSET + 
                      (System.nanoTime() % 10000) / 1000;

        // 处理时间回退和序列号
        while (true) {
            long last = lastTimestamp.get();
            if (current > last) {
                if (lastTimestamp.compareAndSet(last, current)) {
                    break;
                }
            } else {
                clockSequence = (clockSequence + 1) & 0x3FFF;
                current = last + 1;
                if (lastTimestamp.compareAndSet(last, current)) {
                    break;
                }
            }
        }
        return current;
    }

    private static int initializeClockSequence() {
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.nextInt(0x3FFF + 1);
    }

    private static byte[] initializeNodeId() {
        byte[] mac = getMacAddress();
        if (mac == null) {
            mac = new byte[6];
            new SecureRandom().nextBytes(mac);
            mac[0] |= 0x01;  // 设置多播位
        }
        return mac;
    }

    private static long getNodeIdLong() {
        return ((long) (nodeId[0] & 0xFF) << 40)
             | ((long) (nodeId[1] & 0xFF) << 32)
             | ((long) (nodeId[2] & 0xFF) << 24)
             | ((long) (nodeId[3] & 0xFF) << 16)
             | ((long) (nodeId[4] & 0xFF) << 8)
             | (nodeId[5] & 0xFF);
    }

    private static byte[] getMacAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface network = interfaces.nextElement();
                byte[] mac = network.getHardwareAddress();
                if (mac != null && mac.length == 6 && !isZeroMac(mac)) {
                    return mac;
                }
            }
        } catch (Exception e) {
            // 处理异常
        }
        return null;
    }

    private static boolean isZeroMac(byte[] mac) {
        for (byte b : mac) {
            if (b != 0) return false;
        }
        return true;
    }

    public static void main(String[] args) {
        java.util.UUID uuid = generateV1();
        System.out.println("Generated UUIDv1: " + uuid);
        System.out.println("Version: " + uuid.version());
        System.out.println("Variant: " + uuid.variant());
    }
}