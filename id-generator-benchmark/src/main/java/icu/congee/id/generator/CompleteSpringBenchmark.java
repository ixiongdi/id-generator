package icu.congee.id.generator;

import icu.congee.id.generator.distributed.snowflake.LockFreeSnowflakeIdGenerator;
import icu.congee.id.generator.distributed.snowflake.SnowflakeIdGenerator;
import icu.congee.id.generator.distributed.ttsid.TtsIdGenerator;
import icu.congee.id.generator.distributed.ttsid.TtsIdPlusGenerator;
import icu.congee.id.generator.distributed.ttsid.TtsIdProGenerator;
import icu.congee.id.generator.distributed.ttsid.TtsIdProMaxGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class CompleteSpringBenchmark {

    @Param({"1", "24"}) // 测试不同的线程数
    private int threadCount;


    public SnowflakeIdGenerator snowflakeIdGenerator;
    public LockFreeSnowflakeIdGenerator lockFreeSnowflakeIdGenerator;
    public ConfigurableApplicationContext context;

    public TtsIdGenerator ttsIdGenerator;
    public TtsIdPlusGenerator ttsIdPlusGenerator;
    public TtsIdProGenerator ttsIdProGenerator;
    public TtsIdProMaxGenerator ttsIdProMaxGenerator;

    public static void main(String[] args) throws Exception {
        Options opt =
                new OptionsBuilder()
                        .include(CompleteSpringBenchmark.class.getSimpleName())
                        .timeUnit(TimeUnit.SECONDS)
                        .mode(Mode.Throughput)
                        .forks(0)
                        .warmupIterations(0)
                        .warmupTime(TimeValue.seconds(0)) // 预热迭代次数
                        .measurementIterations(1)
                        .measurementTime(TimeValue.seconds(10))
                        .timeout(TimeValue.seconds(15))
                        .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void init() {
        context = SpringApplication.run(IdGeneratorBenchmarkApplication.class);
//        snowflakeIdGenerator = context.getBean(SnowflakeIdGenerator.class);
//        lockFreeSnowflakeIdGenerator = context.getBean(LockFreeSnowflakeIdGenerator.class);
//        broIdGenerator = context.getBean(BroIdGenerator.class);
        ttsIdGenerator = context.getBean(TtsIdGenerator.class);
        ttsIdPlusGenerator = context.getBean(TtsIdPlusGenerator.class);
        ttsIdProGenerator = context.getBean(TtsIdProGenerator.class);
        ttsIdProMaxGenerator = context.getBean(TtsIdProMaxGenerator.class);
    }

    @TearDown(Level.Trial)
    public void close() {
        context.close();
    }

//    @Benchmark
    public void generateSnowflake(Blackhole bh) {
        bh.consume(snowflakeIdGenerator.generate());
    }

//    @Benchmark
    public void generateLockFreeSnowflake(Blackhole bh) {
        bh.consume(lockFreeSnowflakeIdGenerator.generate());
    }

    @Benchmark
    @Threads(1)
    public void generateTtsId() {
        ttsIdGenerator.generate();
    }

    @Benchmark
    @Threads(1)
    public void generateTtsIdPlus() {
        ttsIdPlusGenerator.generate();
    }

    @Benchmark
    @Threads(1)
    public void generateTtsIdPro() {
        ttsIdProGenerator.generate();
    }

    @Benchmark
    @Threads(1)
    public void generateTtsIdProMax() {
        ttsIdProMaxGenerator.generate();
    }
}