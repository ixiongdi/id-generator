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

package icu.congee.id.generator.uuid;

// 导入Java标准库中的UUID类，用于创建和操作UUID
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;
// 导入Java并发包中的ThreadLocalRandom类，用于生成线程安全的随机数
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * UUIDv7生成器
 * <p>
 * 该类用于生成符合UUIDv7规范的UUID。UUIDv7是一种基于时间的UUID版本，
 * 它结合了时间戳和随机数，提供了良好的排序特性和唯一性保证。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 *        &#064;copyright (c) 2025 ixiongdi. All rights reserved.
 */
public class UUIDv7Generator implements IdGenerator {
    private static volatile long lastTimestamp = 0L;
    private static final LongAdder sequence = new LongAdder();

    /**
     * 生成一个新的UUIDv7
     * <p>
     * 该方法创建并返回一个新的UUIDv7实例，其中包含当前时间戳和随机数据。
     * UUIDv7的结构如下：
     * - 最高有效位(MSB)：48位时间戳 + 4位版本号(7) + 12位序列号
     * - 最低有效位(LSB)：2位变体标识 + 62位随机数
     * </p>
     *
     * @return 新生成的UUIDv7实例
     */
    public static synchronized UUID next() {
        // 获取当前时间戳
        long currentTime = System.currentTimeMillis();

        // 处理时钟回拨和相同时间戳的情况
        if (currentTime <= lastTimestamp) {
            sequence.increment();
        } else {
            sequence.reset();
            lastTimestamp = currentTime;
        }

        // 构建最高有效位(MSB)
        // currentTime << 16 - 将时间戳左移16位
        // | 0x7000 - 添加版本号7
        // | (sequence.sum() & 0xFFF) - 添加序列号（12位）
        long msb = currentTime << 16 | 0x7000 | (sequence.sum() & 0xFFF);

        // 构建最低有效位(LSB)
        // 0x8000000000000000L - 设置变体位为2（RFC 4122规范）
        // | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL - 添加随机数（62位）
        long lsb = 0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;

        return new UUID(msb, lsb);
    }

    @Override
    public String generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv7;
    }
}
