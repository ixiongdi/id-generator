package icu.congee.id.generator.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 在标准UUID v7上把rand_a的12bit作为固定位长的专用计数器，最大支持4096个，可统计毫秒内的生成的UUID总数
 */
public class DedicatedCounterUUIDv7Generator {
    private static final LongAdder counter = new LongAdder();
    private static final AtomicLong lastTm = new AtomicLong(System.currentTimeMillis());

    public static UUID next() {
        long currTm = System.currentTimeMillis();
        if (currTm > lastTm.get()) {
            lastTm.set(currTm);
            counter.reset();
        }
        long msb = System.currentTimeMillis() << 16 | 0x7000 | counter.sum() & 0xFFF;

        counter.increment();

        long lsb =
                0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;

        return new UUID(msb, lsb);
    }
}
