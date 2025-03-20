package icu.congee.id.generator;

import icu.congee.id.generator.uuid.*;

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

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, warmups = 0)
@Threads(16) // 设置并发线程数为4
public class IdGeneratorBenchmark {

    SecureRandom random = new SecureRandom();

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
    public void testUUIDv1() {
        UUIDv1Generator.next();
    }

    @Benchmark
    public void testUUIDv2() {
        UUIDv2Generator.next();
    }

    @Benchmark
    public void testUUIDv3() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        UUIDv3Generator.fromNamespaceAndName(UUID.randomUUID(), Arrays.toString(bytes));
    }

    @Benchmark
    public void testUUIDv4() {
        UUIDv4Generator.next();
    }

    @Benchmark
    public void testUUIDv5() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        UUIDv5Generator.fromNamespaceAndName(UUID.randomUUID(), Arrays.toString(bytes));
    }

    @Benchmark
    public void testUUIDv6() {
        UUIDv6Generator.next();
    }

    @Benchmark
    public void testUUIDv7() {
        UUIDv7Generator.next();
    }
}
