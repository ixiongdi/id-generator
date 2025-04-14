package icu.congee.id.generator;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Threads(16)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class Base64EncodeBenchmark {

    private byte[] TEST_BYTES;

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.options.Options opt = new org.openjdk.jmh.runner.options.OptionsBuilder().include(Base64EncodeBenchmark.class.getSimpleName()).build();
        new org.openjdk.jmh.runner.Runner(opt).run();
    }

    @Setup
    public void setup() {
        TEST_BYTES = new byte[16];
        new java.security.SecureRandom().nextBytes(TEST_BYTES);
    }

    @Benchmark
    public String jdkBase64Encode() {
        return java.util.Base64.getEncoder().encodeToString(TEST_BYTES);
    }

    @Benchmark
    public String hutoolBase64Encode() {
        return cn.hutool.core.codec.Base64.encode(TEST_BYTES);
    }

    @Benchmark
    public String guavaBase64Encode() {
        return com.google.common.io.BaseEncoding.base64().encode(TEST_BYTES);
    }

    @Benchmark
    public String lexicalBase64Encode() {
        return icu.congee.id.base.util.LexicalBase64.encode(TEST_BYTES);
    }
}