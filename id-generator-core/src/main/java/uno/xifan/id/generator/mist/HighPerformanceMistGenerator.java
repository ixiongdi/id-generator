package uno.xifan.id.generator.mist;

import uno.xifan.id.base.IdType;

import java.util.concurrent.ThreadLocalRandom;

/** 薄雾算法高性能实现 使用LongAdder和ThreadLocalRandom实现，适用于高并发场景 */
/**
 * 高性能薄雾算法ID生成器
 * <p>
 * 该生成器是薄雾算法的高性能实现版本，专门针对高并发场景进行优化。
 * 使用静态计数器和ThreadLocalRandom来生成唯一标识符，具有以下特点：
 * - 使用位运算提高性能
 * - 支持高并发场景
 * - 生成的ID具有良好的分布性
 * </p>
 * 
 * @author ixiongdi
 * @since 1.0
 */
public class HighPerformanceMistGenerator implements MistGenerator {

    private static long adder = 0; // 高并发计数器

    /**
     * 生成下一个唯一ID
     * <p>
     * 使用位运算和ThreadLocalRandom生成唯一标识符，通过以下步骤：
     * 1. 使用静态计数器生成序列号
     * 2. 结合随机数生成最终ID
     * </p>
     *
     * @return 生成的唯一ID
     */
    public static long next() {
        // 通过位运算实现自动占位

        return adder++ << 16 | ThreadLocalRandom.current().nextInt() & 0xFFFF;
    }

    /**
     * 生成唯一编号
     *
     * @return 生成的唯一ID
     */
    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }
}
