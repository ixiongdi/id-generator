package icu.congee.id.base;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Base62Codec {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 将字节数组编码为 Base62 字符串
    public static String encode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long number = buffer.getLong();
        if (number == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.insert(0, BASE62_ALPHABET.charAt(remainder));
            number /= 62;
        }
        return sb.toString();
    }

    // 将 Base62 字符串解码为字节数组
    public static byte[] decode(String base62) {
        long result = 0;
        for (int i = 0; i < base62.length(); i++) {
            char c = base62.charAt(i);
            int digit = BASE62_ALPHABET.indexOf(c);
            result = result * 62 + digit;
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(result);
        return buffer.array();
    }

    public static void main(String[] args) {
        byte[] inputBytes = {1, 2, 3, 4, 5, 6, 7, 8};
        String encoded = encode(inputBytes);
        byte[] decoded = decode(encoded);
        System.out.println("Base62 编码前的字节数组: " + Arrays.toString(inputBytes));
        System.out.println("Base62 编码后的字符串: " + encoded);
        System.out.println("Base62 解码后的字节数组: " + Arrays.toString(decoded));
    }
}