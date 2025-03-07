package com.github.ixiongdi.id.generator.ulid;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 高性能ULID生成器
 * <p>
 * ULID (Universally Unique Lexicographically Sortable Identifier) 是一种结合了时间戳和随机性的唯一标识符，
 * 具有按字典序排序的特性。该实现专注于高性能，使用了线程本地变量、位运算优化和缓冲区复用等技术。
 * </p>
 * <p>
 * ULID结构：
 * - 前10字节（80位）：毫秒级时间戳
 * - 后16字节（48位）：随机数据
 * </p>
 * <p>
 * 编码格式：使用Crockford's Base32（不区分大小写，排除易混淆字符）
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class ULIDGenerator {
    // Base32字符集（Crockford's Base32）
    private static final char[] ENCODING_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
        'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
    };

    // 常量定义
    private static final int TIMESTAMP_BYTES = 6; // 时间戳占用的字节数
    private static final int RANDOM_BYTES = 10; // 随机数占用的字节数
    private static final int ULID_BYTES = TIMESTAMP_BYTES + RANDOM_BYTES; // ULID总字节数
    private static final int ENCODED_LENGTH = 26; // 编码后的ULID长度

    // 线程本地变量，避免多线程竞争
    private static final ThreadLocal<ByteBuffer> BUFFER =
            ThreadLocal.withInitial(() -> ByteBuffer.allocate(ULID_BYTES));
    private static final ThreadLocal<char[]> CHAR_BUFFER =
            ThreadLocal.withInitial(() -> new char[ENCODED_LENGTH]);
    private static final ThreadLocal<byte[]> LAST_RANDOM_BYTES =
            ThreadLocal.withInitial(() -> new byte[RANDOM_BYTES]);

    /**
     * 生成一个新的ULID字符串
     *
     * @return 26个字符的ULID字符串
     */
    public static String generate() {
        // 获取当前时间戳（毫秒）
        long timestamp = System.currentTimeMillis();

        // 获取线程本地缓冲区
        ByteBuffer buffer = BUFFER.get();
        buffer.clear();

        // 写入时间戳（6字节，48位）
        buffer.put((byte) (timestamp >>> 40));
        buffer.put((byte) (timestamp >>> 32));
        buffer.put((byte) (timestamp >>> 24));
        buffer.put((byte) (timestamp >>> 16));
        buffer.put((byte) (timestamp >>> 8));
        buffer.put((byte) timestamp);

        // 生成随机字节
        byte[] randomBytes = LAST_RANDOM_BYTES.get();


        ThreadLocalRandom.current().nextBytes(randomBytes);

        // 写入随机字节
        buffer.put(randomBytes);

        // 编码为Base32字符串
        return encode(buffer.array());
    }


    /**
     * 将ULID字节数组编码为Base32字符串
     *
     * @param data ULID字节数组
     * @return 编码后的ULID字符串
     */
    private static String encode(byte[] data) {
        char[] chars = CHAR_BUFFER.get();

        // 编码时间戳部分（10个字符）
        chars[0] = ENCODING_CHARS[(data[0] & 0xE0) >>> 5];
        chars[1] = ENCODING_CHARS[data[0] & 0x1F];
        chars[2] = ENCODING_CHARS[(data[1] & 0xF8) >>> 3];
        chars[3] = ENCODING_CHARS[((data[1] & 0x07) << 2) | ((data[2] & 0xC0) >>> 6)];
        chars[4] = ENCODING_CHARS[(data[2] & 0x3E) >>> 1];
        chars[5] = ENCODING_CHARS[((data[2] & 0x01) << 4) | ((data[3] & 0xF0) >>> 4)];
        chars[6] = ENCODING_CHARS[((data[3] & 0x0F) << 1) | ((data[4] & 0x80) >>> 7)];
        chars[7] = ENCODING_CHARS[(data[4] & 0x7C) >>> 2];
        chars[8] = ENCODING_CHARS[((data[4] & 0x03) << 3) | ((data[5] & 0xE0) >>> 5)];
        chars[9] = ENCODING_CHARS[data[5] & 0x1F];

        // 编码随机部分（16个字符）
        chars[10] = ENCODING_CHARS[(data[6] & 0xF8) >>> 3];
        chars[11] = ENCODING_CHARS[((data[6] & 0x07) << 2) | ((data[7] & 0xC0) >>> 6)];
        chars[12] = ENCODING_CHARS[(data[7] & 0x3E) >>> 1];
        chars[13] = ENCODING_CHARS[((data[7] & 0x01) << 4) | ((data[8] & 0xF0) >>> 4)];
        chars[14] = ENCODING_CHARS[((data[8] & 0x0F) << 1) | ((data[9] & 0x80) >>> 7)];
        chars[15] = ENCODING_CHARS[(data[9] & 0x7C) >>> 2];
        chars[16] = ENCODING_CHARS[((data[9] & 0x03) << 3) | ((data[10] & 0xE0) >>> 5)];
        chars[17] = ENCODING_CHARS[data[10] & 0x1F];
        chars[18] = ENCODING_CHARS[(data[11] & 0xF8) >>> 3];
        chars[19] = ENCODING_CHARS[((data[11] & 0x07) << 2) | ((data[12] & 0xC0) >>> 6)];
        chars[20] = ENCODING_CHARS[(data[12] & 0x3E) >>> 1];
        chars[21] = ENCODING_CHARS[((data[12] & 0x01) << 4) | ((data[13] & 0xF0) >>> 4)];
        chars[22] = ENCODING_CHARS[((data[13] & 0x0F) << 1) | ((data[14] & 0x80) >>> 7)];
        chars[23] = ENCODING_CHARS[(data[14] & 0x7C) >>> 2];
        chars[24] = ENCODING_CHARS[((data[14] & 0x03) << 3) | ((data[15] & 0xE0) >>> 5)];
        chars[25] = ENCODING_CHARS[data[15] & 0x1F];

        return new String(chars);
    }
   }