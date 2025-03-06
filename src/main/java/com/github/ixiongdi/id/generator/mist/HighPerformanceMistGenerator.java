package com.github.ixiongdi.id.generator.mist;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.LongAdder;

/**
 * 薄雾算法高性能实现
 * 使用LongAdder和ThreadLocalRandom实现，适用于高并发场景
 */
public class HighPerformanceMistGenerator implements MistGenerator {
    private static final int SALT_BIT = 8; // 随机因子二进制位数
    private static final int SALT_SHIFT = 8; // 随机因子移位数
    private static final int INCREAS_SHIFT = SALT_BIT + SALT_SHIFT; // 自增数移位数
    private static final int MAX_SALT_VALUE = 255; // 随机因子最大值

    private final LongAdder increas = new LongAdder(); // 高并发计数器
    private final ThreadLocalRandom random = ThreadLocalRandom.current(); // 线程安全的随机数生成器

    public HighPerformanceMistGenerator() {
        increas.add(1); // 初始值设为1
    }

    /**
     * 生成唯一编号
     * 
     * @return 生成的唯一ID
     */
    @Override
    public long generate() {
        // 增加计数
        increas.increment();
        long increasValue = increas.sum();

        // 获取随机因子数值
        long saltA = random.nextInt(MAX_SALT_VALUE + 1);
        long saltB = random.nextInt(MAX_SALT_VALUE + 1);

        // 通过位运算实现自动占位
        return (increasValue << INCREAS_SHIFT) | (saltA << SALT_SHIFT) | saltB;
    }

    /**
     * 获取单例实例
     */
    private static class SingletonHolder {
        private static final HighPerformanceMistGenerator INSTANCE = new HighPerformanceMistGenerator();
    }

    /**
     * 获取HighPerformanceMistGenerator的单例实例
     * 
     * @return HighPerformanceMistGenerator实例
     */
    public static HighPerformanceMistGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }
}