package uno.xifan.id.generator.distributed.ttsid;

import uno.xifan.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.util.HexFormat;

public record TtsId(long timestamp, long threadId, long sequence) implements Id {

    public static long currentTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        // 41位timestamp, 10位threadId, 12位sequence
        return timestamp << 22 | threadId << 12 | sequence;
    }

    @Override
    public String toBase16() {
        return HexFormat.of().formatHex(toBytes());
    }
}
