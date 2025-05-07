package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
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

    private final Queue<Long> queue = new ConcurrentLinkedQueue<>();
    private final AtomicBoolean isFilling = new AtomicBoolean(false);
    @Resource
    private RedissonClient redisson;
    private RAtomicLong atomicLong;
    @Value("${id.generator.mist.name:IdGenerator:AtomicLongIdGenerator:current}")
    private String name;
    @Value("${id.generator.mist.value:-1}")
    private Long value;
    @Value("${id.generator.mist.secret:false}")
    private Boolean secret;
    @Value("${id.generator.mist.bufferSize:65536}")
    private Integer bufferSize;
    private Random random;

    public MistIdGenerator(RedissonClient redisson) {
        this.redisson = redisson;
    }

    private void fillQueue() {
        long e = atomicLong.getAndAdd(bufferSize);
        for (int i = 0; i < bufferSize; i++) {
            queue.offer(e + i);
        }
    }

    @PostConstruct
    public void init() {
        atomicLong = redisson.getAtomicLong(name);
        if (value >= 0) {
            atomicLong.set(value);
        }
        random = secret ? new SecureRandom() : ThreadLocalRandom.current();

        // 客户端心跳线程
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::fillQueue, 0, 10, TimeUnit.MILLISECONDS);
        fillQueue();
    }

    @Override
    public Long generate() {
        synchronized (this) {
            if (queue.size() < bufferSize * 0.1 && isFilling.compareAndSet(false, true)) {
                new Thread(() -> {
                    try {
                        fillQueue();
                    } finally {
                        isFilling.set(false);
                    }
                }).start();
            }
        }
        return queue.remove() | random.nextInt() & 0xFFFF;
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }
}
