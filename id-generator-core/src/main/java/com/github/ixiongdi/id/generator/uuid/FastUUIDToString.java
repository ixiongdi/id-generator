package com.github.ixiongdi.id.generator.uuid;

import java.util.UUID;

/**
 * 高效的UUID toString实现
 * <p>
 * 该类提供了一个比Java标准库中UUID.toString()更高效的实现。
 * 主要优化点包括：
 * 1. 预分配固定大小的字符数组，避免字符串拼接和StringBuilder的开销
 * 2. 使用位运算和查找表快速将字节转换为十六进制字符
 * 3. 直接操作字符数组，减少中间对象创建
 * 4. 使用线程本地缓冲区，避免频繁创建字符数组
 * </p>
 *
 * @author ixiongdi
 * @version 1.1
 * @since 2024-05-01
 */
public class FastUUIDToString {

    // 十六进制字符查找表，用于快速将字节转换为十六进制字符
    private static final char[] HEX_DIGITS = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };

    // UUID字符串的固定长度：32个十六进制字符 + 4个连字符
    private static final int UUID_STRING_LENGTH = 36;

    // 连字符的位置
    private static final int[] DASH_POSITIONS = {8, 13, 18, 23};
    
    // 线程本地字符数组缓冲区，避免频繁创建字符数组
    private static final ThreadLocal<char[]> CHAR_BUFFER = 
            ThreadLocal.withInitial(() -> new char[UUID_STRING_LENGTH]);

    /**
     * 将UUID转换为字符串的高效实现
     *
     * @param uuid 要转换的UUID
     * @return UUID的字符串表示
     */
    /**
     * 将UUID转换为字符串的高效实现
     *
     * @param uuid 要转换的UUID
     * @return UUID的字符串表示
     */
    public static String toString(UUID uuid) {
        if (uuid == null) {
            return null;
        }

        // 获取UUID的高64位和低64位
        long mostSigBits = uuid.getMostSignificantBits();
        long leastSigBits = uuid.getLeastSignificantBits();

        // 使用线程本地缓冲区获取字符数组，避免频繁创建对象
        char[] chars = CHAR_BUFFER.get();

        // 处理高64位
        formatHex(chars, mostSigBits >> 32, 0, 8);
        chars[8] = '-';
        formatHex(chars, mostSigBits >> 16, 9, 4);
        chars[13] = '-';
        formatHex(chars, mostSigBits, 14, 4);
        chars[18] = '-';

        // 处理低64位
        formatHex(chars, leastSigBits >> 48, 19, 4);
        chars[23] = '-';
        formatHex(chars, leastSigBits, 24, 12);

        // 从字符数组创建字符串
        return new String(chars);
    }

    /**
     * 将长整型数字的指定部分格式化为十六进制字符，并放入字符数组的指定位置
     *
     * @param chars 目标字符数组
     * @param value 要格式化的长整型数字
     * @param offset 字符数组中的起始偏移量
     * @param digits 要格式化的十六进制数字的数量
     */
    private static void formatHex(char[] chars, long value, int offset, int digits) {
        for (int i = 0; i < digits; i++) {
            // 从右到左处理每个十六进制数字
            int hexDigit = (int) (value >> ((digits - 1 - i) * 4)) & 0xF;
            chars[offset + i] = HEX_DIGITS[hexDigit];
        }
    }
}