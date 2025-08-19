package uno.xifan.id.generator.distributed.ttsid;

import uno.xifan.id.base.Id;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.HexFormat;

public record TtsIdPro(long timestamp, int threadId, short sequence) implements Id {

    // 微秒
    public static long currentTimestamp() {
        Instant now = Instant.now();
        return now.getEpochSecond() * 1_000_000 + now.getNano() / 1000;
    }

    @Override
    public byte[] toBytes() {
        // 分配12字节缓冲区：7字节timestamp + 3字节threadId + 2字节sequence
        ByteBuffer buffer = ByteBuffer.allocate(12);

        // 写入56位timestamp（只取低7字节）
        buffer.put((byte) (timestamp >>> 48));
        buffer.put((byte) (timestamp >>> 40));
        buffer.put((byte) (timestamp >>> 32));
        buffer.put((byte) (timestamp >>> 24));
        buffer.put((byte) (timestamp >>> 16));
        buffer.put((byte) (timestamp >>> 8));
        buffer.put((byte) timestamp);

        // 写入24位threadId（3字节）
        buffer.put((byte) (threadId >>> 16));
        buffer.put((byte) (threadId >>> 8));
        buffer.put((byte) threadId);

        // 写入16位sequence（2字节）
        buffer.put((byte) (sequence >>> 8));
        buffer.put((byte) sequence);

        return buffer.array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toBase16() {
        return HexFormat.of().formatHex(toBytes());
    }
}
