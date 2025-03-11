package icu.congee.id.generator.broid.part;

import icu.congee.id.generator.broid.BitUtils;
import icu.congee.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
public class ClockSeqBroIdPart implements BroIdPart {

    private final AtomicLong timestamp;

    private final AtomicLong sequence;

    private final int bits;


    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(sequence.getAndIncrement(), getBits());
    }
}
