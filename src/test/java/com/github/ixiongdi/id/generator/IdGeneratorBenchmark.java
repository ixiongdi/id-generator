package com.github.ixiongdi.id.generator;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.security.NoSuchAlgorithmException;
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
    public void customUUID() {
        IdUtil.customUUID();
    }
}
