package com.github.ixiongdi.id.generator.custom;

import com.github.ixiongdi.id.generator.NumberIdGenerator;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TimeBasedAndRandomIdGenerator implements NumberIdGenerator {

    /** Wed May 01 2024 00:00:00 GMT+0800 (GMT+08:00) 纪念RFC9652标准发布 */
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
