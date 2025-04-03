package icu.congee.id.generator.distributed.snowflake;

import icu.congee.id.base.Id;

import lombok.Data;

@Data
public class SnowflakeId implements Id {
    private long timestamp;
    private long machineId;
    private long sequence;

    public SnowflakeId(long timestamp, long machineId, long sequence) {
        this.timestamp = timestamp;
        this.machineId = machineId;
        this.sequence = sequence;
    }

    @Override
    public byte[] toBytes() {
        return long2bytes(toLong());
    }

    @Override
    public long toLong() {
        return timestamp << 22 | machineId << 12 | sequence;
    }
}
