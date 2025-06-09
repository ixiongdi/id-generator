package icu.congee.id.generator;

import icu.congee.id.generator.distributed.atomiclong.AtomicLongIdGenerator;
import icu.congee.id.generator.distributed.cosid.CosIdGenerator;
import icu.congee.id.generator.distributed.dtsid.DtsIdGenerator;
import icu.congee.id.generator.distributed.mist.MistIdGenerator;
import icu.congee.id.generator.distributed.rid.RedissonIdGenerator;
import icu.congee.id.generator.distributed.segmentid.SegmentChainIdGenerator;
import icu.congee.id.generator.distributed.snowflake.SnowflakeIdGenerator;
import icu.congee.id.generator.distributed.ttsid.TtsIdPlusGenerator;
import icu.congee.id.generator.distributed.wxseq.WxSeqGenerator;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

/**
 * 分布式ID生成器基准测试类
 * 测试路径：/c:/Users/76932/ktnb/id-generater/id-generator-spring-redis/src/main/java/icu/congee/id/generator/distributed/
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Threads(16)
@Warmup(iterations = 3, time = 10, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 10, timeUnit = TimeUnit.SECONDS)
public class DistributedIdBenchmark {

    private RedissonClient redisson;
    private AtomicLongIdGenerator atomicLongIdGenerator;
    private CosIdGenerator cosIdGenerator;
    private DtsIdGenerator dtsIdGenerator;
    private MistIdGenerator mistIdGenerator;
    private RedissonIdGenerator redissonIdGenerator;
    private SnowflakeIdGenerator snowflakeIdGenerator;
    private TtsIdPlusGenerator ttsIdPlusGenerator;
    private WxSeqGenerator wxSeqGenerator;
    private SegmentChainIdGenerator segmentChainIdGenerator;

    public static void main(String[] args) throws RunnerException {
        Options opt =
                new OptionsBuilder().include(DistributedIdBenchmark.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        // 初始化Redis连接（与现有测试保持一致）
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://congee.icu:6379")
                .setPassword("qw3erT^&*()_+")
                .setConnectionMinimumIdleSize(16)
                .setConnectionPoolSize(16);
        redisson = Redisson.create(config);

        // 初始化各分布式ID生成器（根据实际构造函数参数调整）
        atomicLongIdGenerator = new AtomicLongIdGenerator(redisson);
        cosIdGenerator = new CosIdGenerator(redisson, 44, 20, 16, 0);
        dtsIdGenerator = new DtsIdGenerator(redisson);
        mistIdGenerator = new MistIdGenerator(redisson, "mist", 0, true, 1000);
        redissonIdGenerator = new RedissonIdGenerator(redisson, "rid", 0, 1000);
        snowflakeIdGenerator = new SnowflakeIdGenerator(redisson, 0, 41, 10, 12); // 示例机器ID
        ttsIdPlusGenerator = new TtsIdPlusGenerator(redisson);
        wxSeqGenerator = new WxSeqGenerator(redisson);
        segmentChainIdGenerator = new SegmentChainIdGenerator(redisson);
    }

    @TearDown
    public void tearDown() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }

    @Benchmark
    public void testAtomicLongId(Blackhole bh) {
        bh.consume(atomicLongIdGenerator.generate());
    }

    @Benchmark
    public void testCosId(Blackhole bh) {
        bh.consume(cosIdGenerator.generate());
    }

    @Benchmark
    public void testDtsId(Blackhole bh) {
        bh.consume(dtsIdGenerator.generate());
    }

    @Benchmark
    public void testMistId(Blackhole bh) {
        bh.consume(mistIdGenerator.generate());
    }

    @Benchmark
    public void testRedissonId(Blackhole bh) {
        bh.consume(redissonIdGenerator.generate());
    }

    @Benchmark
    public void testSnowflakeId(Blackhole bh) {
        bh.consume(snowflakeIdGenerator.generate());
    }

    @Benchmark
    public void testTtsIdPlus(Blackhole bh) {
        bh.consume(ttsIdPlusGenerator.generate());
    }

    @Benchmark
    public void testWxSeq(Blackhole bh) {
        bh.consume(wxSeqGenerator.generate());
    }

    @Benchmark
    public void testSegmentChain(Blackhole bh) {
        bh.consume(segmentChainIdGenerator.generate());
    }
}
