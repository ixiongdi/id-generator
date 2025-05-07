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

package icu.congee.id.generator.broid;

import icu.congee.id.base.*;

import lombok.Data;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
/**
 * BroId类提供了一个自定义的ID生成和管理系统。
 * 该类实现了Comparable接口以支持ID之间的比较操作，并提供了多种编码格式的转换方法。
 * 每个BroId实例包含一个布尔值列表，用于存储ID的二进制表示。
 */
public class BroId implements Comparable<BroId> {
    private final String name = "BroId";
    private final String desc = "Custom Id";
    private final List<Boolean> value;

    @Override
    public String toString() {
        return Base64.encode(BitUtils.listToByteArray(value));
    }



    /**
     * 将BroId转换为长整型数值。
     * 注意：此方法可能会因为BroId的位数超过64位而导致数据丢失。
     *
     * @return BroId的长整型表示
     */
    public Long toLong() {
        return BitUtils.listToLong(value);
    }

    /**
     * 将BroId转换为标准的UUID。
     * 转换过程会将前64位作为UUID的最高有效位（MSB），后64位作为最低有效位（LSB）。
     *
     * @return UUID实例
     */
    public UUID toUUID() {
        long msb = BitUtils.listToLong(value.subList(0, 64));
        long lsb = BitUtils.listToLong(value.subList(64, 128));
        return new UUID(msb, lsb);
    }

    /**
     * 将BroId转换为指定类型的UUID。
     * 目前仅支持转换为UUIDv8类型，会根据RFC规范设置相应的版本和变体位。
     *
     * @param idType UUID的目标类型
     * @return 指定类型的UUID实例，如果不支持指定的类型则返回null
     */
    public UUID toUUID(IdType idType) {
        if (idType == IdType.UUIDv8) {
            // The 4-bit version field as defined by Section 4.2, set to 0b1000 (8).
            // Occupies bits 48 through 51 of octet 6.
            value.set(48, true);
            value.set(49, false);
            value.set(50, false);
            value.set(51, false);
            // The 2-bit variant field as defined by Section 4.1, set to 0b10. Occupies bits
            // 64 and 65 of octet 8.
            value.set(64, true);
            value.set(65, false);
            long msb = BitUtils.listToLong(value.subList(0, 64));
            long lsb = BitUtils.listToLong(value.subList(64, 128));
            return new UUID(msb, lsb);
        }
        return null;
    }

    /**
     * 比较两个BroId实例，基于它们的value值
     * 如果value列表长度不同，则较短的列表被视为较小
     * 如果value列表长度相同，则通过逐位比较位列表中的布尔值
     *
     * @param other 要比较的另一个BroId实例
     * @return 负数、零或正数，分别表示此BroId小于、等于或大于指定的BroId
     */
    @Override
    public int compareTo(BroId other) {
        if (other == null) {
            return 1; // null被视为最小
        }

        if (this.value == null) {
            return other.value == null ? 0 : -1;
        }

        if (other.value == null) {
            return 1;
        }

        // 首先比较列表长度
        int sizeComparison = Integer.compare(this.value.size(), other.value.size());
        if (sizeComparison != 0) {
            return sizeComparison;
        }

        // 如果长度相同，则逐位比较
        for (int i = 0; i < this.value.size(); i++) {
            boolean bit1 = this.value.get(i);
            boolean bit2 = other.value.get(i);
            if (bit1 != bit2) {
                return bit1 ? 1 : -1;
            }
        }
        return 0;
    }

    /**
     * 返回一个比较器，用于比较两个BroId实例
     *
     * @return 用于比较BroId实例的比较器
     */
    public static Comparator<BroId> getComparator() {
        return Comparator.nullsFirst(Comparator.comparing(BroId::getValue,
                (list1, list2) -> {
                    if (list1 == null && list2 == null) {
                        return 0;
                    }
                    if (list1 == null) {
                        return -1;
                    }
                    if (list2 == null) {
                        return 1;
                    }

                    // 首先比较列表长度
                    int sizeComparison = Integer.compare(list1.size(), list2.size());
                    if (sizeComparison != 0) {
                        return sizeComparison;
                    }

                    // 如果长度相同，则逐位比较
                    for (int i = 0; i < list1.size(); i++) {
                        boolean bit1 = list1.get(i);
                        boolean bit2 = list2.get(i);
                        if (bit1 != bit2) {
                            return bit1 ? 1 : -1;
                        }
                    }
                    return 0;
                }));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        BroId broId = (BroId) o;

        // 使用与compareTo相同的比较逻辑
        if (this.value == null) {
            return broId.value == null;
        }

        if (broId.value == null) {
            return false;
        }

        // 首先比较列表长度
        if (this.value.size() != broId.value.size()) {
            return false;
        }

        // 如果长度相同，则逐位比较
        for (int i = 0; i < this.value.size(); i++) {
            if (this.value.get(i) != broId.value.get(i)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value.toArray());
    }
}
