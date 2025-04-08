package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;

import lombok.*;

@Data
@AllArgsConstructor
public class BroId implements Id {
    // 16bit
    private long threadId;
    // 48bit
    private long sequence;
    
    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        return threadId << 48 | sequence;
    }
}
