package icu.congee.LexicalUUID;


import cn.hutool.core.util.HexUtil;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class LexicalUUID implements Comparable<LexicalUUID> {
    private final long timestamp;
    private final long workerID;
    private static final long defaultWorkerID;

    static {
        try {
            byte[] hostBytes = InetAddress.getLocalHost().getHostName().getBytes();
            defaultWorkerID = FNV1A.hash(hostBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize default worker ID", e);
        }
    }

    // 核心构造函数
    public LexicalUUID(long timestamp, long workerID) {
        this.timestamp = timestamp;
        this.workerID = workerID;
    }

    // 使用时钟和自定义workerID构造
    public LexicalUUID(Clock clock, long workerID) {
        this(clock.timestamp(), workerID);
    }

    // 使用时钟和默认workerID构造
    public LexicalUUID(Clock clock) {
        this(clock.timestamp(), defaultWorkerID);
    }

    // 从UUID字符串解析（工厂方法）
    public static LexicalUUID fromString(String uuid) {
        String clean = uuid.replace("-", "");
        try {
            byte[] bytes = HexUtil.decodeHex(clean.toCharArray());
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            return new LexicalUUID(buf.getLong(), buf.getLong());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID string", e);
        }
    }

    // 排序逻辑：先按时间戳，再按workerID
    @Override
    public int compareTo(LexicalUUID other) {
        int timeCompare = Long.compare(this.timestamp, other.timestamp);
        return (timeCompare != 0) ? timeCompare : Long.compare(this.workerID, other.workerID);
    }

    // 生成标准UUID格式字符串
    @Override
    public String toString() {
        String hexTimestamp = String.format("%016x", timestamp);
        return String.format("%s-%s-%s-%016x",
                hexTimestamp.substring(0, 8),
                hexTimestamp.substring(8, 12),
                hexTimestamp.substring(12, 16),
                workerID);
    }

    // 静态工厂方法（替代Scala的apply）
    public static LexicalUUID create(Clock clock) {
        return new LexicalUUID(clock);
    }

    // Getter方法
    public long getTimestamp() { return timestamp; }
    public long getWorkerID() { return workerID; }
}