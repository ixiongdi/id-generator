package icu.congee.id.generator.uuid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

/**
 * UUIDv5生成器
 * <p>
 * 该类用于生成符合UUIDv5规范的UUID。UUIDv5是一种基于名称空间的UUID版本，
 * 它使用SHA-1哈希算法将名称空间UUID和名称字符串作为输入，生成确定性的UUID。
 * 相同的名称空间和名称将始终生成相同的UUID。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class UUIDv5Generator {
    // 常量定义，用于位掩码和版本/变体的标识
    /** UUID 版本 5 的标识符 */
    private static final int VERSION_IDENTIFIER = 5;

    /** UUID 变体 2 的标识符（RFC 4122规范） */
    private static final long VARIANT_IDENTIFIER = 0x8000000000000000L;

    // 预定义的标准名称空间UUID常量
    /** 预定义的DNS名称空间UUID */
    public static final UUID NAMESPACE_DNS = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");

    /** 预定义的URL名称空间UUID */
    public static final UUID NAMESPACE_URL = UUID.fromString("6ba7b811-9dad-11d1-80b4-00c04fd430c8");

    /** 预定义的OID名称空间UUID */
    public static final UUID NAMESPACE_OID = UUID.fromString("6ba7b812-9dad-11d1-80b4-00c04fd430c8");

    /** 预定义的X500 DN名称空间UUID */
    public static final UUID NAMESPACE_X500 = UUID.fromString("6ba7b814-9dad-11d1-80b4-00c04fd430c8");

    /**
     * 根据名称空间和名称生成UUIDv5
     * <p>
     * 该方法使用SHA-1哈希算法将名称空间UUID和名称字符串作为输入，生成确定性的UUID。
     * 相同的名称空间和名称将始终生成相同的UUID。
     * UUIDv5的结构如下：
     * - 最高有效位(MSB)：从哈希的前64位中提取，并设置版本号(5)
     * - 最低有效位(LSB)：从哈希的后64位中提取，并设置变体标识
     * </p>
     *
     * @param namespace 名称空间UUID，用于提供上下文
     * @param name 要转换为UUID的名称字符串
     * @return 基于提供的名称空间和名称生成的UUIDv5实例
     * @throws RuntimeException 如果SHA-1算法不可用
     */
    public static UUID fromNamespaceAndName(UUID namespace, String name) {
        try {
            // 创建SHA-1消息摘要实例
            MessageDigest sha1Digest = MessageDigest.getInstance("SHA-1");

            // 将名称空间UUID转换为字节数组并更新摘要
            sha1Digest.update(uuidToBytes(namespace));

            // 将名称字符串转换为UTF-8字节数组并更新摘要
            sha1Digest.update(name.getBytes(StandardCharsets.UTF_8));

            // 计算哈希值
            byte[] hash = sha1Digest.digest();

            // 设置版本号（版本5）
            hash[6] &= 0x0f; // 清除版本位
            hash[6] |= (VERSION_IDENTIFIER << 4); // 设置版本位为5

            // 设置变体（RFC 4122变体）
            hash[8] &= 0x3f; // 清除变体位
            hash[8] |= 0x80; // 设置变体位

            // 从哈希中提取最高有效位和最低有效位
            long msb = 0;
            long lsb = 0;

            // 构建最高有效位(MSB)，使用哈希的前8个字节
            for (int i = 0; i < 8; i++) {
                msb = (msb << 8) | (hash[i] & 0xff);
            }

            // 构建最低有效位(LSB)，使用哈希的后8个字节
            for (int i = 8; i < 16; i++) {
                lsb = (lsb << 8) | (hash[i] & 0xff);
            }

            // 使用构建好的MSB和LSB创建并返回一个新的UUID实例
            return new UUID(msb, lsb);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1算法不可用", e);
        }
    }

    /**
     * 使用DNS名称空间生成UUIDv5
     *
     * @param name 要转换为UUID的名称字符串
     * @return 基于DNS名称空间和提供的名称生成的UUIDv5实例
     */
    public static UUID fromDNS(String name) {
        return fromNamespaceAndName(NAMESPACE_DNS, name);
    }

    /**
     * 使用URL名称空间生成UUIDv5
     *
     * @param name 要转换为UUID的名称字符串
     * @return 基于URL名称空间和提供的名称生成的UUIDv5实例
     */
    public static UUID fromURL(String name) {
        return fromNamespaceAndName(NAMESPACE_URL, name);
    }

    /**
     * 使用OID名称空间生成UUIDv5
     *
     * @param name 要转换为UUID的名称字符串
     * @return 基于OID名称空间和提供的名称生成的UUIDv5实例
     */
    public static UUID fromOID(String name) {
        return fromNamespaceAndName(NAMESPACE_OID, name);
    }

    /**
     * 使用X500 DN名称空间生成UUIDv5
     *
     * @param name 要转换为UUID的名称字符串
     * @return 基于X500 DN名称空间和提供的名称生成的UUIDv5实例
     */
    public static UUID fromX500(String name) {
        return fromNamespaceAndName(NAMESPACE_X500, name);
    }

    /**
     * 将UUID转换为字节数组
     *
     * @param uuid 要转换的UUID
     * @return 表示UUID的16字节数组
     */
    private static byte[] uuidToBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();

        // 将最高有效位转换为前8个字节
        for (int i = 7; i >= 0; i--) {
            bytes[i] = (byte) (msb & 0xff);
            msb >>>= 8;
        }

        // 将最低有效位转换为后8个字节
        for (int i = 15; i >= 8; i--) {
            bytes[i] = (byte) (lsb & 0xff);
            lsb >>>= 8;
        }

        return bytes;
    }
}