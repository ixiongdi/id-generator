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

/**
 * JMH基准测试类，用于评估不同UUID版本生成器的性能
 *
 * <p>
 * 该类使用JMH（Java Microbenchmark Harness）框架进行基准测试，
 * 测试了UUID v1到v7各个版本的生成性能。测试在16个并发线程下运行，
 * 以模拟真实的高并发场景。
 *
 * @author congee
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1, warmups = 0)
@Threads(16) // 设置并发线程数为4
public class IdGeneratorBenchmark {

    /** 用于生成随机字节的安全随机数生成器 */
    SecureRandom random = new SecureRandom();

    /**
     * 主方法，用于运行基准测试
     *
     * @param args 命令行参数（未使用）
     * @throws RunnerException 如果基准测试运行过程中发生错误
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IdGeneratorBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(1) // 预热迭代次数
                .measurementIterations(1)
                .build();
        new Runner(opt).run();
    }

    /**
     * 测试UUID v1生成器的性能
     * <p>
     * UUID v1基于时间戳和节点ID生成，适用于分布式系统
     */
    @Benchmark
    public void testUUIDv1() {
        UUIDv1Generator.next();
    }

    /**
     * 测试UUID v2生成器的性能
     * <p>
     * UUID v2基于DCE安全机制，包含域和标识符
     */
    @Benchmark
    public void testUUIDv2() {
        UUIDv2Generator.next();
    }

    /**
     * 测试UUID v3生成器的性能
     * <p>
     * UUID v3使用MD5哈希算法基于命名空间和名称生成
     */
    @Benchmark
    public void testUUIDv3() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        UUIDv3Generator.fromNamespaceAndName(UUID.randomUUID(), Arrays.toString(bytes));
    }

    /**
     * 测试UUID v4生成器的性能
     * <p>
     * UUID v4完全基于随机或伪随机数生成
     */
    @Benchmark
    public void testUUIDv4() {
        UUIDv4Generator.next();
    }

    /**
     * 测试UUID v5生成器的性能
     * <p>
     * UUID v5使用SHA-1哈希算法基于命名空间和名称生成
     */
    @Benchmark
    public void testUUIDv5() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        UUIDv5Generator.fromNamespaceAndName(UUID.randomUUID(), Arrays.toString(bytes));
    }

    /**
     * 测试UUID v6生成器的性能
     * <p>
     * UUID v6是v1的改进版本，提供更好的时序性
     */
    @Benchmark
    public void testUUIDv6() {
        UUIDv6Generator.next();
    }

    /**
     * 测试UUID v7生成器的性能
     * <p>
     * UUID v7基于Unix时间戳，提供严格的时序性
     */
    @Benchmark
    public void testUUIDv7() {
        UUIDv7Generator.next();
    }
}
