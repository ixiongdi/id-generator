package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;

@Component
public enum BroIdGenerator implements IdGenerator {
    INSTANCE;

    @Resource
    private RedissonClient redisson;

    private RAtomicLong threadId;

    private ThreadLocal<BroIdThreadLocalHolder> threadLocal;

    @Value("${id.generator.broid.sign-bits:1}")
    private int signBits;
    @Value("${id.generator.broid.thread-id-bits:15}")
    private int threadIdBits;
    @Value("${id.generator.broid.sequence-bits:48}")
    private int sequenceBits;

    @Value("${id.generator.broid.thread-pool.core-size:1}")
    private int corePoolSize;

    @Value("${id.generator.broid.thread-pool.max-size:16}")
    private int maxPoolSize;

    private ExecutorService threadPool;

    @PostConstruct
    public void init() {
        validateBitsConfiguration();
        validateThreadPoolConfiguration();

        this.threadId = redisson.getAtomicLong("IdGenerator:BroIdGenerator:threadId");
        this.threadLocal = ThreadLocal.withInitial(
                () -> new BroIdThreadLocalHolder(this.threadId.getAndIncrement(), 0));

        // 创建自定义线程池
        this.threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                1,
                TimeUnit.DAYS,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    @PreDestroy
    public void destroy() {
        if (threadPool != null && !threadPool.isShutdown()) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void validateBitsConfiguration() {
        if ((signBits + threadIdBits + sequenceBits) > 64) {
            throw new IllegalArgumentException("Total bits exceed 64");
        }
        if (signBits < 0 || threadIdBits <= 0 || sequenceBits <= 0) {
            throw new IllegalArgumentException("Bits configuration invalid");
        }
    }

    private void validateThreadPoolConfiguration() {
        if (corePoolSize < 1) {
            throw new IllegalArgumentException("Core pool size must be at least 1");
        }
        if (maxPoolSize < corePoolSize) {
            throw new IllegalArgumentException("Max pool size must be greater than or equal to core pool size");
        }
        if (maxPoolSize > 64) {
            throw new IllegalArgumentException("Max pool size cannot exceed 64");
        }
    }

    @Override
    public Long generate() {
        try {
            Future<Long> future = threadPool.submit(new IdGenerationTask());
            return future.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("ID generation was interrupted", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error generating ID", e.getCause());
        }
    }

    private class IdGenerationTask implements Callable<Long> {
        @Override
        public Long call() {
            BroIdThreadLocalHolder broIdThreadLocalHolder = threadLocal.get();
            final int threadShift = sequenceBits;
            final long maxThreadId = (1L << threadIdBits) - 1;
            final long maxSequence = (1L << sequenceBits) - 1;

            if (broIdThreadLocalHolder.threadId > maxThreadId) {
                throw new IllegalStateException("Thread ID exceeds maximum value");
            }
            if (broIdThreadLocalHolder.sequence > maxSequence) {
                broIdThreadLocalHolder.sequence = 0L;
            }

            return (broIdThreadLocalHolder.threadId << threadShift) | broIdThreadLocalHolder.sequence++;
        }
    }

    @Override
    public IdType idType() {
        return IdType.BroId;
    }

    @Data
    @AllArgsConstructor
    public static class BroIdThreadLocalHolder {
        long threadId;
        long sequence;
    }
}
