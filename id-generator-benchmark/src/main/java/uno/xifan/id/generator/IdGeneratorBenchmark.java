package uno.xifan.id.generator;

import uno.xifan.id.util.IdUtil;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * JMH基准测试类，用于评估IdUtil工具类中所有ID生成方法的性能
 *
 * <p>
 * 遵循JMH最佳实践配置：
 * - 5轮预热（Warmup）确保JIT编译优化
 * - 5轮测量（Measurement）保证结果稳定性
 * - 16线程并发模拟高负载场景
 * - 输出吞吐量（操作数/秒）
 *
 * @author 稀饭科技
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 2, warmups = 1)
@Threads(16)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
public class IdGeneratorBenchmark {

    /**
     * 主方法，用于运行基准测试
     *
     * @param args 命令行参数（未使用）
     * @throws RunnerException 如果基准测试运行过程中发生错误
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(IdGeneratorBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    // ------------------------- 基础ID生成方法测试 -------------------------

    @Benchmark
    public void testCombGuid(Blackhole blackhole) {
        String id = IdUtil.combguid();
        blackhole.consume(id);
    }

    @Benchmark
    public void testCuid1(Blackhole blackhole) {
        String id = IdUtil.cuid1();
        blackhole.consume(id);
    }

    @Benchmark
    public void testCuid2(Blackhole blackhole) {
        String id = IdUtil.cuid2();
        blackhole.consume(id);
    }

    @Benchmark
    public void testElasticflake(Blackhole blackhole) {
        String id = IdUtil.elasticflake();
        blackhole.consume(id);
    }

    @Benchmark
    public void testEntropyId(Blackhole blackhole) {
        long id = IdUtil.entropy();
        blackhole.consume(id);
    }

    @Benchmark
    public void testJavaScriptSafetyId(Blackhole blackhole) {
        long id = IdUtil.javaScriptSafetyId();
        blackhole.consume(id);
    }

    @Benchmark
    public void testKsuid(Blackhole blackhole) {
        String id = IdUtil.ksuid();
        blackhole.consume(id);
    }

    @Benchmark
    public void testLexicalUuid(Blackhole blackhole) {
        String id = IdUtil.lexicalUuid();
        blackhole.consume(id);
    }

    @Benchmark
    public void testNanoId(Blackhole blackhole) {
        String id = IdUtil.nanoId();
        blackhole.consume(id);
    }

    @Benchmark
    public void testObjectId(Blackhole blackhole) {
        String id = IdUtil.objectId();
        blackhole.consume(id);
    }

    @Benchmark
    public void testOrderedUuid(Blackhole blackhole) {
        String id = IdUtil.orderedUuid();
        blackhole.consume(id);
    }

    @Benchmark
    public void testPushId(Blackhole blackhole) {
        String id = IdUtil.pushId();
        blackhole.consume(id);
    }

    @Benchmark
    public void testSid(Blackhole blackhole) {
        String id = IdUtil.sid();
        blackhole.consume(id);
    }

    @Benchmark
    public void testBusinessId(Blackhole blackhole) {
        long id = IdUtil.businessId();
        blackhole.consume(id);
    }

    @Benchmark
    public void testUlid(Blackhole blackhole) {
        String id = IdUtil.ulid();
        blackhole.consume(id);
    }

    // ------------------------- UUID系列方法测试 -------------------------

    @Benchmark
    public void testUuid1(Blackhole blackhole) {
        String id = IdUtil.uuid1();
        blackhole.consume(id);
    }

    @Benchmark
    public void testUuid2(Blackhole blackhole) {
        String id = IdUtil.uuid2();
        blackhole.consume(id);
    }

    @Benchmark
    public void testUuid4(Blackhole blackhole) {
        String id = IdUtil.uuid4();
        blackhole.consume(id);
    }

    @Benchmark
    public void testUuid6(Blackhole blackhole) {
        String id = IdUtil.uuid6();
        blackhole.consume(id);
    }

    @Benchmark
    public void testUuid7(Blackhole blackhole) {
        String id = IdUtil.uuid7();
        blackhole.consume(id);
    }


    @Benchmark
    public void testXid(Blackhole blackhole) {
        String id = IdUtil.xid();
        blackhole.consume(id);
    }
}
