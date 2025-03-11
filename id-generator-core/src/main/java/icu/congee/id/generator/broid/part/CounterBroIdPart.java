package icu.congee.id.generator.broid.part;

import icu.congee.id.generator.broid.BitUtils;
import icu.congee.id.generator.broid.BroIdPart;

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
