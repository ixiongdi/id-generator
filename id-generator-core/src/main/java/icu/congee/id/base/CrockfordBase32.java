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

package icu.congee.id.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Crockford Base32编码工具类。
 * 实现了Douglas Crockford的Base32编码规范，这是一种人类可读的编码方案，
 * 设计用于减少视觉混淆，例如将容易混淆的字符（如'I'、'L'、'O'）映射到其他字符。
 * 
 * @see <a href="https://www.crockford.com/base32.html">Crockford's Base32</a>
 */
public class CrockfordBase32 {
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final int BASE = 32;
    private static final Map<Character, Integer> DECODING_MAP = new HashMap<>();

    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            char c = ALPHABET.charAt(i);
            DECODING_MAP.put(c, i);
            DECODING_MAP.put(Character.toLowerCase(c), i);
        }
        DECODING_MAP.put('I', 1);
        DECODING_MAP.put('i', 1);
        DECODING_MAP.put('L', 1);
        DECODING_MAP.put('l', 1);
        DECODING_MAP.put('O', 0);
        DECODING_MAP.put('o', 0);
    }

    /**
     * 将字节数组编码为Crockford Base32字符串。
     * 编码过程将每5个比特映射为一个字符，使用特定的32字符字母表。
     *
     * @param data 要编码的字节数组
     * @return 编码后的Base32字符串，如果输入为null或空数组则返回空字符串
     */
    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xff);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                result.append(ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 0x1f));
                bitsLeft -= 5;
            }
        }

        if (bitsLeft > 0) {
            buffer <<= (5 - bitsLeft);
            result.append(ALPHABET.charAt(buffer & 0x1f));
        }

        return result.toString();
    }

    /**
     * 将Crockford Base32编码的字符串解码为字节数组。
     * 解码过程会自动处理字符大小写，并将特殊字符（如'I'、'L'、'O'）映射到对应的数值。
     *
     * @param encoded 要解码的Base32字符串
     * @return 解码后的字节数组，如果输入为null或空字符串则返回空数组
     * @throws IllegalArgumentException 如果输入字符串包含无效字符
     */
    public static byte[] decode(String encoded) {
        if (encoded == null || encoded.length() == 0) {
            return new byte[0];
        }
        encoded = encoded.replace("-", "");
        int bitLength = encoded.length() * 5;
        int byteLength = (bitLength + 7) / 8;
        byte[] result = new byte[byteLength];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        for (char c : encoded.toCharArray()) {
            if (!DECODING_MAP.containsKey(c)) {
                throw new IllegalArgumentException("Invalid character in Base32 string: " + c);
            }
            buffer = (buffer << 5) | DECODING_MAP.get(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }

        return result;
    }

    /**
     * 测试方法，演示Base32编码和解码的基本用法。
     * 将一个示例字符串进行编码和解码，并打印结果。
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        byte[] originalData = "Hello, World!".getBytes();
        String encoded = encode(originalData);
        byte[] decoded = decode(encoded);
        System.out.println("Original data: " + new String(originalData));
        System.out.println("Encoded data: " + encoded);
        System.out.println("Decoded data: " + new String(decoded));
    }
}