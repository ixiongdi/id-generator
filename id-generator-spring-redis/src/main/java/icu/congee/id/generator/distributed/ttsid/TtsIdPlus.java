package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import icu.congee.id.generator.util.CrockfordBase32Encoder;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;
import java.util.HexFormat;

@AllArgsConstructor
@ToString
public class TtsIdPlus implements Id {
    // 44bit
    private long timestamp;

    // 20bit
    private int threadId;

    // 16bit
    private short sequence;

    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public byte[] toBytes() {
        // 分配10字节缓冲区：5.5字节timestamp + 2.5字节threadId + 2字节sequence
        ByteBuffer buffer = ByteBuffer.allocate(10);

        // 写入44位timestamp（5.5字节）
        buffer.put((byte) (timestamp >>> 36));
        buffer.put((byte) (timestamp >>> 28));
        buffer.put((byte) (timestamp >>> 20));
        buffer.put((byte) (timestamp >>> 12));
        buffer.put((byte) (timestamp >>> 4));
        // 高4位是timestamp的最低4位，低4位是threadId的最高4位
        byte mixed1 = (byte) (((timestamp & 0x0F) << 4) | ((threadId >>> 16) & 0x0F));
        buffer.put(mixed1);

        // 写入剩余16位threadId（2字节）
        buffer.put((byte) (threadId >>> 8));
        buffer.put((byte) threadId);

        // 写入16位sequence（2字节）
        buffer.put((byte) (sequence >>> 8));
        buffer.put((byte) sequence);

        return buffer.array();
    }

    @Override
    public long toLong() {
        // 由于TtsIdPlus总共80位(44位timestamp + 20位threadId + 16位sequence)，超过了long的64位，无法完全表示
        throw new UnsupportedOperationException("TtsIdPlus (80-bit) cannot be represented as a 64-bit long");
    }

    /**
     * 专门为80bit优化的算法
     */
    @Override
    public String toBase32() {
        return CrockfordBase32Encoder.encode80Bit(toBytes());
    }

    @Override
    public String toBase16() {
        return HexFormat.of().formatHex(toBytes());
    }
}
