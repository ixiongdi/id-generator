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

    @Benchmark
    public void bytes2hex(org.openjdk.jmh.infra.Blackhole bh) {
        bh.consume(bytesToHex(testData));
    }


    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        final int length = bytes.length;
        final char[] hexChars = new char[length << 1]; // 位运算代替 *2
        for (int i = 0; i < length; i++) {
            final int value = bytes[i] & 0xFF;
            final int index = i << 1; // 位运算代替 *2
            hexChars[index]     = HEX_ARRAY[value >>> 4]; // 高4位
            hexChars[index + 1] = HEX_ARRAY[value & 0x0F]; // 低4位
        }
        return new String(hexChars);
    }
}
