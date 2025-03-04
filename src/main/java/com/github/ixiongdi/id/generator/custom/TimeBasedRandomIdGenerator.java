package com.github.ixiongdi.id.generator.custom;

import java.util.concurrent.ThreadLocalRandom;

public class TimeBasedRandomIdGenerator {

    /** 纪念RFC9652标准发布的时间点: Wed May 01 2024 00:00:00 GMT+0800 (GMT+08:00) */
    private static final long EPOCH = 1714492800L; // Convert epoch to milliseconds

    public static long next() {
        long timestamp = System.currentTimeMillis() / 1000 - EPOCH;
        long randomPart = ThreadLocalRandom.current().nextInt() & 0xFFFFFFFFL;
        return timestamp << 32 | randomPart;
    }
}
