package icu.congee.id.generator.distributed.segmentid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class SegmentChainIdGenerator implements IdGenerator {

    private final long MIN_STEP = 1L;
    private final long MAX_STEP = 1000L;
    private final RAtomicLong atomicLongId;
    private long current;
    private long step;
    private IdSegment idSegment;

    public SegmentChainIdGenerator(
            RedissonClient redissonClient,
            @Value("${id.generator.segment.name:IdGenerator:SegmentChainIdGenerator:current}")
                    String name) {
        this.atomicLongId = redissonClient.getAtomicLong(name);
        this.current = this.atomicLongId.get();
        this.idSegment = new IdSegment(this.current, this.current + this.step);
        this.step = MIN_STEP;

        // 客户端心跳线程
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::adjustStep, 0, 1, TimeUnit.SECONDS); // 每25秒续期一次
    }

    private void adjustStep() {
        // 引入饥饿状态维护号段安全距离，保证号段里一直有号可用
    }

    private IdSegment newIdSegment() {
        this.idSegment = new IdSegment(this.atomicLongId.getAndAdd(this.step), this.step);
        return this.idSegment;
    }

    @Override
    public Object generate() {
        if (current < this.idSegment.getEnd()) {
            return current++;
        } else {
            return newIdSegment().getStart();
        }
    }

    @Override
    public IdType idType() {
        return IdType.SegmentChainId;
    }
}
