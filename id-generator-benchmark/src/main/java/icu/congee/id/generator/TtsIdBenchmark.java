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
@Threads(1024)
@Warmup(iterations = 3, time = 10)
@Measurement(iterations = 5, time = 10)
public class TtsIdBenchmark {

    private RedissonClient redisson;

    private TtsIdMiniGenerator ttsIdMiniGenerator;
    private TtsIdGenerator ttsIdGenerator;
    private TtsIdPlusGenerator ttsIdPlusGenerator;
    private TtsIdProGenerator ttsIdProGenerator;
    private TtsIdProMaxGenerator ttsIdProMaxGenerator;

    @Setup
    public void setup() {
        // 初始化Redis连接（与现有测试保持一致）
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379")
//                .setPassword("qw3erT^&*()_+")
                .setConnectionMinimumIdleSize(1024)
                .setConnectionPoolSize(1024);
        redisson = Redisson.create(config);
        ttsIdMiniGenerator = new TtsIdMiniGenerator(redisson, new TtsIdMiniGeneratorConfig());
        ttsIdGenerator = new TtsIdGenerator(redisson, new TtsIdGeneratorConfig());
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
    public void testTtsIdMni(Blackhole bh) {
        bh.consume(ttsIdMiniGenerator.generate().toLong());
    }

    @Benchmark
    public void testTtsId(Blackhole bh) {
        bh.consume(ttsIdGenerator.generate().toLong());
    }

    @Benchmark
    public void testTtsIdPlus(Blackhole bh) {
        bh.consume(ttsIdPlusGenerator.generate().toBase32());
    }

    @Benchmark
    public void testTtsIdPro(Blackhole bh) {
        bh.consume(ttsIdProGenerator.generate().toBase16());
    }

    @Benchmark
    public void testTtsIdProMax(Blackhole bh) {
        bh.consume(ttsIdProMaxGenerator.generate().toBase16());
    }
}
