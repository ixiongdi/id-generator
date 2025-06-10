package icu.congee.id.generator;

import icu.congee.id.generator.distributed.ttsid.*;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(1)
@Threads(100)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 5, time = 10)
public class TtsIdBenchmark {

    private RedissonClient redisson;

    private TtsIdGenerator ttsIdGenerator;
    private TtsIdMiniGenerator ttsIdMiniGenerator;
    private TtsIdPlusGenerator ttsIdPlusGenerator;
    private TtsIdProGenerator ttsIdProGenerator;
    private TtsIdProMaxGenerator ttsIdProMaxGenerator;

    @Setup
    public void setup() {
        // 初始化Redis连接（与现有测试保持一致）
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://congee.icu:6379")
                .setPassword("qw3erT^&*()_+")
                .setConnectionMinimumIdleSize(100)
                .setConnectionPoolSize(100);
        redisson = Redisson.create(config);
        ttsIdGenerator = new TtsIdGenerator(redisson);
        ttsIdMiniGenerator = new TtsIdMiniGenerator(redisson);
        ttsIdPlusGenerator = new TtsIdPlusGenerator(redisson);
        ttsIdProGenerator = new TtsIdProGenerator(redisson);
        ttsIdProMaxGenerator = new TtsIdProMaxGenerator(redisson);
    }

    @TearDown
    public void tearDown() {
        if (redisson != null) {
            redisson.shutdown();
        }
    }

    @Benchmark
    public void testTtsId(Blackhole bh) {
        bh.consume(ttsIdGenerator.generate());
    }

    @Benchmark
    public void testTtsIdMni(Blackhole bh) {
        bh.consume(ttsIdMiniGenerator.generate());
    }

    @Benchmark
    public void testTtsIdPlus(Blackhole bh) {
        bh.consume(ttsIdPlusGenerator.generate());
    }

    @Benchmark
    public void testTtsIdPro(Blackhole bh) {
        bh.consume(ttsIdProGenerator.generate());
    }

    @Benchmark
    public void testTtsIdProMax(Blackhole bh) {
        bh.consume(ttsIdProMaxGenerator.generate());
    }
}
