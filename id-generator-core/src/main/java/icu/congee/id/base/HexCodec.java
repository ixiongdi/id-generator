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

public class HexCodec {
    private static final char[] DIGITS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    private static final int[] VALUES = new int[128];

    static {
        for (int i = 0; i < VALUES.length; i++) {
            VALUES[i] = -1;
        }
        for (int i = 0; i < DIGITS.length; i++) {
            VALUES[DIGITS[i]] = i;
            // 支持大写字母
            if (i > 9) {
                VALUES[Character.toUpperCase(DIGITS[i])] = i;
            }
        }
    }

    public static String encode(long value) {
        if (value == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (value != 0) {
            sb.append(DIGITS[(int)(value & 0xF)]);
            value >>>= 4;
        }
        return sb.reverse().toString();
    }

    public static String encode(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long value = buffer.getLong();
        return encode(value);
    }

    public static byte[] decode(String hex) {
        long value = decodeLong(hex);
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(value);
        return buffer.array();
    }

    private static long decodeLong(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("hex string cannot be null or empty");
        }

        long result = 0;
        for (int i = 0; i < hex.length(); i++) {
            char c = hex.charAt(i);
            if (c >= VALUES.length || VALUES[c] == -1) {
                throw new IllegalArgumentException("invalid hex character: " + c);
            }
            result = (result << 4) | VALUES[c];
        }
        return result;
    }
}