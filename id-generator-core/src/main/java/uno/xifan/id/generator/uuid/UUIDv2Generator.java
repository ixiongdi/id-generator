package uno.xifan.id.generator.uuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * UUIDv2生成器 (DCE安全规范实现)
 *
 * <p>
 * 该类用于生成符合DCE安全规范的UUIDv2。该版本包含POSIX用户/组ID信息， 主要应用于需要系统级安全标识的场景。使用时需注意平台兼容性。
 *
 * <p>
 * UUIDv2结构规范：
 *
 * <ul>
 * <li>时间戳 - 28位 (60ns精度，从1582-10-15开始)
 * <li>版本号 - 4位 (0010b)
 * <li>本地标识符 - 16位 (POSIX用户ID)
 * <li>变体标识 - 2位 (RFC 4122规范)
 * <li>安全域 - 8位
 * <li>本地标识符扩展 - 32位 (POSIX组ID)
 * </ul>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @author ixiongdi
 */
public class UUIDv2Generator implements IdGenerator {
    // 常量定义
    private static final long TIMESTAMP_EPOCH_OFFSET = 0x01B21DD213814000L;
    private static final AtomicLong timestampCounter = new AtomicLong();
    private static final byte securityDomain = 0x00;

    public static UUID next() {
        // 时间戳处理（60ns精度，基于1582纪元的AtomicLong计数器）
        long timestamp = (System.currentTimeMillis() * 10_000 + TIMESTAMP_EPOCH_OFFSET) & 0x0FFFFFFFL;

        // 处理时间回拨并获取序列号
        long sequence = timestampCounter.updateAndGet(prev -> timestamp > prev ? timestamp : prev + 1);

        // 构建MSB：时间戳(28位) + 版本(0x2000) + 用户ID(16位)
        long msb = ((sequence & 0x0FFFFFFFL) << 36)
                | 0x2000L
                | (getPOSIXUserId() & 0xFFFFL) << 20;

        // 构建LSB：变体标识 + 安全域 + 组ID(32位)
        long lsb = 0x8000000000000000L
                | (securityDomain << 48)
                | ((getPOSIXGroupId() & 0xFFFFFFFFL) << 16)
                | (sequence & 0xFFFL);

        return new UUID(msb, lsb);
    }

    private static short getPOSIXUserId() {
        return 0;
    }

    private static int getPOSIXGroupId() {
        return 0;
    }

    @Override
    public Object generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv2;
    }
}
