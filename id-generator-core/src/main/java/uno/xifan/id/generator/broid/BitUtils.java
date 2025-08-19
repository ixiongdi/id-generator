/*
 * MIT License
 *
 * Copyright (c) 2025 ixiongdi
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

package uno.xifan.id.generator.broid;

import java.util.ArrayList;
import java.util.List;

/** 位操作工具类 */
public class BitUtils {

    /**
     * 将long值转换为List&lt;Boolean&gt;
     *
     * @param value long值
     * @param bits  位数
     * @return List&lt;Boolean&gt;对象
     */
    public static List<Boolean> longToList(long value, int bits) {
        if (bits < 0 || bits > 64) {
            throw new IllegalArgumentException("bits 必须在 0 到 64 之间");
        }

        List<Boolean> booleanList = new ArrayList<>(bits);
        for (int i = bits - 1; i >= 0; i--) {
            boolean isSet = ((value >> i) & 1) == 1;
            booleanList.add(isSet);
        }

        return booleanList;
    }

    /**
     * 将List&lt;Boolean&gt;转换为byte数组
     *
     * @param booleanList List&lt;Boolean&gt;对象
     * @return byte数组
     */
    public static byte[] listToByteArray(List<Boolean> booleanList) {
        int size = booleanList.size();
        int byteSize = (size + 7) / 8;
        byte[] bytes = new byte[byteSize];

        for (int i = 0; i < size; i++) {
            if (booleanList.get(i)) {
                bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
        }

        return bytes;
    }

    /**
     * 将List&lt;Boolean&gt;转换为long值
     *
     * @param booleanList List&lt;Boolean&gt;对象
     * @return long值
     */
    public static long listToLong(List<Boolean> booleanList) {
        long result = 0;
        int size = Math.min(booleanList.size(), 64);

        for (int i = 0; i < size; i++) {
            if (booleanList.get(i)) {
                result |= (1L << (size - 1 - i));
            }
        }

        return result;
    }
}
