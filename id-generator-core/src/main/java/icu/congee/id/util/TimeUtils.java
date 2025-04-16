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
package icu.congee.id.util;

import java.time.Instant;

public class TimeUtils {
    /**
     * 获取当前时间自 Unix 纪元以来的纳秒数
     * @return 纳秒数
     */
    public static long getCurrentUnixNano() {
        Instant now = Instant.now();
        // 先获取秒数并转换为纳秒
        long secondsInNanos = now.getEpochSecond() * 1_000_000_000;
        // 再加上当前秒内的纳秒数
        long nanos = now.getNano();
        return secondsInNanos + nanos;
    }

    public static void main(String[] args) {
        long unixNano = getCurrentUnixNano();
        System.out.println("当前时间自 Unix 纪元以来的纳秒数: " + unixNano);
    }
}