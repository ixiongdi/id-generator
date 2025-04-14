package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class TtsIdPro implements Id {

    // 48bit
    private long timestamp;

    // 24bit
    private int threadId;

    // 24bit
    private int sequence;

    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public byte[] toBytes() {
        // 分配12字节缓冲区：6字节timestamp + 3字节threadId + 3字节sequence
        ByteBuffer buffer = ByteBuffer.allocate(12);

        // 写入48位timestamp（只取低6字节）
        buffer.put((byte) (timestamp >>> 40));
        buffer.put((byte) (timestamp >>> 32));
        buffer.put((byte) (timestamp >>> 24));
        buffer.put((byte) (timestamp >>> 16));
        buffer.put((byte) (timestamp >>> 8));
        buffer.put((byte) timestamp);

        // 写入24位threadId（只取低3字节）
        buffer.put((byte) (threadId >>> 16));
        buffer.put((byte) (threadId >>> 8));
        buffer.put((byte) threadId);

        // 写入24位sequence（只取低3字节）
        buffer.put((byte) (sequence >>> 16));
        buffer.put((byte) (sequence >>> 8));
        buffer.put((byte) sequence);

        return buffer.array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException();
    }
}
