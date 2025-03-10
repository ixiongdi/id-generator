package icu.congee.id.generator.borid;

import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 基于计数器的BorId部分实现
 * 默认实现为全局自增的值，支持传入时间参数
 */
public class CounterBorIdPart implements BorIdPart {

    private final int bits;
    private static final AtomicLong COUNTER = new AtomicLong(0);
    private long lastTimestamp = -1L;
    private final Function<Byte, BitSet> supplier;

    /**
     * 构造函数
     * 
     * @param bits 位长度，必须是8的倍数
     */
    public CounterBorIdPart(int bits) {
        this(bits, null);
    }

    /**
     * 构造函数
     * 
     * @param bits     位长度
     * @param supplier 自定义值生成器
     */
    public CounterBorIdPart(int bits, Function<Byte, BitSet> supplier) {
        this.bits = bits;
        this.supplier = supplier;
    }

   

    @Override
    public int getBits() {
        return bits;
    }

    @Override
    public BitSet next() {
        // 每次调用next时都重新生成值
        return this.supplier.apply((byte) this.bits);
    }

    /**
     * 生成基于计数器的BitSet
     * 
     * @param timestamp 可选的时间戳参数，如果为null则使用全局计数器
     * @return BitSet对象
     */
    private BitSet generateValue(Long timestamp) {
        long counterValue;

        if (timestamp != null) {
            // 如果时间戳变化，重置计数器
            if (timestamp != lastTimestamp) {
                COUNTER.set(0);
                lastTimestamp = timestamp;
            }
            counterValue = COUNTER.getAndIncrement();
        } else {
            // 使用全局计数器
            counterValue = COUNTER.getAndIncrement();
        }

        BitSet bitSet = new BitSet(bits);

        // 将counterValue转换为BitSet
        for (int i = 0; i < bits && i < 64; i++) { // long最多64位
            if ((counterValue & (1L << i)) != 0) {
                bitSet.set(i);
            }
        }

        return bitSet;
    }

}