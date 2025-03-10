package icu.congee.id.generator.borid;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 基于随机数的BorId部分实现
 * 默认实现为Random.nextLong
 */
public class RandomBorIdPart implements BorIdPart {

    private final int bits;
    private final Random random;
    private final Function<Byte, BitSet> supplier;

    /**
     * 构造函数
     * 
     * @param bits 位长度
     */
    public RandomBorIdPart(int bits) {
        this(bits, null);
    }

    /**
     * 构造函数
     * 
     * @param bits     位长度
     * @param supplier 自定义值生成器
     */
    public RandomBorIdPart(int bits, Function<Byte, BitSet> supplier) {
        this.bits = bits;
        this.random = new Random();
        this.supplier = supplier;
    }

    @Override
    public int getBits() {
        return bits;
    }

    @Override
    public BitSet next() {
        return supplier.apply((byte) this.bits);
    }

    /**
     * 生成基于随机数的BitSet
     * 
     * @return BitSet对象
     */
    private BitSet generateValue() {
        BitSet bitSet = new BitSet(bits);

        // 对于任意长度，生成随机的BitSet
        for (int i = 0; i < bits; i++) {
            if (random.nextBoolean()) {
                bitSet.set(i);
            }
        }

        return bitSet;
    }
}