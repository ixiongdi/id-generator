package com.github.ixiongdi.id.generator;

import java.util.concurrent.atomic.AtomicLong;

public class MonotonicClock {
    private final AtomicLong lastTimestamp = new AtomicLong(0);

    public long getTimestamp() {
        long current = System.currentTimeMillis();
        while (true) {
            long last = lastTimestamp.get();
            if (current <= last) {
                current = last + 1; // 确保递增
            }
            if (lastTimestamp.compareAndSet(last, current)) {
                return current;
            }
        }
    }
}