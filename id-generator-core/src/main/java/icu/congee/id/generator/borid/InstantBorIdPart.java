package icu.congee.id.generator.borid;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.util.BitSet;
import java.util.function.Function;

/**
 * 基于时间戳的BorId部分实现
 * 默认实现为当前时间戳
 */
public class InstantBorIdPart implements BorIdPart {
    
    private final int bits;
    private final Function<Byte, BitSet> function;
    
    /**
     * 构造函数
     * 
     * @param bits 位长度，必须是8的倍数
     */
    public InstantBorIdPart(int bits) {
        this(bits, null);
    }
    
    /**
     * 构造函数
     * 
     * @param bits 位长度
     * @param function 自定义值生成器
     */
    public InstantBorIdPart(int bits, Function<Byte, BitSet> function) {
        this.bits = bits;
        this.function = function;
    }
    
    
    @Override
    public int getBits() {
        return bits;
    }
    
    @Override
    public BitSet next() {

        return this.function.apply((byte) this.bits);
    }

}