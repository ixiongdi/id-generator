package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;

import lombok.*;

@Data
@AllArgsConstructor
public class BroId implements Id {
    // 48bit
    private long sequence;
    // 16bit
    private long threadId;

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        return sequence << 16 | threadId;
    }
}
