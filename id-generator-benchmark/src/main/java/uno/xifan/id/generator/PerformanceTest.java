package uno.xifan.id.generator;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Threads(100)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
public class PerformanceTest {
    private final AtomicLong atomicLong = new AtomicLong();
    private final LongAdder longAdder = new LongAdder();
    private long longValue;

    // 测试AtomicLong的递增性能
    @Benchmark
    public void atomicLongIncrement(Blackhole bh) {
        bh.consume(atomicLong.incrementAndGet());
    }

    // 测试LongAdder的递增性能
    @Benchmark
    public void longAdderIncrement(Blackhole bh) {
        longAdder.increment();
        bh.consume(longAdder.sum());
    }

    @Benchmark
    public void longValueIncrement(Blackhole bh) {
        bh.consume(++longValue);
    }
}
