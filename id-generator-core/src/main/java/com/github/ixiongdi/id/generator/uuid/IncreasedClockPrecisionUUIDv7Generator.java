package com.github.ixiongdi.id.generator.uuid;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 在标准UUID v7上把rand_a的12bit作为更高精度的时钟，时间精度大约为250ns
 */
public class IncreasedClockPrecisionUUIDv7Generator {
    public static UUID next() {
        Instant instant = Instant.now();
        int clockSeq = (int) ((instant.getNano() / 1000_000f) % 1 * 4096);
        long msb = instant.toEpochMilli() << 16 | 0x7000 | clockSeq;
        long lsb = 0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;
        return new UUID(msb, lsb);
    }
}
