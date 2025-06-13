package icu.congee.id.generator.distributed.segmentid.concurrent;


import icu.congee.id.generator.distributed.segmentid.IdSegment;
import icu.congee.id.util.IdGeneratorExecutors;

import lombok.extern.log4j.Log4j2;

import org.redisson.api.RAtomicLong;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Log4j2
public class IdSegmentChain {
    private final RAtomicLong globalMaxId;

    private final AtomicLong step = new AtomicLong(1000);
    private final LongAdder count = new LongAdder();

    private volatile IdSegment current = new IdSegment();
    private volatile IdSegment next = new IdSegment();

    public IdSegmentChain(RAtomicLong globalMaxId) {
        this.globalMaxId = globalMaxId;

        IdGeneratorExecutors.getScheduledExecutorService()
                .scheduleWithFixedDelay(this::calcStep, 1, 1, TimeUnit.SECONDS);

        fetch(current);
        fetch(next);
    }

    public void swap() {
        IdSegment temp = current;
        current = next;
        next = temp;
    }

    public long nextId() {
        while (true) {
            if (current.isOverflow() && next.isOverflow()) {
                fetch(current);
                IdGeneratorExecutors.getExecutorService()
                        .submit(
                                () -> {
                                    fetch(next);
                                });
            } else if (current.isOverflow()) {
                swap();
                IdGeneratorExecutors.getExecutorService()
                        .submit(
                                () -> {
                                    fetch(next);
                                });
                continue;
            }
            count.increment();
            return current.next();
        }
    }

    public void fetch(IdSegment idSegment) {
        long s = step.get();
        long nextMaxId = globalMaxId.getAndAdd(s);
        idSegment.setStart(nextMaxId);
        idSegment.setEnd(nextMaxId + s);
        idSegment.getCurrent().set(nextMaxId);
    }

    private void calcStep() {
        long s = step.get();
        long c = count.sumThenReset();
        if (c >= s) {
            this.step.set(c * 2);
        } else if (c > 0) {
            this.step.set(c);
        } else {
            this.step.set(1);
        }
        log.info("过去一秒生成：{}个ID，调整步长为：{}", c, s);
    }
}
