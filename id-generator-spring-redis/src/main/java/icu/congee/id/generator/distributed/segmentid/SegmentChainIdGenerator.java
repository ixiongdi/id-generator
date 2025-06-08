package icu.congee.id.generator.distributed.segmentid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
@Slf4j
public class SegmentChainIdGenerator implements IdGenerator {

    private static final long INIT_STEP = 1000;
    private final LongAdder rate = new LongAdder();
    private final AtomicLong currentId = new AtomicLong();
    private final RedissonClient redissonClient;
    // 当前段信息
    private final ConcurrentLinkedQueue<IdSegment> segmentQueue = new ConcurrentLinkedQueue<>();
    private RAtomicLong globalMaxId;
    private volatile long currentEnd;
    private volatile IdSegment currentSegment;
    private volatile long currentStep = INIT_STEP;
    private long nextStep = INIT_STEP;

    public SegmentChainIdGenerator(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        initialize();
    }

    /**
     * 初始化全局ID指针和首段缓存
     *
     * @throws IllegalStateException 当Redis连接失败时抛出
     */
    private void initialize() {

        globalMaxId = redissonClient.getAtomicLong("segment_chain_global_max_id");
        if (!globalMaxId.isExists()) {
            globalMaxId.set(0L);
        }
        prefetchNextSegment();
        switchToNextSegment();
        Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(this::monitor, 0, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public Long generate() {
        while (true) {
            IdSegment segment = currentSegment;

            if (segment != null) {
                long id = segment.nextId();
                if (id != -1) {
                    rate.increment();
                    return id;
                }
            }

            synchronized (this) {
                if ((segment = currentSegment) == null || segment.isExhausted()) {
                    currentSegment = segmentQueue.poll();
                    if (currentSegment == null) {
                        prefetchNextSegment();
                        currentSegment = segmentQueue.poll();
                    }
                }
            }
        }
    }

    @Override
    public IdType idType() {
        return IdType.SegmentChainId;
    }

    /** 切换到下一段 */
    private void switchToNextSegment() {
        if (!segmentQueue.isEmpty()) {
            currentSegment = segmentQueue.poll();
            currentId.set(currentSegment.start);
            currentEnd = currentSegment.end;
        }
        currentId.set(currentSegment.start);
        currentEnd = currentSegment.end;
        currentStep = nextStep;
    }

    /**
     * 预取下一个ID段（非线程安全）
     *
     * @implNote 需要保证在同步块中调用
     */
    private void prefetchNextSegment() {
        long newStart = globalMaxId.getAndAdd(currentStep);
        segmentQueue.add(new IdSegment(newStart, newStart + currentStep));
        if (segmentQueue.size() < 2) { // 保持至少两个备用段
            long nextStart = globalMaxId.getAndAdd(currentStep);
            segmentQueue.add(new IdSegment(nextStart, nextStart + currentStep));
        }
    }

    /** 预取下一段 */

    /**
     * 动态调整步长算法
     *
     * @param currentRate 当前秒级ID生成速率
     * @apiNote 当currentRate>=当前步长时倍增，否则折半，步长范围[1,65536]
     */
    private void adjustStepBasedOnRate(long currentRate) {
        if (currentRate >= currentStep) {
            nextStep = currentStep * 2;
        } else {
            nextStep = currentStep / 2;
        }
    }

    /** 动态调整步长逻辑 */

    /** 监控任务（每秒执行） 职责： 1. 统计并重置ID生成速率 2. 动态调整步长 3. 触发段预取 */
    public void monitor() {

        // 获取并重置速率值
        long currentRate = rate.sumThenReset();
        log.info("当前速率: {}-{}", currentStep, currentRate);

        // 动态调整步长（示例：每秒超过5000则步长翻倍，低于1000则减半）
        // 动态步长调整（速率≥当前步长则翻倍，否则减半）
        adjustStepBasedOnRate(currentRate);

        // 应用极值限制
        nextStep = Math.max(nextStep, 1);
        nextStep = Math.min(nextStep, 1 << 30); // 扩展最大步长至1048576

        // 触发预取条件
        long remaining = currentEnd - currentId.get();
        if (remaining < currentStep * 0.7 && segmentQueue.size() < 2) {
            prefetchNextSegment();
        }
    }

    /** 定时监控（每秒） */
    private static class IdSegment {
        private final long start;
        private final long end;
        private long current;

        public IdSegment(long start, long end) {
            this.start = start;
            this.end = end;
            this.current = start;
        }

        public synchronized long nextId() {
            if (current >= end) {
                return -1;
            }
            return current++;
        }

        public boolean isExhausted() {
            return current >= end;
        }
    }
}
