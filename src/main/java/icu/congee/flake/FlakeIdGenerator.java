package icu.congee.flake;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class FlakeIdGenerator {
    // 各部分位数定义
    private static final int SEQUENCE_BITS = 16;
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long workerId; // 48位Worker ID
    private volatile long lastTimestamp = -1L;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public FlakeIdGenerator(long workerId) {
        this.workerId = workerId;
    }

    public synchronized byte[] nextId() {
        long currentTimestamp = System.currentTimeMillis();

        // 处理时间回拨问题
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }

        // 同一毫秒内序列递增
        int currentSequence;
        if (currentTimestamp == lastTimestamp) {
            currentSequence = sequence.incrementAndGet() & (int) MAX_SEQUENCE;
            if (currentSequence == 0) { // 序列耗尽则等待下一毫秒
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            sequence.set(0);
            currentSequence = 0;
        }

        lastTimestamp = currentTimestamp;

        // 组合128位ID
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(currentTimestamp); // 64位时间戳
        putWorkerId(buffer, workerId);    // 48位Worker ID
        buffer.putShort((short) currentSequence); // 16位序列号

        return buffer.array();
    }

    // 将48位Worker ID写入缓冲区
    private void putWorkerId(ByteBuffer buffer, long workerId) {
        for (int i = 5; i >= 0; i--) { // 取高48位中的6字节
            buffer.put((byte) ((workerId >> (i * 8)) & 0xFF));
        }
    }

    // 获取MAC地址生成Worker ID
    public static long generateWorkerId() throws SocketException {
        NetworkInterface network = getPhysicalNetworkInterface();
        byte[] mac = network.getHardwareAddress();
        long workerId = 0;
        for (byte b : mac) {
            workerId = (workerId << 8) | (b & 0xFF);
        }
        return workerId;
    }

    // 获取物理网卡地址
    private static NetworkInterface getPhysicalNetworkInterface() throws SocketException {
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            if (!ni.isLoopback() && !ni.isVirtual() && ni.getHardwareAddress() != null) {
                return ni;
            }
        }
        throw new SocketException("No physical network interface found");
    }

    // 等待下一毫秒
    private long waitNextMillis(long currentTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= currentTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    public static void main(String[] args) throws Exception {
        long workerId = generateWorkerId();
        FlakeIdGenerator generator = new FlakeIdGenerator(workerId);
        
        // 生成示例ID
        byte[] id = generator.nextId();
        System.out.println("Hex ID: " + bytesToHex(id));
    }

    // 字节数组转十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}