package icu.congee.custom;

import icu.congee.NumberIdGenerator;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TimeBasedAndRandomIdGenerator implements NumberIdGenerator {

    /**
     * 2024-05-01 00:00:00 纪念RFC9652标准发布
     */
    private static final long EPOCH = 1714492800L;

    public static long next() {
        return (Instant.now().getEpochSecond() - EPOCH << 32)
                | (ThreadLocalRandom.current().nextInt() & 0xFFFFFFFFL);
    }

    @Override
    public Number generate() {
        return next();
    }
}
