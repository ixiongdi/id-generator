package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.Id;
import lombok.Data;

@Data
public class MistId implements Id {
    private long increment;
    private long random1;
    private long random2;

    public MistId(long increment, long random1, long random2) {
        this.increment = increment;
        this.random1 = random1;
        this.random2 = random2;
    }

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        return this.increment << 16 | this.random1 << 8 | this.random2;
    }

    @Override
    public String toString() {
        return String.valueOf(toLong());
    }
}
