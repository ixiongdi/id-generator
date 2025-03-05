package com.github.ixiongdi.id.generator.lexical;

import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * 一个128位的UUID，由64位时间戳和64位工作节点ID组成
 */
public class LexicalUUID implements Comparable<LexicalUUID> {
    private static final long defaultWorkerID;

    static {
        try {
            defaultWorkerID = FNV1A.hash(InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize default worker ID", e);
        }
    }

    private final long timestamp;
    private final long workerID;

    /**
     * 使用指定的时间戳和工作节点ID创建LexicalUUID
     */
    public LexicalUUID(long timestamp, long workerID) {
        this.timestamp = timestamp;
        this.workerID = workerID;
    }

    /**
     * 使用指定的时钟和工作节点ID创建LexicalUUID
     */
    public LexicalUUID(Clock clock, long workerID) {
        this(clock.timestamp(), workerID);
    }

    /**
     * 使用指定的时钟和默认的工作节点ID创建LexicalUUID
     */
    public LexicalUUID(Clock clock) {
        this(clock.timestamp(), defaultWorkerID);
    }

    /**
     * 从UUID字符串创建LexicalUUID
     */
    public static LexicalUUID fromString(String uuid) {
        String hexString = uuid.replace("-", "");
        try {
            byte[] bytes = decodeHex(hexString);
            ByteBuffer buf = ByteBuffer.wrap(bytes);
            return new LexicalUUID(buf.getLong(), buf.getLong());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid UUID string: " + uuid, e);
        }
    }

    /**
     * 将十六进制字符串解码为字节数组
     */
    private static byte[] decodeHex(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    | Character.digit(hex.charAt(i + 1), 16));
        }

        return data;
    }

    /**
     * 使用指定的时钟生成新的LexicalUUID
     */
    public static LexicalUUID generate(Clock clock) {
        return new LexicalUUID(clock, defaultWorkerID);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public long getWorkerID() {
        return workerID;
    }

    @Override
    public int compareTo(LexicalUUID that) {
        int res = Long.compare(this.timestamp, that.timestamp);
        return res != 0 ? res : Long.compare(this.workerID, that.workerID);
    }

    @Override
    public String toString() {
        String hex = String.format("%016x", timestamp);
        return String.format("%s-%s-%s-%016x",
                hex.substring(0, 8),
                hex.substring(8, 12),
                hex.substring(12, 16),
                workerID);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LexicalUUID that = (LexicalUUID) o;
        return timestamp == that.timestamp && workerID == that.workerID;
    }

    @Override
    public int hashCode() {
        return 31 * Long.hashCode(timestamp) + Long.hashCode(workerID);
    }
}