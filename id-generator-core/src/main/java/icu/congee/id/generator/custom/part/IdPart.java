package icu.congee.id.generator.custom.part;

import java.nio.ByteBuffer;

/**
 * ID部分抽象类
 * <p>
 * 该抽象类定义了ID生成器中各个部分(时间戳、工作节点ID、序列号、随机数)的通用行为和属性。
 * 提供了生成和获取值的统一接口，以及位数限制的处理逻辑。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public abstract class IdPart {
    
    // 该部分占用的位数
    protected final int bits;
    
    // 该部分的当前值
    protected long value;
    
    // 该部分的最大值（由位数决定）
    protected final long maxValue;
    
    /**
     * 构造函数
     *
     * @param bits 该部分占用的位数
     */
    protected IdPart(int bits) {
        this.bits = bits;
        this.maxValue = ~(-1L << bits);
    }
    
    /**
     * 生成该部分的值
     * 
     * @return 生成的值
     */
    public abstract long generate();
    
    /**
     * 刷新并获取该部分的值
     * 
     * @return 刷新后的值
     */
    public long refreshAndGet() {
        this.value = generate() & maxValue; // 确保值不超过位数限制
        return this.value;
    }
    
    /**
     * 获取当前值
     * 
     * @return 当前值
     */
    public long getValue() {
        return value;
    }
    
    /**
     * 获取该部分的字节数组表示
     * 
     * @return 字节数组
     */
    public byte[] getBytes() {
        // 根据位数确定需要的字节数
        int byteCount = (bits + 7) / 8; // 向上取整到字节
        ByteBuffer buffer = ByteBuffer.allocate(byteCount);
        
        // 将long值转换为字节数组
        for (int i = 0; i < byteCount; i++) {
            int shift = (byteCount - 1 - i) * 8;
            buffer.put((byte) ((value >> shift) & 0xFF));
        }
        
        return buffer.array();
    }
    
    /**
     * 获取该部分占用的位数
     * 
     * @return 位数
     */
    public int getBits() {
        return bits;
    }
    
    /**
     * 获取该部分的最大值
     * 
     * @return 最大值
     */
    public long getMaxValue() {
        return maxValue;
    }
}