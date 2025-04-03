package icu.congee.id.generator.distributed.cosid;

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
public enum CosIdGenerator implements IdGenerator {
    INSTANCE;

    private static final long MAX_SEQUENCE = 1 << 12 - 1; // 12位序列号
    private final AtomicLong currentSequence = new AtomicLong(0L);
    private volatile long lastTimestamp = -1L;

    @Value("${id.generator.cosid.epoch:1672502400000}") // 默认2023-01-01 00:00:00
    private long epoch;

    @Resource private RedissonClient redisson;
    private MachineIdService machineIdService;

    @PostConstruct
    public void init() {
        machineIdService = new MachineIdService(redisson, IdType.CosId.getName());
    }

    /**
     * 等待下一个毫秒
     */
    private void waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
    }

    /**
     * 获取当前时间戳（相对于纪元时间）
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis() - epoch;
    }

    /**
     * 时钟回拨异常
     */
    public static class ClockMovedBackwardsException extends RuntimeException {
        public ClockMovedBackwardsException(String message) {
            super(message);
        }
    }

    @Override
    public synchronized CosId generate() {
        long timestamp = getCurrentTimestamp();

        // 检查时钟回拨
        if (timestamp < lastTimestamp) {
            long backwardMillis = lastTimestamp - timestamp;
            String errorMessage = String.format("时钟回拨，拒绝生成ID，回拨时间：%d毫秒", backwardMillis);
            throw new ClockMovedBackwardsException(errorMessage);
        }

        // 如果是同一毫秒
        if (timestamp == lastTimestamp) {
            long sequence = currentSequence.get();
            // 同一毫秒内序列号达到最大值
            if (sequence >= MAX_SEQUENCE) {
                waitNextMillis(lastTimestamp);
                timestamp = getCurrentTimestamp();
                currentSequence.set(0L);
            }
        } else {
            // 时间戳变化，重置序列号
            currentSequence.set(0L);
        }

        lastTimestamp = timestamp;
        return new CosId(
                timestamp,
                machineIdService.get(),
                currentSequence.getAndIncrement());
    }

    @Override
    public IdType idType() {
        return IdType.CosId;
    }
}
