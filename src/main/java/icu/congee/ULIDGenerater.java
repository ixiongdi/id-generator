package icu.congee;

import java.security.SecureRandom;
import java.time.Instant;

public class ULIDGenerater implements IDGenerater {
    // Crockford的Base32编码字符集，避免混淆字符（如I、L、O、U）
    private static final char[] ENCODING_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
        'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
    };

    // 用于生成随机部分的SecureRandom实例
    private final SecureRandom random = new SecureRandom();

    @Override
    public String generateId() {
        return generateULID();
    }


    @Override
    public String getType() {
        return "ULID";
    }

    // 生成ULID字符串
    private String generateULID() {
        Instant now = Instant.now();
        long timestamp = now.toEpochMilli(); // 获取当前毫秒时间戳
        byte[] randomness = new byte[10];    // 10字节随机数（80位）
        random.nextBytes(randomness);

        StringBuilder sb = new StringBuilder(26);
        encodeTimestamp(timestamp, sb); // 编码时间戳部分
        encodeRandomness(randomness, sb); // 编码随机部分

        return sb.toString();
    }

    // 编码时间戳（48位，10个字符）
    private void encodeTimestamp(long timestamp, StringBuilder sb) {
        for (int i = 9; i >= 0; i--) {
            int charIndex = (int) (timestamp & 0x1F); // 取低5位
            sb.append(ENCODING_CHARS[charIndex]);
            timestamp >>= 5; // 右移5位
        }
    }

    // 编码随机部分（80位，16个字符）
    private void encodeRandomness(byte[] randomness, StringBuilder sb) {
        for (int i = 0; i < 10; i++) {
            int charIndex = randomness[i] & 0x1F; // 取低5位
            sb.append(ENCODING_CHARS[charIndex]);
        }
    }
}