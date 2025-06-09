package icu.congee.id.generator.distributed.segmentid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SegmentChainIdGenerator implements IdGenerator {

    private final ThreadLocal<IdSegmentChain> threadLocalHolder;
    private long step = 1000;
    private long count = 0;

    public SegmentChainIdGenerator(RedissonClient redissonClient) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong("SegmentChainIdGenerator:NextMaxId");
        threadLocalHolder = ThreadLocal.withInitial(() -> new IdSegmentChain(atomicLong, this.step, 2));

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(this::calcStep, 1, 1, TimeUnit.SECONDS);
    }

    private void calcStep() {
        if (count >= step) {
            this.step = Math.max(step * 2, 1);
        } else {
            this.step = Math.min(step / 2, Integer.MAX_VALUE);
        }
        log.info("过去一秒生成：{}个ID， 调整步长为：{}", count,  step);
        count = 0;
    }

    @Override
    public Long generate() {
        count++;
        return threadLocalHolder.get().nextId();
    }

    @Override
    public IdType idType() {
        return IdType.SegmentChainId;
    }
}
