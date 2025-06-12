package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.Id;
import icu.congee.id.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HexFormat;

@AllArgsConstructor
@ToString
public class TtsIdMini implements Id {

    // 31bit
    private long timestamp;

    // 10bit
    private long threadId;

    // 12bit
    private long sequence;

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
