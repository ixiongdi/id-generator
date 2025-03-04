package com.github.ixiongdi.id.generator;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.time.Instant;

public class KSUID {
    // 自定义纪元：2014年5月13日16:53:20 UTC
    private static final long EPOCH = 1_400_000_000L;
    private static final int TIMESTAMP_BYTES = 4;  // 时间戳占 4 字节
    private static final int PAYLOAD_BYTES = 16;   // 负载占 16 字节
    private static final int TOTAL_BYTES = TIMESTAMP_BYTES + PAYLOAD_BYTES;
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final BigInteger BASE = BigInteger.valueOf(62);

    private final byte[] bytes;

    // 默认构造函数：生成新的 KSUID
    public KSUID() {
        this.bytes = new byte[TOTAL_BYTES];
        // 计算当前时间相对于纪元的秒数
        long timestamp = Instant.now().getEpochSecond() - EPOCH;
        if (timestamp < 0 || timestamp > 0xFFFFFFFFL) {
            throw new IllegalStateException("时间戳超出范围");
        }
        // 将时间戳写入大端序字节数组
        bytes[0] = (byte) (timestamp >>> 24);
        bytes[1] = (byte) (timestamp >>> 16);
        bytes[2] = (byte) (timestamp >>> 8);
        bytes[3] = (byte) timestamp;
        // 生成随机负载
        SecureRandom random = new SecureRandom();
        random.nextBytes(bytes);
    }

    // 从字节数组构造 KSUID
    public KSUID(byte[] bytes) {
        if (bytes.length != TOTAL_BYTES) {
            throw new IllegalArgumentException("KSUID 字节长度必须为 " + TOTAL_BYTES);
        }
        this.bytes = bytes.clone();
    }

    // 获取时间戳（相对于纪元的秒数）
    public long getTimestamp() {
        return ((bytes[0] & 0xFFL) << 24) |
               ((bytes[1] & 0xFFL) << 16) |
               ((bytes[2] & 0xFFL) << 8) |
               (bytes[3] & 0xFFL);
    }

    // 获取随机负载
    public byte[] getPayload() {
        byte[] payload = new byte[PAYLOAD_BYTES];
        System.arraycopy(bytes, TIMESTAMP_BYTES, payload, 0, PAYLOAD_BYTES);
        return payload;
    }

    // 编码为 Base62 字符串
    public String toBase62() {
        BigInteger num = new BigInteger(1, bytes);
        StringBuilder sb = new StringBuilder();
        while (num.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = num.divideAndRemainder(BASE);
            sb.append(BASE62_CHARS.charAt(divmod[1].intValue()));
            num = divmod[0];
        }
        // 填充至 27 个字符
        while (sb.length() < 27) {
            sb.append('0');
        }
        return sb.reverse().toString();
    }

    // 从 Base62 字符串解码
    public static KSUID fromBase62(String base62) {
        if (base62.length() != 27) {
            throw new IllegalArgumentException("Base62 字符串长度必须为 27");
        }
        BigInteger num = BigInteger.ZERO;
        for (int i = 0; i < base62.length(); i++) {
            char c = base62.charAt(i);
            int digit = BASE62_CHARS.indexOf(c);
            if (digit == -1) {
                throw new IllegalArgumentException("无效的 Base62 字符: " + c);
            }
            num = num.multiply(BASE).add(BigInteger.valueOf(digit));
        }
        byte[] bytes = num.toByteArray();
        // 处理前导零
        if (bytes.length < TOTAL_BYTES) {
            byte[] padded = new byte[TOTAL_BYTES];
            System.arraycopy(bytes, 0, padded, TOTAL_BYTES - bytes.length, bytes.length);
            bytes = padded;
        } else if (bytes.length > TOTAL_BYTES) {
            throw new IllegalArgumentException("Base62 字符串解码结果超过 20 字节");
        }
        return new KSUID(bytes);
    }

    // 重写 toString 方法，返回 Base62 表示
    @Override
    public String toString() {
        return toBase62();
    }

    // 示例使用
    public static void main(String[] args) {
        KSUID ksuid = new KSUID();
        System.out.println("生成的 KSUID: " + ksuid);
        System.out.println("时间戳: " + ksuid.getTimestamp());
        System.out.println("负载 (十六进制): " + bytesToHex(ksuid.getPayload()));

        // 解码测试
        String base62 = ksuid.toBase62();
        KSUID decoded = KSUID.fromBase62(base62);
        System.out.println("解码后的 KSUID: " + decoded);
    }

    // 将字节数组转换为十六进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}