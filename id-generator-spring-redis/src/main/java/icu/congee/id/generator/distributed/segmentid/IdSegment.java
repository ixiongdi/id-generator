package icu.congee.id.generator.distributed.segmentid;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IdSegment {
    private long start;
    private long end;
}
