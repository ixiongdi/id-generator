package icu.congee.id.generator.distributed.segmentid;

import org.redisson.api.RAtomicLong;

import java.util.ArrayDeque;
import java.util.Deque;

public class IdSegmentChain {
    private final Deque<IdSegment> deque = new ArrayDeque<>();
    private final RAtomicLong globalMaxId;
    private final long currentStep;
    private final long size;

    public IdSegmentChain(RAtomicLong globalMaxId, long currentStep, long size) {
        this.globalMaxId = globalMaxId;
        this.currentStep = currentStep;
        this.size = size;

        for (int i = 0; i < this.size; i++) {
            fetch();
        }
    }

    public long nextId() {
        while (true) {
            if (deque.size() < this.size) {
                fetch();
            } else {
                IdSegment idSegment = this.deque.getFirst();
                if (idSegment.isOverflow()) {
                    this.deque.removeFirst();
                } else {
                    return idSegment.next();
                }
            }
        }
    }

    public void fetch() {
        long nextMaxId = globalMaxId.getAndAdd(currentStep);
        this.deque.addLast(new IdSegment(nextMaxId, nextMaxId + currentStep));
    }
}
