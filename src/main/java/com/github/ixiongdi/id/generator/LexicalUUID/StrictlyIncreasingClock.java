package com.github.ixiongdi.id.generator.LexicalUUID;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 一个线程安全、严格递增的时钟抽象类。
 */
public abstract class StrictlyIncreasingClock implements Clock {
    private final AtomicLong counter;

    public StrictlyIncreasingClock() {
        counter = new AtomicLong(tick());
    }

    /**
     * 获取严格递增的时间戳。
     * 通过CAS操作确保线程安全，时间戳永不回退。
     */
    public long timestamp() {
        long newTime = 0;
        while (newTime == 0) {
            long last = counter.get();       // 当前计数器值
            long current = tick();           // 获取基础时间源
            long next = current > last ? current : last + 1; // 计算下一个时间戳
            if (counter.compareAndSet(last, next)) { // 原子更新
                newTime = next;
            }
        }
        return newTime;
    }

    /**
     * 子类需实现的抽象方法，提供基础时间源。
     */
    protected abstract long tick();
}