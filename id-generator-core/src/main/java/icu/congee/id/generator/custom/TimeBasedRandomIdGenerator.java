// 定义包路径，指明该类所在的包结构
package icu.congee.id.generator.custom;

// 导入Java并发包中的ThreadLocalRandom类，用于生成线程安全的随机数
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于时间戳和随机数的ID生成器
 *
 * <p>
 * 该类用于生成64位长整型ID，其中高32位为时间戳（相对于自定义纪元），低32位为随机数。
 * 这种设计可以保证ID按时间递增，同时在同一时间点生成的ID具有唯一性。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class TimeBasedRandomIdGenerator implements IdGenerator {

    /** Tuesday, February 22, 2022 2:22:22.00 PM GMT-05:00 该值是RFC中测试时多次提到的值 */
    private static final long EPOCH = 1645557742L;

    private static final ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * 生成一个新的基于时间和随机数的ID
     *
     * <p>
     * 该方法创建并返回一个64位长整型ID，其结构如下： - 高32位：当前时间（秒级）减去自定义纪元时间 - 低32位：随机数
     *
     * @return 新生成的64位长整型ID
     */
    public static long next() {
        // 计算时间戳部分
        // System.currentTimeMillis() - 获取当前系统时间的毫秒数
        // / 1000 - 将毫秒转换为秒
        // - EPOCH - 减去自定义纪元时间，得到相对时间戳
        long timestamp = System.currentTimeMillis() / 1000 - EPOCH;

        // 生成随机数部分
        // ThreadLocalRandom.current() - 获取当前线程的随机数生成器实例
        // .nextInt() - 生成一个随机整数
        // & 0xFFFFFFFFL - 使用按位与操作只保留低32位，确保随机部分为正数
        long randomPart = random.nextLong() & 0xFFFFFFFFL;

        // 组合时间戳和随机数
        // timestamp << 32 - 将时间戳左移32位，放置在高32位位置
        // | randomPart - 使用按位或操作将随机数部分放在低32位位置
        // 返回组合后的64位长整型ID
        return timestamp << 32 | randomPart;
    }

    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.TimeBasedRandomId;
    }
}
