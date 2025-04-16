package icu.congee.id.base;

import java.util.Arrays;

public class Base16 {

    private static final byte[] ENCODE_MAP;
    private static final byte[] DECODE_MAP;

    static {
        // 初始化编码映射表（0-9, A-F）
        ENCODE_MAP = new byte[16];
        for (int i = 0; i < 10; i++) {
            ENCODE_MAP[i] = (byte) ('0' + i);
        }
        for (int i = 10; i < 16; i++) {
            ENCODE_MAP[i] = (byte) ('A' + (i - 10));
        }

        // 初始化解码映射表
        DECODE_MAP = new byte[128];
        Arrays.fill(DECODE_MAP, (byte) -1);
        for (int i = 0; i < 10; i++) {
            DECODE_MAP['0' + i] = (byte) i;
        }
        for (int i = 0; i < 6; i++) {
            DECODE_MAP['A' + i] = (byte) (10 + i);
            DECODE_MAP['a' + i] = (byte) (10 + i); // 支持小写字母
        }
    }

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            result.append((char) ENCODE_MAP[(b >> 4) & 0x0F]);
            result.append((char) ENCODE_MAP[b & 0x0F]);
        }
        return result.toString();
    }

    public static byte[] decode(String str) {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }

        if (str.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid Base16 string length");
        }

        byte[] result = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char high = str.charAt(i);
            char low = str.charAt(i + 1);

            if (high >= 128 || low >= 128 ||
                    DECODE_MAP[high] == -1 || DECODE_MAP[low] == -1) {
                throw new IllegalArgumentException("Invalid Base16 character");
            }

            result[i / 2] = (byte) ((DECODE_MAP[high] << 4) | DECODE_MAP[low]);
        }

        return result;
    }
}