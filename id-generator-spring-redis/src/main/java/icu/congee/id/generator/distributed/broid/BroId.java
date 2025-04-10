package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;

import lombok.*;

import java.nio.ByteBuffer;

@Data
@AllArgsConstructor
public class BroId implements Id {
    private long timestamp;
    private long threadId;
    private long sequence;

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(24)
                .putLong(timestamp)
                .putLong(threadId)
                .putLong(sequence)
                .array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
