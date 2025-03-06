package com.github.ixiongdi.id.generator.mist;

import com.github.ixiongdi.id.core.IdType;

import java.util.concurrent.ThreadLocalRandom;

/** 薄雾算法高性能实现 使用LongAdder和ThreadLocalRandom实现，适用于高并发场景 */
public class HighPerformanceMistGenerator implements MistGenerator {

    private static long adder = 0; // 高并发计数器

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
        return IdType.MIST_FAST_ID;
    }
}
