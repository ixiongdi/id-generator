package com.github.ixiongdi.id.generator.lexical;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个并发的、严格递增的时钟实现
 * <p>
 * 该实现保证生成的时间戳总是严格递增的，即使在多线程环境下也能保持正确性。
 * 它使用AtomicLong来存储最后一次生成的时间戳，并通过CAS操作来确保线程安全。
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public abstract class StrictlyClock implements Clock {
    private final AtomicLong counter = new AtomicLong(tick());

    @Override
    public long timestamp() {
        long newTime = 0;
        while (newTime == 0) {
            long last = counter.get();
            long current = tick();
            long next = current > last ? current : last + 1;
            if (counter.compareAndSet(last, next)) {
                newTime = next;
            }
        }
        return newTime;
    }

    /**
     * 获取当前的时间戳
     * <p>
     * 子类需要实现这个方法来提供基础的时间戳。这个时间戳不需要保证严格递增，
     * 因为StrictlyClock会在此基础上确保最终返回的时间戳是严格递增的。
     * </p>
     *
     * @return 当前的时间戳
     */
    protected abstract long tick();
}