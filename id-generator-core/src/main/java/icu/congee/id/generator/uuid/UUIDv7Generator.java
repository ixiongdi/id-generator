// 定义包路径，指明该类所在的包结构
package icu.congee.id.generator.uuid;

// 导入Java标准库中的UUID类，用于创建和操作UUID
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
 * @copyright Copyright (c) 2024 ixiongdi. All rights reserved.
 */
public class UUIDv7Generator {
    /**
     * 生成一个新的UUIDv7
     * <p>
     * 该方法创建并返回一个新的UUIDv7实例，其中包含当前时间戳和随机数据。
     * UUIDv7的结构如下：
     * - 最高有效位(MSB)：48位时间戳 + 4位版本号(7) + 12位随机数
     * - 最低有效位(LSB)：2位变体标识 + 62位随机数
     * </p>
     *
     * @return 新生成的UUIDv7实例
     */
    public static UUID next() {
        // 构建最高有效位(MSB)
        // System.currentTimeMillis() - 获取当前系统时间的毫秒数
        // << 16 - 将时间戳左移16位，为版本号和随机数留出空间
        // | 0x7000 - 使用按位或操作添加版本号7（0111在二进制中）
        // ThreadLocalRandom.current().nextInt() - 获取一个线程安全的随机整数
        // & 0xFFF - 使用按位与操作只保留随机数的低12位
        long msb = System.currentTimeMillis() << 16 | 0x7000 | ThreadLocalRandom.current().nextInt() & 0xFFF;
        
        // 构建最低有效位(LSB)
        // 0x8000000000000000L - 设置变体位为2（RFC 4122规范）
        // | - 使用按位或操作合并变体位和随机数
        // ThreadLocalRandom.current().nextLong() - 获取一个线程安全的随机长整数
        // & 0x3FFFFFFFFFFFFFFFL - 使用按位与操作确保不会覆盖变体位，只使用低62位
        long lsb = 0x8000000000000000L | ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL;
        
        // 使用构建好的MSB和LSB创建并返回一个新的UUID实例
        return new UUID(msb, lsb);
    }


}
