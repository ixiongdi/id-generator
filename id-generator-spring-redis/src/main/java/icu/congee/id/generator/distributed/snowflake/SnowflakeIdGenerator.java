package icu.congee.id.generator.distributed.snowflake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.service.MachineIdService;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Component
public enum SnowflakeIdGenerator implements IdGenerator {
    INSTANCE;

    // 12 bits for sequence
    private final AtomicLong currentSequence = new AtomicLong(0L);
    private final String uuid = UUID.randomUUID().toString();

    @Value("${id.generator.snowflake.epoch:1645557742000}") // 默认2023-01-01 00:00:00
    private long epoch;

    @Value("${id.generator.snowflake.timestamp:41}") // 默认2023-01-01 00:00:00
    private long timestampBits;

    @Value("${id.generator.snowflake.machine:10}") // 默认2023-01-01 00:00:00
    private long machineIdBits;

    @Value("${id.generator.snowflake.sequence:12}") // 默认2023-01-01 00:00:00
    private long sequenceBits;

    private volatile long lastTimestamp = -1L;

    @Resource private RedissonClient redisson;
    private MachineIdService machineIdService;

    @PostConstruct
    public void init() {
        machineIdService = new MachineIdService(redisson, IdType.Snowflake.getName());
    }

    private void waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
    }

    @Override
    public Long generate() {
        long timestamp = System.currentTimeMillis();

        // 检查时钟回拨
        if (timestamp < lastTimestamp) {
            timestamp = lastTimestamp;
        }

        // 如果是同一毫秒内
        if (timestamp == lastTimestamp) {
            long sequence = currentSequence.get();
            // 序列号达到最大值，等待下一毫秒
            if (sequence >= (1L << sequenceBits - 1)) {
                waitNextMillis(lastTimestamp);
                timestamp = System.currentTimeMillis();
                currentSequence.set(0L);
            }
        } else {
            // 时间戳变化，重置序列号
            currentSequence.set(0L);
        }

        lastTimestamp = timestamp;
        return (timestamp - epoch) << (machineIdBits + sequenceBits)
                | machineIdService.get(uuid) << sequenceBits
                | currentSequence.getAndIncrement();
    }

    @Override
    public IdType idType() {
        return IdType.Snowflake;
    }
}
