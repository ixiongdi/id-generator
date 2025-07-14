package icu.congee.id.util;

import lombok.Getter;

import java.util.concurrent.*;

public class IdGeneratorExecutors {

    // ---------- 原有线程池 ----------
    @Getter
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Getter
    private static final ScheduledExecutorService scheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor();

    // ---------- 新增 CPU 密集型线程池 ----------
    @Getter
    private static final ExecutorService cpuExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors(),
            r -> new Thread(r, "cpu-pool-" + r.hashCode()));

    // ---------- 新增 IO 密集型线程池 ----------
    @Getter
    private static final ExecutorService ioExecutor = new ThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors(),
            Runtime.getRuntime().availableProcessors() * 10,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<>(),
            r -> new Thread(r, "io-pool-" + r.hashCode()), // 简单命名
            new ThreadPoolExecutor.CallerRunsPolicy());
}