package icu.congee;

import cn.hutool.core.util.IdUtil;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class IDGeneraterBenchmark {


    // 测试单个 ID 生成性能
    @Benchmark
    public void randomUUID(Blackhole bh) {
        bh.consume(IdUtil.randomUUID());
    }

    @Benchmark
    public void simpleUUID(Blackhole bh) {
        bh.consume(IdUtil.simpleUUID());
    }

    @Benchmark
    public void fastUUID(Blackhole bh) {
        bh.consume(IdUtil.fastUUID());
    }

    @Benchmark
    public void fastSimpleUUID(Blackhole bh) {
        bh.consume(IdUtil.fastSimpleUUID());
    }

    @Benchmark
    public void objectId(Blackhole bh) {
        bh.consume(IdUtil.objectId());
    }

    @Benchmark
    public void nanoId(Blackhole bh) {
        bh.consume(IdUtil.nanoId());
    }

    @Benchmark
    public void getSnowflakeNextId(Blackhole bh) {
        bh.consume(IdUtil.getSnowflakeNextId());
    }

    // main 方法，用于运行基准测试
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IDGeneraterBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new Runner(opt).run();
    }
}