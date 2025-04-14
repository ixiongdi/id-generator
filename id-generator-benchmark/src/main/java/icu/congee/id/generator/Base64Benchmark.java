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
public class Base64Benchmark {

    private byte[] TEST_BYTES;

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
    public String commonsBase64Encode() {
        return org.apache.commons.codec.binary.Base64.encodeBase64String(TEST_BYTES);
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
    public String nettyBase64Encode() {
        ByteBuf buf = Unpooled.wrappedBuffer(TEST_BYTES);
        try {
            return io.netty.handler.codec.base64.Base64.encode(buf).toString(java.nio.charset.StandardCharsets.UTF_8);
        } finally {
            buf.release();
        }
    }

    @Benchmark
    public byte[] jdkBase64Decode() {
        String encoded = java.util.Base64.getEncoder().encodeToString(TEST_BYTES);
        return java.util.Base64.getDecoder().decode(encoded);
    }

    @Benchmark
    public byte[] commonsBase64Decode() {
        String encoded = org.apache.commons.codec.binary.Base64.encodeBase64String(TEST_BYTES);
        return org.apache.commons.codec.binary.Base64.decodeBase64(encoded);
    }

    @Benchmark
    public byte[] hutoolBase64Decode() {
        String encoded = cn.hutool.core.codec.Base64.encode(TEST_BYTES);
        return cn.hutool.core.codec.Base64.decode(encoded);
    }

    @Benchmark
    public byte[] guavaBase64Decode() {
        String encoded = com.google.common.io.BaseEncoding.base64().encode(TEST_BYTES);
        return com.google.common.io.BaseEncoding.base64().decode(encoded);
    }

    @Benchmark
    public byte[] nettyBase64Decode() {
        ByteBuf buf = Unpooled.wrappedBuffer(TEST_BYTES);
        try {
            String encoded = io.netty.handler.codec.base64.Base64.encode(buf)
                    .toString(java.nio.charset.StandardCharsets.UTF_8);
            return io.netty.handler.codec.base64.Base64.decode(Unpooled.wrappedBuffer(encoded.getBytes())).array();
        } finally {
            buf.release();
        }
    }
}
