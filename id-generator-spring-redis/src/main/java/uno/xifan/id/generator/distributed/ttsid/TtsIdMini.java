package uno.xifan.id.generator.distributed.ttsid;

import uno.xifan.id.base.Id;
import uno.xifan.id.util.TimeUtils;
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
