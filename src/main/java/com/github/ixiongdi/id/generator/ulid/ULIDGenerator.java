package com.github.ixiongdi.id.generator.ulid;

import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicLong;

public class ULIDGenerator {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final AtomicLong LAST_TIMESTAMP = new AtomicLong();
    private static final char[] CROCKFORD_CHARS = "0123456789ABCDEFGHJKMNPQRSTVWXYZ".toCharArray();

    public static String next() {
        long timestamp = System.currentTimeMillis();
        long last = LAST_TIMESTAMP.getAndUpdate(prev -> {
            if (timestamp > prev) return timestamp;
            return prev + 1;
        });
        
        byte[] entropy = new byte[10];
        RANDOM.nextBytes(entropy);
        
        return encode(timestamp, entropy);
    }

    private static String encode(long timestamp, byte[] entropy) {
        char[] chars = new char[26];
        
        // 编码48位时间戳
        for (int i = 4; i >= 0; i--) {
            int bits = (int) ((timestamp >>> (i * 10)) & 0x1F);
            chars[4 - i] = CROCKFORD_CHARS[bits];
        }
        
        // 编码80位随机数
        long hi = ((entropy[0] & 0xFFL) << 32) | ((entropy[1] & 0xFFL) << 24)
                | ((entropy[2] & 0xFFL) << 16) | ((entropy[3] & 0xFFL) << 8)
                | (entropy[4] & 0xFFL);
        
        long lo = ((entropy[5] & 0xFFL) << 32) | ((entropy[6] & 0xFFL) << 24)
                | ((entropy[7] & 0xFFL) << 16) | ((entropy[8] & 0xFFL) << 8)
                | (entropy[9] & 0xFFL);
        
        for (int i = 0; i < 10; i++) {
            int shift = (9 - i) * 5;
            int bits = (int) ((hi >>> shift) & 0x1F);
            chars[5 + i] = CROCKFORD_CHARS[bits];
        }
        
        for (int i = 0; i < 11; i++) {
            int shift = (10 - i) * 5;
            int bits = (int) ((lo >>> shift) & 0x1F);
            chars[15 + i] = CROCKFORD_CHARS[bits];
        }
        
        return new String(chars);
    }
}