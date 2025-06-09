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

import java.security.SecureRandom;
import java.util.UUID;
// 导入Java并发包中的ThreadLocalRandom类，用于生成线程安全的随机数

/**
 * UUIDv7生成器
 *
 * <p>该类用于生成符合UUIDv7规范的UUID。UUIDv7是一种基于时间的UUID版本， 它结合了时间戳和随机数，提供了良好的排序特性和唯一性保证。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01 &#064;copyright (c) 2025 ixiongdi. All rights reserved.
 */
public class UUIDv7Generator implements IdGenerator {
    // 使用加密级强随机数生成器
    private static final SecureRandom random = new SecureRandom();

    // 高位随机数掩码: 0x000000000000000FFF (12位掩码)
    private static final long HIGH_PART_RANDOM_MASK = (1L << 12) - 1;

    // 低位随机数掩码: 0x3FFFFFFFFFFFFFFF (62位掩码)
    private static final long LOW_PART_RANDOM_MASK = (1L << 62) - 1;

    // 版本号标识位: 版本7对应的位模式 (0x7000)
    private static final long VERSION = 0b0111L << 12;

    // 变体标识位: RFC变体对应的位模式 (0x8000000000000000)
    private static final long VARIANT = 0b10L << 62;

    /**
     * 生成UUIDv7对象
     *
     * @return 符合RFC 4122标准的UUIDv7对象
     */
    public static UUID next() {
        // 时间戳部分: 48位毫秒级时间戳左移16位
        long timestampPart = System.currentTimeMillis() << 16;

        // 高位随机数部分: 截取长整型随机数的低12位
        long highRandomPart = random.nextLong() & HIGH_PART_RANDOM_MASK;

        // 高位组合: 时间戳 | 版本标识 | 高位随机数
        long msb =
                timestampPart
                        | VERSION // 设置版本号位(4位)
                        | highRandomPart; // 填充12位随机数

        // 低位随机数部分: 截取长整型随机数的低62位
        long lowRandomPart = random.nextLong() & LOW_PART_RANDOM_MASK;

        // 低位组合: 变体标识 | 低位随机数
        long lsb = VARIANT | lowRandomPart; // 设置变体位(2位)

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
