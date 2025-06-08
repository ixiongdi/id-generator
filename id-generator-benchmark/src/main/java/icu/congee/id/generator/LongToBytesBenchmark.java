package icu.congee.id.generator;import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class LongToBytesBenchmark {
    private long testValue = System.currentTimeMillis();

    // 方法一：手动位移转换
    public static byte[] longToBytesManual(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    // 方法二：使用ByteBuffer
    public static byte[] longToBytesByteBuffer(long value) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(value);
        return buffer.array();
    }

    // 方法三：使用Unsafe
    private static final Unsafe unsafe;
    private static final long BYTE_ARRAY_BASE_OFFSET;

    static {
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);
            BYTE_ARRAY_BASE_OFFSET = unsafe.arrayBaseOffset(byte[].class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] longToBytesUnsafe(long value) {
        byte[] bytes = new byte[8];
        unsafe.putLong(bytes, BYTE_ARRAY_BASE_OFFSET, value);
        return bytes;
    }

    @Benchmark
    public byte[] testManual() {
        return longToBytesManual(testValue);
    }

    @Benchmark
    public byte[] testByteBuffer() {
        return longToBytesByteBuffer(testValue);
    }

    @Benchmark
    public byte[] testUnsafe() {
        return longToBytesUnsafe(testValue);
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(LongToBytesBenchmark.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }
}    