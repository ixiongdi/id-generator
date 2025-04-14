package icu.congee.id.generator;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 5, time = 1)
public class Base64DecodeBenchmark {

    private byte[] TEST_BYTES;
    private String JDK_ENCODED;
    private String COMMONS_ENCODED;
    private String HUTOOL_ENCODED;
    private String GUAVA_ENCODED;
    private String NETTY_ENCODED;

    @Setup
    public void setup() {
        TEST_BYTES = new byte[16];
        new java.security.SecureRandom().nextBytes(TEST_BYTES);
        JDK_ENCODED = java.util.Base64.getEncoder().encodeToString(TEST_BYTES);
        COMMONS_ENCODED = org.apache.commons.codec.binary.Base64.encodeBase64String(TEST_BYTES);
        HUTOOL_ENCODED = cn.hutool.core.codec.Base64.encode(TEST_BYTES);
        GUAVA_ENCODED = com.google.common.io.BaseEncoding.base64().encode(TEST_BYTES);
        ByteBuf buf = Unpooled.wrappedBuffer(TEST_BYTES);
        try {
            NETTY_ENCODED = io.netty.handler.codec.base64.Base64.encode(buf)
                    .toString(java.nio.charset.StandardCharsets.UTF_8);
        } finally {
            buf.release();
        }
    }

    @Benchmark
    public byte[] jdkBase64Decode() {
        return java.util.Base64.getDecoder().decode(JDK_ENCODED);
    }

    @Benchmark
    public byte[] commonsBase64Decode() {
        return org.apache.commons.codec.binary.Base64.decodeBase64(COMMONS_ENCODED);
    }

    @Benchmark
    public byte[] hutoolBase64Decode() {
        return cn.hutool.core.codec.Base64.decode(HUTOOL_ENCODED);
    }

    @Benchmark
    public byte[] guavaBase64Decode() {
        return com.google.common.io.BaseEncoding.base64().decode(GUAVA_ENCODED);
    }

    @Benchmark
    public byte[] nettyBase64Decode() {
        return io.netty.handler.codec.base64.Base64.decode(
                Unpooled.wrappedBuffer(NETTY_ENCODED.getBytes())).array();
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.runner.options.Options opt = new org.openjdk.jmh.runner.options.OptionsBuilder()
                .include(Base64DecodeBenchmark.class.getSimpleName())
                .forks(1)
                .build();
        new org.openjdk.jmh.runner.Runner(opt).run();
    }
}