package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class TtsIdPlus implements Id {
    // 40bit
    private long timestamp;

    // 20bit
    private int threadId;

    // 20bit
    private int sequence;

    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public byte[] toBytes() {
        // 分配10字节缓冲区：5字节timestamp + 2.5字节threadId + 2.5字节sequence
        ByteBuffer buffer = ByteBuffer.allocate(10);
        
        // 写入40位timestamp（只取低5字节）
        buffer.put((byte) (timestamp >>> 32));
        buffer.put((byte) (timestamp >>> 24));
        buffer.put((byte) (timestamp >>> 16));
        buffer.put((byte) (timestamp >>> 8));
        buffer.put((byte) timestamp);
        
        // 写入20位threadId（只取低2.5字节）
        buffer.put((byte) (threadId >>> 12));
        buffer.put((byte) (threadId >>> 4));
        // 高4位是threadId的最低4位，低4位是sequence的最高4位
        byte mixed = (byte) (((threadId & 0x0F) << 4) | ((sequence >>> 16) & 0x0F));
        buffer.put(mixed);
        
        // 写入剩余16位sequence（低2字节）
        buffer.put((byte) (sequence >>> 8));
        buffer.put((byte) sequence);
        
        return buffer.array();
    }

    @Override
    public long toLong() {
        // 由于TtsIdPlus总共80位，超过了long的64位，无法完全表示
        throw new UnsupportedOperationException("TtsIdPlus (80-bit) cannot be represented as a 64-bit long");
    }
}
