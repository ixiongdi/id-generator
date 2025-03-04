package com.github.ixiongdi.id.generator.uuid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;

public class UUIDv3Generator {
    // 预定义命名空间缓存（减少重复计算）
    private static final UUID DNS_NAMESPACE = UUID.fromString("6ba7b810-9dad-11d1-80b4-00c04fd430c8");
    private static final ThreadLocal<MessageDigest> MD5_DIGEST = 
        ThreadLocal.withInitial(() -> {
            try {
                return MessageDigest.getInstance("MD5");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    public static UUID generate(String name) {
        return generate(DNS_NAMESPACE, name);
    }

    public static UUID generate(UUID namespace, String name) {
        // 1. 命名空间字节处理
        byte[] nsBytes = toBytes(namespace);
        
        // 2. 名称字节编码（UTF-8）
        byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
        
        // 3. 合并字节流
        byte[] input = new byte[nsBytes.length + nameBytes.length];
        System.arraycopy(nsBytes, 0, input, 0, nsBytes.length);
        System.arraycopy(nameBytes, 0, input, nsBytes.length, nameBytes.length);
        
        // 4. MD5 哈希计算
        MessageDigest md = MD5_DIGEST.get();
        byte[] hash = md.digest(input);
        md.reset();
        
        // 5. 调整位布局
        hash[6] &= 0x0F;    // 清空高4位
        hash[6] |= 0x30;    // 设置版本号 3
        hash[8] &= 0x3F;    // 清空高2位
        hash[8] |= 0x80;    // 设置变体号 10b
        
        return bytesToUUID(hash);
    }

    private static byte[] toBytes(UUID uuid) {
        byte[] bytes = new byte[16];
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        for (int i = 0; i < 8; i++) {
            bytes[i] = (byte) (msb >>> 8 * (7 - i));
            bytes[i + 8] = (byte) (lsb >>> 8 * (7 - i));
        }
        return bytes;
    }

    private static UUID bytesToUUID(byte[] hash) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++)
            msb = (msb << 8) | (hash[i] & 0xff);
        for (int i = 8; i < 16; i++)
            lsb = (lsb << 8) | (hash[i] & 0xff);
        return new UUID(msb, lsb);
    }

    // 测试用例
    public static void main(String[] args) {
        UUID uuid = generate("");
        System.out.println("UUIDv3: " + uuid);
        System.out.println("Version: " + uuid.version()); // 应输出7
        System.out.println("Variant: " + uuid.variant()); // 应输出2
    }
}