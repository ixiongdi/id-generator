package icu.congee.id.generator.distributed.uuid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;

@AllArgsConstructor
@ToString
public class UUID implements Id {

    long timestamp;
    long clockSeq;
    long node;

    @Override
    public byte[] toBytes() {
        // 分割时间戳
        long timeHigh = (timestamp >>> 28) & 0xFFFFFFFFL; // 最显著的 32 位
        long timeMid = (timestamp >>> 12) & 0xFFFFL; // 接下来的 16 位
        long timeLow = timestamp & 0xFFFL; // 最不显著的 12 位

        // 版本号: 8 (0b0110)
        int ver = 8;

        // 变种: 0b10
        long var = 0b10;
        return ByteBuffer.allocate(16).putLong(timeHigh << 32 | timeMid << 16 | ver << 12 | timeLow).putLong(var << 62 | clockSeq << 48 | node).array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException();
    }

    public java.util.UUID toUUID() {
        // 分割时间戳
        long timeHigh = (timestamp >>> 28) & 0xFFFFFFFFL; // 最显著的 32 位
        long timeMid = (timestamp >>> 12) & 0xFFFFL; // 接下来的 16 位
        long timeLow = timestamp & 0xFFFL; // 最不显著的 12 位

        // 版本号: 8 (0b0110)
        int ver = 8;

        // 变种: 0b10
        long var = 0b10;
        return new java.util.UUID(timeHigh << 32 | timeMid << 16 | ver << 12 | timeLow, var << 62 | clockSeq << 48 | node);
    }
}
