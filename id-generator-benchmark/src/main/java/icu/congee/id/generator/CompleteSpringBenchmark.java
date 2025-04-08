package icu.congee.id.generator;

import icu.congee.id.generator.distributed.broid.BroIdGenerator;
import icu.congee.id.generator.distributed.snowflake.SnowflakeIdGenerator;
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

    public SnowflakeIdGenerator snowflakeIdGenerator;
    public BroIdGenerator broIdGenerator;
    public ConfigurableApplicationContext context;

    public static void main(String[] args) throws Exception {
        Options opt =
                new OptionsBuilder()
                        .include(CompleteSpringBenchmark.class.getSimpleName())
                        .timeUnit(TimeUnit.SECONDS)
                        .mode(Mode.All)
                        .threads(16)
                        .forks(0)
                        .warmupIterations(0)
                        .warmupTime(TimeValue.seconds(0)) // 预热迭代次数
                        .measurementIterations(5)
                        .measurementTime(TimeValue.seconds(10))
                        .build();
        new Runner(opt).run();
    }

    @Setup(Level.Trial)
    public void init() {
        context = SpringApplication.run(IdGeneratorBenchmarkApplication.class);
        snowflakeIdGenerator = context.getBean(SnowflakeIdGenerator.class);
        broIdGenerator = context.getBean(BroIdGenerator.class);
    }

    @TearDown(Level.Trial)
    public void close() {
        context.close();
    }

    @Benchmark
    public void generateSnowflake(Blackhole bh) {
        bh.consume(snowflakeIdGenerator.generate());
    }

    @Benchmark
    public void generateBroId(Blackhole bh) {
        bh.consume(broIdGenerator.generate());
    }
}
