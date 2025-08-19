package uno.xifan.id.generator.uuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * UUIDv4生成器
 * <p>
 * 该类用于生成符合UUIDv4规范的UUID。UUIDv4是一种基于随机数的UUID版本，
 * 它使用随机或伪随机数据生成UUID，提供了高度的唯一性保证，但不包含时间信息。
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 * @author ixiongdi
 */
public class UUIDv4Generator implements IdGenerator {
    // 常量定义，用于位掩码和版本/变体的标识
    /** UUID 版本 4 的标识符 */
    private static final long VERSION_IDENTIFIER = 0x4000L;

    /** UUID 变体 2 的标识符（RFC 4122规范） */
    private static final long VARIANT_IDENTIFIER = 0x8000000000000000L;

    /**
     * 生成一个新的UUIDv4
     * <p>
     * 该方法创建并返回一个新的UUIDv4实例，其中包含随机生成的数据。
     * UUIDv4的结构如下：
     * - 最高有效位(MSB)：60位随机数 + 4位版本号(4)
     * - 最低有效位(LSB)：2位变体标识 + 62位随机数
     * </p>
     *
     * @return 新生成的UUIDv4实例
     */
    public static UUID next() {
        // 获取随机数生成器
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 生成两个随机长整型数
        long msb = random.nextLong();
        long lsb = random.nextLong();

        // 设置版本号（版本4）
        // 清除版本位（第49-52位）并设置为版本4
        msb = (msb & 0xFFFFFFFFFFFF0FFFL) | VERSION_IDENTIFIER;

        // 设置变体（RFC 4122变体）
        // 清除变体位（第65-66位）并设置为RFC 4122变体
        lsb = (lsb & 0x3FFFFFFFFFFFFFFFL) | VARIANT_IDENTIFIER;

        // 使用构建好的MSB和LSB创建并返回一个新的UUID实例
        return new UUID(msb, lsb);
    }

    @Override
    public Object generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.UUIDv4;
    }
}