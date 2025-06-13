package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import icu.congee.id.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HexFormat;

public record TtsIdMini(long timestamp, long threadId, long sequence) implements Id {

    public static long currentTimestamp() {
        return TimeUtils.getCurrentUnixSeconds();
    }

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        // 31位timestamp, 10位threadId, 12位sequence
        return timestamp << 22 | threadId << 12 | sequence;
    }

    @Override
    public String toBase16() {
        return HexFormat.of().formatHex(toBytes());
    }
}
