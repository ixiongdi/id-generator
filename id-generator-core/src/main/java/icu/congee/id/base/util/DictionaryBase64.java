package icu.congee.id.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 按字典序编码的Base62工具类
 */
public class DictionaryBase64 {
    // 优化后的字符集（移除了容易混淆的字符：0,1,I,O,l,o）
    private static final char[] DICTIONARY = {
            '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'm',
            'n', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    // 解码映射表
    private static final byte[] DECODE_MAP = new byte[128];

    static {
        // 初始化解码映射表
        Arrays.fill(DECODE_MAP, (byte) -1);
        for (int i = 0; i < DICTIONARY.length; i++) {
            DECODE_MAP[DICTIONARY[i]] = (byte) i;
        }
    }

    /**
     * 将字节数组编码为Base64字符串
     *
     * @param data 待编码的字节数组
     * @return 编码后的Base64字符串
     */
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        int outputLength = ((data.length + 2) / 3) * 4;
        char[] output = new char[outputLength];
        int inputLength = data.length;
        int outputIndex = 0;
        int inputIndex = 0;

        while (inputIndex < inputLength) {
            int b1 = data[inputIndex++] & 0xff;
            int b2 = inputIndex < inputLength ? data[inputIndex++] & 0xff : 0;
            int b3 = inputIndex < inputLength ? data[inputIndex++] & 0xff : 0;

            int group = (b1 << 16) | (b2 << 8) | b3;

            output[outputIndex++] = DICTIONARY[(group >> 18) & 0x37];
            output[outputIndex++] = DICTIONARY[(group >> 12) & 0x37];
            output[outputIndex++] = DICTIONARY[(group >> 6) & 0x37];
            output[outputIndex++] = DICTIONARY[group & 0x37];
        }

        // Base64URLSafe不使用填充字符

        return new String(output);
    }

    /**
     * 将Base64字符串解码为字节数组
     *
     * @param base64Str Base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decode(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return new byte[0];
        }

        int inputLength = base64Str.length();
        byte[] output = new byte[inputLength * 3 / 4];
        int outputIndex = 0;

        char[] input = base64Str.toCharArray();
        int inputIndex = 0;

        while (inputIndex < inputLength) {
            int b1 = DECODE_MAP[input[inputIndex++]];
            int b2 = DECODE_MAP[input[inputIndex++]];
            int b3 = inputIndex < inputLength ? DECODE_MAP[input[inputIndex++]] : 0;
            int b4 = inputIndex < inputLength ? DECODE_MAP[input[inputIndex++]] : 0;

            if (b1 < 0 || b2 < 0 || b3 < 0 || b4 < 0) {
                throw new IllegalArgumentException("Invalid Base64 character");
            }

            int group = (b1 << 18) | (b2 << 12) | (b3 << 6) | b4;

            output[outputIndex++] = (byte) ((group >> 16) & 0xff);
            if (outputIndex < output.length) {
                output[outputIndex++] = (byte) ((group >> 8) & 0xff);
            }
            if (outputIndex < output.length) {
                output[outputIndex++] = (byte) (group & 0xff);
            }
        }

        return output;
    }

    /**
     * 将字符串编码为Base64字符串
     *
     * @param str 待编码的字符串
     * @return 编码后的Base64字符串
     */
    public static String encodeString(String str) {
        if (str == null) {
            return "";
        }
        return encode(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将Base64字符串解码为原始字符串
     *
     * @param base64Str Base64编码的字符串
     * @return 解码后的原始字符串
     */
    public static String decodeString(String base64Str) {
        if (base64Str == null) {
            return "";
        }
        return new String(decode(base64Str), StandardCharsets.UTF_8);
    }
}