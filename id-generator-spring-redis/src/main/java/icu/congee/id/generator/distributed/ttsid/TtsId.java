package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;
import java.time.Instant;

@AllArgsConstructor
public class TtsId implements Id {

    // 32bit
    private int timestamp;

    // 16bit
    private short threadId;

    // 16bit
    private short sequence;

    public static int currentTimestamp() {
        return (int) Instant.now().getEpochSecond();
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(8).putInt(timestamp).putShort(threadId).putShort(sequence).array();
    }

    @Override
    public long toLong() {
        return (long) timestamp << 32 | threadId << 16 | sequence;
    }
}
