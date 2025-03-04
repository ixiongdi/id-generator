package icu.congee.custom;

import icu.congee.NumberIdGenerator;

import java.time.Instant;
import java.util.concurrent.ThreadLocalRandom;

public class TimeBasedAndRandomIdGenerator implements NumberIdGenerator {

    public static long next() {
        return (Instant.now().getEpochSecond() << 32) | ThreadLocalRandom.current().nextInt();
    }

    @Override
    public Number generate() {
        return next();
    }
}
