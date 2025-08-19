package uno.xifan.id.generator;

import uno.xifan.id.generator.distributed.cosid.CosIdGenerator;
import uno.xifan.id.generator.distributed.ttsid.TtsIdPlusGenerator;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Threads(16)
public class TtsIdVsCosIdBenchmark {

    private RedissonClient redisson;
    private TtsIdPlusGenerator ttsIdGenerator;
    private CosIdGenerator cosIdGenerator; // 假设CosId生成器已正确集成

    public static void main(String[] args) throws Exception {
        Options opt =
                new OptionsBuilder()
                        .include(TtsIdVsCosIdBenchmark.class.getSimpleName())
                        .timeUnit(TimeUnit.SECONDS)
                        .measurementIterations(5)
                        .forks(0)
                        .threads(Runtime.getRuntime().availableProcessors())
                        .measurementTime(TimeValue.seconds(10))
                        .build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        // Initialize Redisson
        Config config = new Config();
        config.useSingleServer().setAddress("redis://xifan.uno:6379").setPassword("qw3erT^&*()_+");
        redisson = Redisson.create(config);
        ttsIdGenerator = new TtsIdPlusGenerator(redisson);
        cosIdGenerator = new CosIdGenerator(redisson, 44, 20, 16, 0);
    }

    @TearDown
    public void tearDown() {
        if (redisson != null) {
            redisson.shutdown(); // Critical: shuts down threads
        }
    }

    @Benchmark
    public void generateTtsId(Blackhole bh) {
        bh.consume(ttsIdGenerator.generate());
    }

    @Benchmark
    public void generateCosId(Blackhole bh) {
        bh.consume(cosIdGenerator.generate());
    }
}
