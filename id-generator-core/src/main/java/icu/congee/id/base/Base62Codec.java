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

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Base62编解码器
 * <p>
 * 该类提供了字节数组与Base62字符串之间的编码和解码功能。Base62使用0-9、A-Z和a-z这62个字符来表示数据，
 * 适用于需要生成短小、可读性强且大小写敏感的标识符的场景。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class Base62Codec {
    private static final String BASE62_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 将字节数组编码为 Base62 字符串
    /**
     * 将字节数组编码为Base62字符串
     *
     * @param bytes 要编码的字节数组
     * @return 编码后的Base62字符串
     */
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
    /**
     * 将Base62字符串解码为字节数组
     *
     * @param base62 要解码的Base62字符串
     * @return 解码后的字节数组
     */
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

    /**
     * 测试方法，演示Base62编解码的使用方式
     *
     * @param args 命令行参数（未使用）
     */
    public static void main(String[] args) {
        byte[] inputBytes = { 1, 2, 3, 4, 5, 6, 7, 8 };
        String encoded = encode(inputBytes);
        byte[] decoded = decode(encoded);
        System.out.println("Base62 编码前的字节数组: " + Arrays.toString(inputBytes));
        System.out.println("Base62 编码后的字符串: " + encoded);
        System.out.println("Base62 解码后的字节数组: " + Arrays.toString(decoded));
    }
}