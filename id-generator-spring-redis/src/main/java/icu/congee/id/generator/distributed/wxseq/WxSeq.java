package icu.congee.id.generator.distributed.wxseq;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.nio.ByteBuffer;

@AllArgsConstructor
@ToString
public class WxSeq implements Id {

    private long userId;
    private long sequence;

    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(16).putLong(userId).putLong(sequence).array();
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException();
    }
}
