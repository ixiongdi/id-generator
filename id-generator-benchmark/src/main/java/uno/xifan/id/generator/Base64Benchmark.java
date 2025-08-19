package uno.xifan.id.generator;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;

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

}
