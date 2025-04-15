package icu.congee.id.generator;

import org.apache.commons.codec.binary.Hex;
import com.google.common.io.BaseEncoding;
import cn.hutool.core.util.HexUtil;
import org.openjdk.jmh.annotations.*;

import java.util.HexFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Threads(16)
@Fork(value = 1, warmups = 0)
@Warmup(iterations = 1, time = 1)
@Measurement(iterations = 1, time = 1)
public class HexBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.options.Options opt = new org.openjdk.jmh.runner.options.OptionsBuilder()
                .include(HexBenchmark.class.getSimpleName())
                .build();
        new org.openjdk.jmh.runner.Runner(opt).run();
    }

    private byte[] testData;

    @Setup
    public void setUp() {
        testData = new byte[16];
        ThreadLocalRandom.current().nextBytes(testData);
    }

    @Benchmark
    public void apacheCommonsCodec(org.openjdk.jmh.infra.Blackhole bh) {
        bh.consume(Hex.encodeHexString(testData));
    }

    @Benchmark
    public void googleGuava(org.openjdk.jmh.infra.Blackhole bh) {
        bh.consume(BaseEncoding.base16().encode(testData));
    }

    @Benchmark
    public void hutool(org.openjdk.jmh.infra.Blackhole bh) {
        bh.consume(HexUtil.encodeHexStr(testData));
    }

    @Benchmark
    public void jdk(org.openjdk.jmh.infra.Blackhole bh) {
        bh.consume(HexFormat.of().formatHex(testData));
    }

    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(java.nio.charset.StandardCharsets.US_ASCII);

    public static String bytesToHex(byte[] bytes) {
        byte[] hexBytes = new byte[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexBytes[i * 2] = HEX_ARRAY[v >>> 4];
            hexBytes[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexBytes, java.nio.charset.StandardCharsets.US_ASCII);
    }
}
