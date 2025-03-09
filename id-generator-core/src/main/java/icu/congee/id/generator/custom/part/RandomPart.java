package icu.congee.id.generator.custom.part;

import icu.congee.id.generator.custom.IdPartGenerator;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 随机数部分实现类
 * <p>
 * 该类继承自IdPart抽象类，实现了随机数部分的生成逻辑。
 * 支持自定义随机数生成器和默认的线程安全随机数生成。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class RandomPart extends IdPart {
    
    // 随机数生成器
    private final IdPartGenerator<Long> generator;
    
    /**
     * 使用默认配置创建随机数部分
     *
     * @param bits 随机数占用的位数
     */
    public RandomPart(int bits) {
        this(bits, () -> ThreadLocalRandom.current().nextLong(1L << bits));
    }
    
    /**
     * 使用固定值创建随机数部分
     *
     * @param bits 随机数占用的位数
     * @param value 固定的随机数值
     */
    public RandomPart(int bits, long value) {
        this(bits, () -> value);
        
        // 验证随机数是否在有效范围内
        if (value < 0 || value > maxValue) {
            throw new IllegalArgumentException("随机数值必须不小于0且不超过" + maxValue);
        }
    }
    
    /**
     * 使用自定义生成器创建随机数部分
     *
     * @param bits 随机数占用的位数
     * @param generator 自定义随机数生成器
     */
    public RandomPart(int bits, IdPartGenerator<Long> generator) {
        super(bits);
        this.generator = generator;
    }
    
    /**
     * 生成随机数值
     *
     * @return 生成的随机数值
     */
    @Override
    public long generate() {
        return generator.generate();
    }
}