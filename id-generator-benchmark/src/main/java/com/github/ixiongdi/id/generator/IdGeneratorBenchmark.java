package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.util.IdUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, warmups = 0)
@Threads(16) // 设置并发线程数为4
public class IdGeneratorBenchmark {

    // main 方法，用于运行基准测试
    public static void main(String[] args) throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include(IdGeneratorBenchmark.class.getSimpleName())
                        .forks(1)
                        .warmupIterations(1) // 预热迭代次数
                        .measurementIterations(1)
                        .build();
        new Runner(opt).run();
    }

    @Benchmark
    public void mist() {
    }

    @Benchmark
    public void ulid() {
        IdUtil.ulid();
    }

    @Benchmark
    public void lexicalUUID() {
        IdUtil.lexicalUUID();
    }

    @Benchmark
    public void businessId() {
        IdUtil.businessId();
    }

    @Benchmark
    public void randomId() {
        IdUtil.randomId();
    }

    @Benchmark
    public void unixTimeBasedUUID() {
        IdUtil.unixTimeBasedUUID();
    }

    @Benchmark
    public void unixTimeBasedUUID1() {
        IdUtil.unixTimeBasedUUID1();
    }

    @Benchmark
    public void unixTimeBasedUUID2() {
        IdUtil.unixTimeBasedUUID2();
    }

    @Benchmark
    public void customUUID() {
        IdUtil.customUUID();
    }
}
