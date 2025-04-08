package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.Id;
import lombok.Data;

@Data
public class MistId implements Id {
    private long increment;
    private long random;

    public MistId(long increment, long random) {
        this.increment = increment;
        this.random = random;
    }

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        return this.increment << 16 | this.random;
    }

    @Override
    public String toString() {
        return String.valueOf(toLong());
    }
}
