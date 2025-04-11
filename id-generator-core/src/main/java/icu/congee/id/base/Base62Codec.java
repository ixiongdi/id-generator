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
     * @return 编码后的Base62字符串，如果输入为null或空数组则返回空字符串
     */
    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        // 处理标准8字节长度的情况
        if (bytes.length == Long.BYTES) {
            ByteBuffer buffer = ByteBuffer.wrap(bytes);
            long number = buffer.getLong();
            if (number == 0) {
                return "0";
            }
            return longToBase62(number);
        }

        // 处理其他长度的字节数组
        // 对于小于8字节的数组，补0后处理
        // 对于大于8字节的数组，分段处理
        StringBuilder result = new StringBuilder();
        byte[] paddedBytes = bytes;

        if (bytes.length < Long.BYTES) {
            paddedBytes = new byte[Long.BYTES];
            System.arraycopy(bytes, 0, paddedBytes, Long.BYTES - bytes.length, bytes.length);
            ByteBuffer buffer = ByteBuffer.wrap(paddedBytes);
            return longToBase62(buffer.getLong());
        } else {
            // 对于大于8字节的情况，分段处理
            for (int i = 0; i < bytes.length; i += Long.BYTES) {
                int remaining = Math.min(Long.BYTES, bytes.length - i);
                byte[] chunk = new byte[Long.BYTES];
                System.arraycopy(bytes, i, chunk, Long.BYTES - remaining, remaining);

                ByteBuffer buffer = ByteBuffer.wrap(chunk);
                String encoded = longToBase62(buffer.getLong());
                result.append(encoded);
                if (i + Long.BYTES < bytes.length) {
                    result.append("-"); // 使用分隔符连接各段
                }
            }
            return result.toString();
        }
    }

    /**
     * 将长整型数值转换为Base62字符串
     * 
     * @param number 要转换的长整型数值
     * @return 转换后的Base62字符串
     */
    private static String longToBase62(long number) {
        if (number == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        // 处理负数情况
        boolean isNegative = number < 0;
        if (isNegative) {
            // 对于负数，我们使用绝对值并在结果前添加特殊标记
            // 注意：这里需要特别处理Long.MIN_VALUE，因为其绝对值超出了long范围
            if (number == Long.MIN_VALUE) {
                // 特殊处理Long.MIN_VALUE
                sb.append(BASE62_ALPHABET.charAt(0)); // 添加前缀标记
                number = -(number + 1); // 避免溢出
                sb.append(longToBase62(number));
                return sb.toString();
            }
            number = -number;
            sb.append("-"); // 负数前缀
        }

        while (number > 0) {
            int remainder = (int) (number % 62);
            sb.insert(isNegative ? 1 : 0, BASE62_ALPHABET.charAt(remainder));
            number /= 62;
        }

        return sb.toString();
    }

    // 将 Base62 字符串解码为字节数组
    /**
     * 将Base62字符串解码为字节数组
     *
     * @param base62 要解码的Base62字符串
     * @return 解码后的字节数组，如果输入为null或空字符串则返回空数组
     * @throws IllegalArgumentException 如果输入包含非Base62字符
     */
    public static byte[] decode(String base62) {
        if (base62 == null || base62.isEmpty()) {
            return new byte[0];
        }

        // 检查是否包含分隔符，如果有则是多段编码
        if (base62.contains("-")) {
            String[] parts = base62.split("-");
            // 特殊情况：第一个字符是负号
            boolean startsWithNegative = base62.startsWith("-");
            int startIndex = startsWithNegative ? 1 : 0;

            // 计算总字节数
            int totalBytes = (parts.length - startIndex) * Long.BYTES;
            ByteBuffer buffer = ByteBuffer.allocate(totalBytes);

            // 处理第一段（可能包含负号）
            if (startsWithNegative && parts.length > 1) {
                buffer.putLong(base62ToLong("-" + parts[1]));
            } else if (parts.length > 0) {
                buffer.putLong(base62ToLong(parts[0]));
            }

            // 处理剩余段
            for (int i = startIndex + 1; i < parts.length; i++) {
                buffer.putLong(base62ToLong(parts[i]));
            }

            return buffer.array();
        }

        // 单段编码的处理
        long result = base62ToLong(base62);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(result);
        return buffer.array();
    }

    /**
     * 将Base62字符串转换为长整型数值
     * 
     * @param base62 要转换的Base62字符串
     * @return 转换后的长整型数值
     * @throws IllegalArgumentException 如果输入包含非Base62字符
     */
    private static long base62ToLong(String base62) {
        if (base62 == null || base62.isEmpty()) {
            return 0L;
        }

        // 处理负数情况
        boolean isNegative = base62.startsWith("-");
        if (isNegative) {
            base62 = base62.substring(1);
        }

        long result = 0;
        for (int i = 0; i < base62.length(); i++) {
            char c = base62.charAt(i);
            int digit = BASE62_ALPHABET.indexOf(c);

            if (digit == -1) {
                throw new IllegalArgumentException("Invalid Base62 character: " + c);
            }

            // 检查是否会溢出
            if (result > Long.MAX_VALUE / 62) {
                throw new IllegalArgumentException("Base62 string too large to decode: " + base62);
            }

            result = result * 62 + digit;

            // 再次检查是否溢出
            if (result < 0) {
                throw new IllegalArgumentException("Base62 string too large to decode: " + base62);
            }
        }

        return isNegative ? -result : result;
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