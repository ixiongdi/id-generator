package icu.congee.id.generator.distributed.segmentid;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IdSegment {
    private long start;
    private long end;

    private long current;

    public IdSegment(long start, long end) {
        this.start = start;
        this.end = end;

        this.current = start;
    }

    public boolean isOverflow() {
        return current >= end;
    }

    public long next() {
        return current++;
    }
}
