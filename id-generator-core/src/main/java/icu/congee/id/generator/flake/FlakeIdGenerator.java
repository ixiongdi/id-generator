package icu.congee.id.generator.flake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class FlakeIdGenerator implements IdGenerator {
    private static final long EPOCH = 1609459200000L; // 自定义起始时间戳（2021-01-01 00:00:00 UTC）
    private static final int WORKER_ID_BITS = 48; // 工作节点ID位数
    private static final int SEQUENCE_BITS = 16; // 序列号位数
    private static final int MAX_WORKER_ID = -1 ^ (-1 << WORKER_ID_BITS); // 最大工作节点ID
    private static final int MAX_SEQUENCE = -1 ^ (-1 << SEQUENCE_BITS); // 最大序列号
    private static final  FlakeIdGenerator INSTANCE = new FlakeIdGenerator();
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public FlakeIdGenerator() {
        this.workerId = getWorkerId();
    }

    public static void main(String[] args) {
        FlakeIdGenerator generator = new FlakeIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.generateFlakeId());
        }
    }

    // 获取工作节点ID（基于MAC地址）
    private long getWorkerId() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                if (networkInterface.isUp() && !networkInterface.isLoopback()) {
                    byte[] macBytes = networkInterface.getHardwareAddress();
                    if (macBytes != null) {
                        long workerId = 0;
                        for (byte b : macBytes) {
                            workerId = (workerId << 8) | (b & 0xFF);
                        }
                        workerId = workerId & MAX_WORKER_ID;
                        return workerId;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return 0; // 默认工作节点ID
    }

    // 生成Flake ID
    public synchronized long generateFlakeId() {
        long currentTimestamp = System.currentTimeMillis();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常");
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << (WORKER_ID_BITS + SEQUENCE_BITS))
                | (workerId << SEQUENCE_BITS)
                | sequence;
    }

    // 等待下一毫秒
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }

    @Override
    public Long generate() {
        return INSTANCE.generateFlakeId();
    }

    @Override
    public IdType idType() {
        return IdType.Flake;
    }
}