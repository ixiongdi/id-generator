package uno.xifan.id.generator;

import org.apache.commons.codec.binary.Base32;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Threads(16)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class Base32Benchmark {


    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder().include(Base32Benchmark.class.getSimpleName()).build();
        new Runner(opt).run();
    }

    private byte[] TEST_BYTES;

    Base32 base32 = new Base32();


    @Setup
    public void setup() {
        TEST_BYTES = new byte[16];
        new java.security.SecureRandom().nextBytes(TEST_BYTES);
    }

    @Benchmark
    public String jdkBase32Encode() {
        return java.util.HexFormat.of().formatHex(TEST_BYTES);
    }

    @Benchmark
    public String commonsBase32Encode() {
        return base32.encodeToString(TEST_BYTES);
    }

    @Benchmark
    public String hutoolBase32Encode() {
        return cn.hutool.core.codec.Base32.encode(TEST_BYTES);
    }

    @Benchmark
    public String guavaBase32Encode() {
        return com.google.common.io.BaseEncoding.base32().encode(TEST_BYTES);
    }
}