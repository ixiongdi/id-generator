package com.github.ixiongdi.id.generator.lexical;

/**
 * 一个基于微秒级时间戳的时钟实现
 * <p>
 * 注意：这个实现实际上并不提供真正的微秒级精度，因为大多数平台都无法可靠地访问微秒级精度的时钟。
 * 它返回的是毫秒时间戳乘以1000。尽管如此，它仍然是严格递增的，所以即使对MicrosecondEpochClock#timestamp
 * 的调用发生在同一毫秒内，返回的时间戳也会按正确的顺序排列。
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class MicrosecondEpochClock extends StrictlyClock {
    private static final MicrosecondEpochClock INSTANCE = new MicrosecondEpochClock();

    private MicrosecondEpochClock() {
        // 私有构造函数，防止外部实例化
    }

    /**
     * 获取MicrosecondEpochClock的单例实例
     *
     * @return MicrosecondEpochClock实例
     */
    public static MicrosecondEpochClock getInstance() {
        return INSTANCE;
    }

    @Override
    protected long tick() {
        return System.currentTimeMillis() * 1000;
    }
}