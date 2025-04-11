/*
 * MIT License
 *
 * Copyright (c) 2024 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package icu.congee.id.base.util;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 词法排序Base64编解码器
 * <p>
 * 该类提供了字节数组与Base64字符串之间的编码和解码功能，但与标准Base64不同，
 * LexicalBase64使用的字符集按照ASCII顺序排列，确保编码后的字符串保持词法排序特性。
 * 这对于需要保持排序特性的分布式ID系统特别有用。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class LexicalBase64 {
    // 按ASCII顺序排列的Base64字符集
    private static final char[] LEXICAL_CHARS = {
            '-', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            '_', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
    };

    // 解码映射表
    private static final byte[] DECODE_MAP = new byte[128];

    static {
        // 初始化解码映射表
        Arrays.fill(DECODE_MAP, (byte) -1);
        for (int i = 0; i < LEXICAL_CHARS.length; i++) {
            DECODE_MAP[LEXICAL_CHARS[i]] = (byte) i;
        }
    }

    /**
     * 将字节数组编码为词法排序的Base64字符串
     *
     * @param data 待编码的字节数组
     * @return 编码后的词法排序Base64字符串
     */
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        int outputLength = ((data.length * 8 + 5) / 6); // 每6位一个字符
        char[] output = new char[outputLength];
        int inputLength = data.length;
        int outputIndex = 0;
        int bitBuffer = 0;
        int bitCount = 0;

        for (int i = 0; i < inputLength; i++) {
            bitBuffer = (bitBuffer << 8) | (data[i] & 0xff);
            bitCount += 8;

            while (bitCount >= 6) {
                bitCount -= 6;
                output[outputIndex++] = LEXICAL_CHARS[(bitBuffer >> bitCount) & 0x3f];
            }
        }

        // 处理剩余的位
        if (bitCount > 0) {
            output[outputIndex++] = LEXICAL_CHARS[(bitBuffer << (6 - bitCount)) & 0x3f];
        }

        return new String(output, 0, outputIndex);
    }

    /**
     * 将词法排序的Base64字符串解码为字节数组
     *
     * @param base64Str 词法排序Base64编码的字符串
     * @return 解码后的字节数组
     */
    public static byte[] decode(String base64Str) {
        if (base64Str == null || base64Str.isEmpty()) {
            return new byte[0];
        }

        int inputLength = base64Str.length();
        int outputLength = inputLength * 6 / 8;
        byte[] output = new byte[outputLength];
        int outputIndex = 0;
        int bitBuffer = 0;
        int bitCount = 0;

        char[] input = base64Str.toCharArray();

        for (int i = 0; i < inputLength; i++) {
            char c = input[i];
            if (c >= 128 || DECODE_MAP[c] == -1) {
                throw new IllegalArgumentException("Invalid LexicalBase64 character: " + c);
            }

            bitBuffer = (bitBuffer << 6) | DECODE_MAP[c];
            bitCount += 6;

            if (bitCount >= 8) {
                bitCount -= 8;
                output[outputIndex++] = (byte) ((bitBuffer >> bitCount) & 0xff);
            }
        }

        // 如果输出长度与预期不符，调整数组大小
        if (outputIndex != outputLength) {
            return Arrays.copyOf(output, outputIndex);
        }

        return output;
    }

    /**
     * 将字符串编码为词法排序的Base64字符串
     *
     * @param str 待编码的字符串
     * @return 编码后的词法排序Base64字符串
     */
    public static String encodeString(String str) {
        if (str == null) {
            return "";
        }
        return encode(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 将词法排序的Base64字符串解码为原始字符串
     *
     * @param base64Str 词法排序Base64编码的字符串
     * @return 解码后的原始字符串
     */
    public static String decodeString(String base64Str) {
        if (base64Str == null) {
            return "";
        }
        return new String(decode(base64Str), StandardCharsets.UTF_8);
    }

    /**
     * 测试方法，演示LexicalBase64编解码的使用方式
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        byte[] testData1 = { 1, 2, 3, 4 };
        byte[] testData2 = { 2, 3, 4, 5 };

        String encoded1 = encode(testData1);
        String encoded2 = encode(testData2);

        System.out.println("测试数据1编码: " + encoded1);
        System.out.println("测试数据2编码: " + encoded2);
        System.out.println("词法排序比较: " + encoded1.compareTo(encoded2));

        byte[] decoded = decode(encoded1);
        System.out.println("解码后: " + Arrays.toString(decoded));
    }
}