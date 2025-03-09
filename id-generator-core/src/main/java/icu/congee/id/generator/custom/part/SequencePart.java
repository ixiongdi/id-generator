package icu.congee.id.generator.custom.part;

import icu.congee.id.generator.custom.IdPartGenerator;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 序列号部分实现类
 * <p>
 * 该类继承自IdPart抽象类，实现了序列号部分的生成逻辑。
 * 支持自定义序列号生成器和默认的自增序列生成。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class SequencePart extends IdPart {
    
    // 默认序列号计数器
    private static final AtomicLong DEFAULT_COUNTER = new AtomicLong(0);
    
    // 序列号生成器
    private final IdPartGenerator<Long> generator;
    
    /**
     * 使用默认配置创建序列号部分
     *
     * @param bits 序列号占用的位数
     */
    public SequencePart(int bits) {
        this(bits, () -> DEFAULT_COUNTER.getAndIncrement() & (~(-1L << bits)));
    }
    
    /**
     * 使用自定义计数器创建序列号部分
     *
     * @param bits 序列号占用的位数
     * @param counter 自定义计数器
     */
    public SequencePart(int bits, AtomicLong counter) {
        this(bits, () -> counter.getAndIncrement() & (~(-1L << bits)));
    }
    
    /**
     * 使用自定义生成器创建序列号部分
     *
     * @param bits 序列号占用的位数
     * @param generator 自定义序列号生成器
     */
    public SequencePart(int bits, IdPartGenerator<Long> generator) {
        super(bits);
        this.generator = generator;
    }
    
    /**
     * 生成序列号值
     *
     * @return 生成的序列号值
     */
    @Override
    public long generate() {
        return generator.generate();
    }
    
    /**
     * 重置序列号（仅当使用默认计数器时有效）
     */
    public static void resetDefaultCounter() {
        DEFAULT_COUNTER.set(0);
    }
}