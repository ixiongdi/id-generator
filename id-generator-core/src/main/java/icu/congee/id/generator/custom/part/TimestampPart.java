package icu.congee.id.generator.custom.part;

import icu.congee.id.generator.custom.IdPartGenerator;

/**
 * 时间戳部分实现类
 * <p>
 * 该类继承自IdPart抽象类，实现了时间戳部分的生成逻辑。
 * 支持自定义时间戳生成器和纪元时间配置。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class TimestampPart extends IdPart {
    
    // 默认纪元时间：2022-02-22 14:22:22 GMT-05:00
    private static final long DEFAULT_EPOCH = 1645557742L;
    
    // 纪元时间
    private final long epoch;
    
    // 时间戳生成器
    private final IdPartGenerator<Long> generator;
    
    /**
     * 使用默认配置创建时间戳部分
     *
     * @param bits 时间戳占用的位数
     */
    public TimestampPart(int bits) {
        this(bits, DEFAULT_EPOCH, () -> System.currentTimeMillis() - DEFAULT_EPOCH);
    }
    
    /**
     * 使用自定义纪元时间创建时间戳部分
     *
     * @param bits 时间戳占用的位数
     * @param epoch 自定义纪元时间
     */
    public TimestampPart(int bits, long epoch) {
        this(bits, epoch, () -> System.currentTimeMillis() - epoch);
    }
    
    /**
     * 使用自定义生成器创建时间戳部分
     *
     * @param bits 时间戳占用的位数
     * @param generator 自定义时间戳生成器
     */
    public TimestampPart(int bits, IdPartGenerator<Long> generator) {
        this(bits, DEFAULT_EPOCH, generator);
    }
    
    /**
     * 使用完全自定义配置创建时间戳部分
     *
     * @param bits 时间戳占用的位数
     * @param epoch 自定义纪元时间
     * @param generator 自定义时间戳生成器
     */
    public TimestampPart(int bits, long epoch, IdPartGenerator<Long> generator) {
        super(bits);
        this.epoch = epoch;
        this.generator = generator;
    }
    
    /**
     * 生成时间戳值
     *
     * @return 生成的时间戳值
     */
    @Override
    public long generate() {
        return generator.generate();
    }
    
    /**
     * 获取纪元时间
     *
     * @return 纪元时间
     */
    public long getEpoch() {
        return epoch;
    }
}