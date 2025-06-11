package icu.congee.id.generator.distributed.segmentid;

import lombok.extern.log4j.Log4j2;

import org.redisson.api.RAtomicLong;

import java.util.concurrent.*;

@Log4j2
public class IdSegmentChain {
    private final RingBuffer<IdSegment> segmentQueue;
    private final RAtomicLong globalMaxId;

    private long step = 1000;
    private long count = 0;

    private IdSegment idSegment;

    public IdSegmentChain(RAtomicLong globalMaxId) {
        this.globalMaxId = globalMaxId;
        this.segmentQueue = new RingBuffer<>(2);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::calcStep, 1000, 1000, TimeUnit.MILLISECONDS);
        fetch();
    }

    public long nextId() {
        if (idSegment == null || idSegment.isOverflow()) {
            try {
                idSegment = segmentQueue.take();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        count++;
        return idSegment.next();
    }

    public void fetch() {
        while (!segmentQueue.isFull()) {
            long nextMaxId = globalMaxId.getAndAdd(step);
            try {
                segmentQueue.put(new IdSegment(nextMaxId, nextMaxId + step));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void calcStep() {
//        if (count >= step / 2) {
//            this.step = Math.max(step * 2, 1);
//        } else {
//            this.step = Math.min(step / 2, Integer.MAX_VALUE);
//        }
        log.info("过去一秒生成：{}个ID，调整步长为：{}", count, step);
        count = 0;
        fetch();
    }
}
