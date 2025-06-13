package icu.congee.id.generator.distributor;

import icu.congee.id.util.IdGeneratorExecutors;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.*;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ThreadIdDistributor {

    // 记录已分配的机器ID
    private final RSetCache<Long> set;
    private final RAtomicLong atomicLong;
    private final String namespace;
    private final long threadId;

    public ThreadIdDistributor(RedissonClient redisson, String namespace, int bits) {
        this.namespace = namespace;
        this.set =
                redisson.getSetCache(
                        String.format("IdGenerator:ThreadIdDistributor:%s:Set", namespace));
        this.atomicLong =
                redisson.getAtomicLong(
                        String.format("IdGenerator:ThreadIdDistributor:%s:AtomicLong", namespace));
        this.threadId = register(bits);

        // 客户端心跳线程
        IdGeneratorExecutors.getScheduledExecutorService()
                .scheduleWithFixedDelay(this::renewLease, 15, 30, TimeUnit.SECONDS); // 每25秒续期一次
    }

    public void renewLease() {
        set.add(threadId, 60, TimeUnit.SECONDS);
        log.info("{} renew thread id:{}", this.namespace, threadId);
    }

    public long register(int bits) {
        long id;
        do {
            id = Math.abs(this.atomicLong.getAndIncrement() % (1L << bits));
        } while (!set.add(id, 60, TimeUnit.SECONDS)); // 利用add方法返回值判断是否存在
        log.info("{} register thread id:{}", namespace, id);
        return id;
    }

    public long get() {
        return threadId;
    }
}
