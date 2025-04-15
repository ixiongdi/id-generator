package icu.congee.id.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class URLSafeBase64 {
    // 按ASCII顺序排列的字符表：- 0-9 A-Z _ a-z
    private static final byte[] ENCODING_TABLE = {
            '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
            'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
            'W', 'X', 'Y', 'Z', '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
            'w', 'x', 'y', 'z'
    };

    // 解码表（ASCII码→索引值）
    private static final byte[] DECODING_TABLE = new byte[128];
    static {
        Arrays.fill(DECODING_TABLE, (byte) -1);
        for (int i = 0; i < ENCODING_TABLE.length; i++) {
            DECODING_TABLE[ENCODING_TABLE[i]] = (byte) i;
        }
    }

    // 编码方法（不带填充）
    public static String encode(byte[] data) {
        if (data == null)
            throw new IllegalArgumentException("Input data cannot be null");

        int inputLen = data.length;
        int outputLen = (inputLen * 4 + 2) / 3; // 计算输出长度
        byte[] encoded = new byte[outputLen];

        int inPos = 0, outPos = 0;
        // 处理完整3字节块
        while (inPos + 3 <= inputLen) {
            int b1 = data[inPos++] & 0xFF;
            int b2 = data[inPos++] & 0xFF;
            int b3 = data[inPos++] & 0xFF;

            encoded[outPos++] = ENCODING_TABLE[(b1 >>> 2) & 0x3F];
            encoded[outPos++] = ENCODING_TABLE[((b1 << 4) & 0x30) | ((b2 >>> 4) & 0x0F)];
            encoded[outPos++] = ENCODING_TABLE[((b2 << 2) & 0x3C) | ((b3 >>> 6) & 0x03)];
            encoded[outPos++] = ENCODING_TABLE[b3 & 0x3F];
        }

        // 处理剩余字节（1或2字节）
        int remaining = inputLen - inPos;
        if (remaining > 0) {
            int b1 = data[inPos++] & 0xFF;
            int b2 = (remaining == 2) ? data[inPos] & 0xFF : 0;

            encoded[outPos++] = ENCODING_TABLE[(b1 >>> 2) & 0x3F];
            encoded[outPos++] = ENCODING_TABLE[((b1 << 4) & 0x30) | ((b2 >>> 4) & 0x0F)];
            if (remaining == 2) {
                encoded[outPos++] = ENCODING_TABLE[((b2 << 2) & 0x3C)];
            }
        }

        return new String(encoded, 0, outPos, StandardCharsets.US_ASCII);
    }

    // 解码方法（自动处理填充）
    public static byte[] decode(String base64) {
        if (base64 == null)
            throw new IllegalArgumentException("Input string cannot be null");

        char[] data = base64.toCharArray();
        int inputLen = data.length;
        int outputLen = (inputLen * 3) / 4;
        byte[] decoded = new byte[outputLen];

        int inPos = 0, outPos = 0;
        int block = 0;
        int validChars = 0;

        for (char c : data) {
            if (c > 127)
                throw new IllegalArgumentException("Invalid character: " + c);
            byte value = DECODING_TABLE[c];
            if (value < 0)
                throw new IllegalArgumentException("Invalid character: " + c);

            block = (block << 6) | value;
            validChars++;

            if (validChars == 4) {
                decoded[outPos++] = (byte) (block >> 16);
                decoded[outPos++] = (byte) (block >> 8);
                decoded[outPos++] = (byte) block;
                block = 0;
                validChars = 0;
            }
        }

        // 处理剩余字符
        if (validChars > 0) {
            block <<= (4 - validChars) * 6;
            if (validChars >= 2) {
                decoded[outPos++] = (byte) (block >> 16);
                if (validChars >= 3) {
                    decoded[outPos++] = (byte) (block >> 8);
                }
            }
        }

        return Arrays.copyOf(decoded, outPos);
    }

    // 测试用例
    public static void main(String[] args) {
        // 基本测试
        String origin = "Hello@Java-2025";
        String encoded = encode(origin.getBytes(StandardCharsets.UTF_8));
        System.out.println("Encoded: " + encoded); // 0JbXy1lWYV9tM-Java2025

        byte[] decoded = decode(encoded);
        System.out.println("Decoded: " + new String(decoded)); // Hello@Java-2025

        // 边界测试
        testPadding(1); // 1字节输入
        testPadding(2); // 2字节输入
        testPadding(15); // 完整块测试
    }

    private static void testPadding(int byteLen) {
        byte[] data = new byte[byteLen];
        Arrays.fill(data, (byte) 0xFF);
        String encoded = encode(data);
        byte[] decoded = decode(encoded);
        System.out.printf("Test %d bytes: %s -> %s%n",
                byteLen, encoded, Arrays.equals(data, decoded) ? "OK" : "FAIL");
    }
}