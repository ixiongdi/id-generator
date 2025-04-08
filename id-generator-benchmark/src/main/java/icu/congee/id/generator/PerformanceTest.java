package icu.congee.id.generator;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import org.openjdk.jmh.runner.options.Options;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@BenchmarkMode(Mode.Throughput)    // 测试吞吐量
@OutputTimeUnit(TimeUnit.MICROSECONDS) // 时间单位为微秒
@Warmup(iterations = 3, time = 1) // 预热3轮，每轮1秒
@Measurement(iterations = 5, time = 1) // 正式测试5轮，每轮1秒
@Fork(1)                          // 单进程测试
@State(Scope.Benchmark)
public class PerformanceTest {
    private AtomicLong atomicLong = new AtomicLong();
    private LongAdder longAdder = new LongAdder();
    private long      longValue = 0L;

    // 测试AtomicLong的递增性能
    @Benchmark
    @Threads(10) // 模拟10线程并发
    public void atomicLongIncrement() {
        atomicLong.getAndIncrement();
    }

    // 测试LongAdder的递增性能
    @Benchmark
    @Threads(10)
    public void longAdderIncrement() {
        longAdder.increment();
    }

    @Benchmark
    @Threads(10)
    public void longValueIncrement() {
        longValue++;
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(PerformanceTest.class.getSimpleName())
                .build();
        new Runner(options).run();
    }
}