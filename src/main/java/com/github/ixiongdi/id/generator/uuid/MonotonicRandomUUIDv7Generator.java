package com.github.ixiongdi.id.generator.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 在标准UUID v7上把rand_b的62bit作为计数器，随机单调递增
 */
public class MonotonicRandomUUIDv7Generator {

    private static final LongAdder counter = new LongAdder();
    private static final AtomicLong lastTm = new AtomicLong(System.currentTimeMillis());

    public static UUID next(long add) {
        long currTm = System.currentTimeMillis() / 1000;
        if (currTm > lastTm.get()) {
            lastTm.set(currTm);
            counter.reset();
        }
        long msb = System.currentTimeMillis() << 16 | 0x7000 | System.nanoTime() & 0xFFF;
        long lsb = 0x8000000000000000L | counter.sum() & 0x3FFFFFFFFFFFFFFFL;
        counter.add(add);
        return new UUID(msb, lsb);
    }

    public static UUID next() {
        return next(false);
    }

    public static UUID next(boolean unguessability) {
        if (unguessability) {
            return next(ThreadLocalRandom.current().nextLong());
        } else {
            return next(1);
        }
    }
}
