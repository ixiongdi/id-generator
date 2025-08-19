package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
public class CounterBroIdPart implements BroIdPart {

    private final AtomicLong counter;
    private final int bits;

    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(counter.getAndIncrement(), getBits());
    }
}
