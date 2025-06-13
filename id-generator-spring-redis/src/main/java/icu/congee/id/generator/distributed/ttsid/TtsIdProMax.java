package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.HexFormat;

public record TtsIdProMax(long timestamp, int threadId, int sequence) implements Id {
    public static long currentTimestamp() {
        Instant instant = Instant.now();
        return instant.getEpochSecond() * 1000_000_000L + instant.getNano();
    }

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(16).putLong(timestamp).putInt(threadId).putInt(sequence).array();
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
