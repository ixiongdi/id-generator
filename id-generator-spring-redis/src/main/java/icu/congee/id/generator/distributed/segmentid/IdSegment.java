package icu.congee.id.generator.distributed.segmentid;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicLong;

@Data
@NoArgsConstructor
public class IdSegment {
    private long start;
    private long end;

    private AtomicLong current;

    public IdSegment(long start, long end) {
        this.start = start;
        this.end = end;

        this.current.set(start);
    }

    public boolean isOverflow() {
        return current.get() >= end;
    }

    public long next() {
        return current.getAndIncrement();
    }
}
