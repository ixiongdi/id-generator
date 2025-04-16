package icu.congee.id.base;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Base62编码解码实现，使用数字、大写字母、小写字母
 */
public class Base62 {
    private static final int STANDARD_BASE = 256;
    private static final int TARGET_BASE = 62;
    private static final byte[] ENCODE_MAP = new byte[62];
    private static final byte[] DECODE_MAP = new byte[128];

    static {
        // 初始化编码映射表（0-9, A-Z, a-z）
        int index = 0;
        // 数字 0-9
        for (int i = 0; i < 10; i++) {
            ENCODE_MAP[index] = (byte) ('0' + i);
            index++;
        }
        // 大写字母 A-Z
        for (int i = 0; i < 26; i++) {
            ENCODE_MAP[index] = (byte) ('A' + i);
            index++;
        }
        // 小写字母 a-z
        for (int i = 0; i < 26; i++) {
            ENCODE_MAP[index] = (byte) ('a' + i);
            index++;
        }

        // 初始化解码映射表
        Arrays.fill(DECODE_MAP, (byte) -1);
        for (int i = 0; i < ENCODE_MAP.length; i++) {
            DECODE_MAP[ENCODE_MAP[i]] = (byte) i;
        }
    }

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        // 计算结果长度
        final int estimatedLength = estimateOutputLength(bytes.length, STANDARD_BASE, TARGET_BASE);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);

        byte[] source = bytes;
        while (source.length > 0) {
            final ByteArrayOutputStream quotient = new ByteArrayOutputStream(source.length);
            int remainder = 0;

            for (byte b : source) {
                final int accumulator = (b & 0xFF) + remainder * STANDARD_BASE;
                final int digit = (accumulator - (accumulator % TARGET_BASE)) / TARGET_BASE;
                remainder = accumulator % TARGET_BASE;

                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit);
                }
            }

            out.write(remainder);
            source = quotient.toByteArray();
        }

        // 处理前导零
        for (int i = 0; i < bytes.length - 1 && bytes[i] == 0; i++) {
            out.write(0);
        }

        // 转换为Base62字符串
        byte[] indices = ArrayUtil.reverse(out.toByteArray());
        byte[] encoded = new byte[indices.length];
        for (int i = 0; i < indices.length; i++) {
            encoded[i] = ENCODE_MAP[indices[i]];
        }

        return new String(encoded);
    }

    public static byte[] decode(String str) {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }

        // 转换为数字索引
        byte[] indices = new byte[str.length()];
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            if (c >= 128 || DECODE_MAP[c] == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }
            indices[i] = DECODE_MAP[c];
        }

        return convert(indices, TARGET_BASE, STANDARD_BASE);
    }

    private static int estimateOutputLength(int inputLength, int sourceBase, int targetBase) {
        return (int) Math.ceil((Math.log(sourceBase) / Math.log(targetBase)) * inputLength);
    }

    private static byte[] convert(byte[] source, int sourceBase, int targetBase) {
        final int estimatedLength = estimateOutputLength(source.length, sourceBase, targetBase);
        final ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);

        byte[] message = source;
        while (message.length > 0) {
            final ByteArrayOutputStream quotient = new ByteArrayOutputStream(message.length);
            int remainder = 0;

            for (byte b : message) {
                final int accumulator = (b & 0xFF) + remainder * sourceBase;
                final int digit = (accumulator - (accumulator % targetBase)) / targetBase;
                remainder = accumulator % targetBase;

                if (quotient.size() > 0 || digit > 0) {
                    quotient.write(digit);
                }
            }

            out.write(remainder);
            message = quotient.toByteArray();
        }

        // 处理前导零
        for (int i = 0; i < source.length - 1 && source[i] == 0; i++) {
            out.write(0);
        }

        return ArrayUtil.reverse(out.toByteArray());
    }

    private static class ArrayUtil {
        public static byte[] reverse(byte[] array) {
            if (array == null) {
                return null;
            }
            int i = 0;
            int j = array.length - 1;
            byte tmp;
            while (j > i) {
                tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                j--;
                i++;
            }
            return array;
        }
    }
}
