package icu.congee.id.base;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class Base36Codec {
    private static final String BASE36_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    // 将字节数组编码为 Base36 字符串
    public static String encode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long number = buffer.getLong();
        if (number == 0) {
            return "0";
        }
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = (int) (number % 36);
            sb.insert(0, BASE36_ALPHABET.charAt(remainder));
            number /= 36;
        }
        return sb.toString();
    }

    // 将 Base36 字符串解码为字节数组
    public static byte[] decode(String base36) {
        long result = 0;
        base36 = base36.toUpperCase();
        for (int i = 0; i < base36.length(); i++) {
            char c = base36.charAt(i);
            int digit = BASE36_ALPHABET.indexOf(c);
            result = result * 36 + digit;
        }
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(result);
        return buffer.array();
    }

    public static void main(String[] args) {
        byte[] inputBytes = {1, 2, 3, 4, 5, 6, 7, 8};
        String encoded = encode(inputBytes);
        byte[] decoded = decode(encoded);
        System.out.println("Base36 编码前的字节数组: " + Arrays.toString(inputBytes));
        System.out.println("Base36 编码后的字符串: " + encoded);
        System.out.println("Base36 解码后的字节数组: " + Arrays.toString(decoded));
    }
}