package icu.congee.id.generator.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UUIDv2生成器 (DCE安全规范实现)
 * <p>
 * 该类用于生成符合DCE安全规范的UUIDv2。该版本包含POSIX用户/组ID信息，
 * 主要应用于需要系统级安全标识的场景。使用时需注意平台兼容性。
 * </p>
 * 
 * <p>UUIDv2结构规范：
 * <ul>
 *   <li>时间戳 - 28位 (60ns精度，从1582-10-15开始)</li>
 *   <li>版本号 - 4位 (0010b)</li>
 *   <li>本地标识符 - 16位 (POSIX用户ID)</li>
 *   <li>变体标识 - 2位 (RFC 4122规范)</li>
 *   <li>安全域 - 8位</li>
 *   <li>本地标识符扩展 - 32位 (POSIX组ID)</li>
 * </ul>
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class UUIDv2Generator {
    // 常量定义
    private static final int VERSION = 0x2000;
    private static final long VARIANT = 0x8000000000000000L;
    private static final int DOMAIN_PERSONAL = 0;
    
    // 线程本地序列号
    private static final ThreadLocal<Short> sequence = ThreadLocal.withInitial(
        () -> (short) ThreadLocalRandom.current().nextInt(0, 0xFF)
    );

    /**
     * 生成DCE安全UUIDv2
     * @param domain 安全域标识 (0=个人, 1=组, 2=组织)
     */
    public static UUID generate(int domain) {
        long timestamp = System.currentTimeMillis() * 10_000 + 0x01B21DD213814000L;
        
        long msb = ((timestamp & 0x0FFFFFFF) << 32)
                 | ((domain & 0xFF) << 24)
                 | (getPOSIXUID() << 16)
                 | VERSION;

        long lsb = VARIANT
                 | ((sequence.get() & 0xFF) << 48)
                 | ((getPOSIXGID() & 0xFFFFFFFFL) << 16);

        return new UUID(msb, lsb);
    }

    // 获取POSIX用户ID
    private static int getPOSIXUID() {
        try {
            return Integer.parseInt(System.getProperty("user.name"));
        } catch (Exception e) {
            return ThreadLocalRandom.current().nextInt(0, 0xFFFF);
        }
    }

    // 获取POSIX组ID
    private static int getPOSIXGID() {
        try {
            return (int) Class.forName("sun.security.action.GetPropertyAction")
                .getMethod("getGroupId").invoke(null);
        } catch (Exception e) {
            return ThreadLocalRandom.current().nextInt(0, 0xFFFF);
        }
    }
}