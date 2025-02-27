package icu.congee;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.uuid.Generators;
import icu.congee.uuid.UUIDv7Generator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class IDGeneraterBenchmark {


    // 测试单个 ID 生成性能
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void UUIDv7Generator(Blackhole bh) {
        bh.consume(UUIDv7Generator.generate());
    }

    // 测试单个 ID 生成性能
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void jdkRandomUUID(Blackhole bh) {
        bh.consume(UUID.randomUUID());
    }

    // 测试单个 ID 生成性能
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void nameUUIDFromBytes(Blackhole bh) {
        bh.consume(UUID.nameUUIDFromBytes("randomUUID".getBytes()));
    }

    // 测试单个 ID 生成性能
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void randomUUID(Blackhole bh) {
        bh.consume(IdUtil.randomUUID());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void simpleUUID(Blackhole bh) {
        bh.consume(IdUtil.simpleUUID());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void fastUUID(Blackhole bh) {
        bh.consume(IdUtil.fastUUID());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void fastSimpleUUID(Blackhole bh) {
        bh.consume(IdUtil.fastSimpleUUID());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void objectId(Blackhole bh) {
        bh.consume(IdUtil.objectId());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void nanoId(Blackhole bh) {
        bh.consume(IdUtil.nanoId());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void timeBasedGenerator(Blackhole bh) {
        bh.consume(Generators.timeBasedGenerator().generate());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void randomBasedGenerator(Blackhole bh) {
        bh.consume(Generators.randomBasedGenerator().generate());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void nameBasedGenerator(Blackhole bh) {
        bh.consume(Generators.nameBasedGenerator().generate("string to hash"));
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void timeBasedReorderedGenerator(Blackhole bh) {
        bh.consume(Generators.timeBasedReorderedGenerator().generate());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void timeBasedEpochGenerator(Blackhole bh) {
        bh.consume(Generators.timeBasedEpochGenerator().generate());
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.SECONDS)
    @Fork(value = 1, warmups = 0)
    @Threads(4) // 设置并发线程数为4
    public void timeBasedEpochRandomGenerator(Blackhole bh) {
        bh.consume(Generators.timeBasedEpochRandomGenerator().generate());
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