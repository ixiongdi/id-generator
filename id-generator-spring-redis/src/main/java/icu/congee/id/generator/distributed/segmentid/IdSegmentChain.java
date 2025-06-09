package icu.congee.id.generator.distributed.segmentid;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RAtomicLong;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class IdSegmentChain {
    private final RingBuffer<IdSegment> ringBuffer;
    private final RAtomicLong globalMaxId;
    private final int size = 10;

    private long step = 1000;
    private long count = 0;

    public IdSegmentChain(RAtomicLong globalMaxId) {
        this.globalMaxId = globalMaxId;
        this.ringBuffer = new RingBuffer<>(size);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::calcStep, 1000, 1000, TimeUnit.MILLISECONDS);

        fetch();
    }

    public long nextId() {
        while (true) {
            if (ringBuffer.isEmpty()) {
                fetch();
            }
            
            IdSegment idSegment = ringBuffer.read();
            if (idSegment == null) continue;

            if (!idSegment.isOverflow()) {
                count++;
                return idSegment.next();
            }
        }
    }

    public void fetch() {
        while (ringBuffer.size() < size) {
            long nextMaxId = globalMaxId.getAndAdd(step);
            ringBuffer.write(new IdSegment(nextMaxId, nextMaxId + step));
        }
    }

    private void calcStep() {
        if (count >= step / 2) {
            this.step = Math.max(step * 2, 1);
        } else {
            this.step = Math.min(step / 2, Integer.MAX_VALUE);
        }
        log.info("过去一秒生成：{}个ID， 调整步长为：{}", count, step);
        count = 0;
        fetch();
    }
}