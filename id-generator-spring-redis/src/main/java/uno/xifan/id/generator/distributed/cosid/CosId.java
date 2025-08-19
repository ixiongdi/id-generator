package uno.xifan.id.generator.distributed.cosid;

import uno.xifan.id.base.Base36;
import uno.xifan.id.base.Base62;
import uno.xifan.id.base.Id;

import lombok.Data;

@Data
public class CosId implements Id {

    private long timestamp;
    private long machineId;
    private long sequence;

    // 位数配置
    private int timestampBits;
    private int machineBits;
    private int sequenceBits;

    public CosId(
            long timestamp,
            long machineId,
            long sequence,
            int timestampBits,
            int machineBits,
            int sequenceBits) {
        this.timestamp = timestamp;
        this.machineId = machineId;
        this.sequence = sequence;
        this.timestampBits = timestampBits;
        this.machineBits = machineBits;
        this.sequenceBits = sequenceBits;
    }

    @Override
    public String toBase62() {
        return Base62.encode(long2bytes(timestamp))
                + Base62.encode(long2bytes(machineId))
                + Base62.encode(long2bytes(sequence));
    }

    @Override
    public String toBase36() {
        return Base36.encode(long2bytes(timestamp))
                + Base36.encode(long2bytes(machineId))
                + Base36.encode(long2bytes(sequence));
    }

    @Override
    public byte[] toBytes() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public long toLong() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
