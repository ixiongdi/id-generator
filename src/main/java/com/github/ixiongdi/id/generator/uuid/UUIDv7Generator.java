package com.github.ixiongdi.id.generator.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class UUIDv7Generator {
    public static UUID next() {
        long msb = System.currentTimeMillis() << 16 | 0x7000 | ThreadLocalRandom.current().nextInt() & 0xFFF;
        long lsb = 0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;
        return new UUID(msb, lsb);
    }
}



