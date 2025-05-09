package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class MistIdGenerator implements IdGenerator {
    private static final int DEFAULT_BUFFER_SIZE = 65536;
    private static final int REFILL_THRESHOLD = DEFAULT_BUFFER_SIZE / 4;
    private static final int MASK = 0xFFFF;

    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    private final ExecutorService refillExecutor = Executors.newSingleThreadExecutor(r -> {
        Thread t = new Thread(r, "mist-id-refill-thread");
        t.setDaemon(true);
        return t;
    });

    private final RedissonClient redisson;
    private RAtomicLong atomicLong;
    private final String name;
    private final long initialValue;
    private final int bufferSize;
    private final Random random;

    // 使用构造函数注入替代字段注入
    public MistIdGenerator(RedissonClient redisson,
                           @Value("${id.generator.mist.name:IdGenerator:AtomicLongIdGenerator:current}") String name,
                           @Value("${id.generator.mist.value:-1}") long initialValue,
                           @Value("${id.generator.mist.secret:false}") boolean useSecureRandom,
                           @Value("${id.generator.mist.bufferSize:65536}") int bufferSize) {
        this.redisson = redisson;
        this.name = name;
        this.initialValue = initialValue;
        this.bufferSize = bufferSize;
        this.random = useSecureRandom ? new SecureRandom() : ThreadLocalRandom.current();
    }

    @PostConstruct
    public void init() {
        atomicLong = redisson.getAtomicLong(name);
        if (initialValue >= 0) {
            atomicLong.set(initialValue);
        }

        // 使用Spring管理的ScheduledThreadPool
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "mist-id-scheduler");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(this::fillQueue, 0, 10, TimeUnit.MILLISECONDS);

        // 初始填充
        fillQueue();
    }

    private void fillQueue() {
        final long start = atomicLong.getAndAdd(bufferSize);
        for (long i = 0; i < bufferSize; i++) {
            queue.offer(start + i);
        }
    }

    @Override
    public Long generate() {
        // 使用更简洁的同步控制
        if (queue.size() < REFILL_THRESHOLD && isFilling.compareAndSet(false, true)) {
            refillExecutor.submit(() -> {
                try {
                    fillQueue();
                } finally {
                    isFilling.set(false);
                }
            });
        }

        // 使用更简洁的随机数生成方式
        final long id = queue.poll();
        return id | (random.nextInt() & MASK);
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }

    // 添加销毁方法释放资源
    @PreDestroy
    public void destroy() {
        refillExecutor.shutdownNow();
    }
}