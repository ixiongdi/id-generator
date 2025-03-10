package icu.congee.id.generator.borid;

import java.util.BitSet;

/**
 * BorId生成器
 * 用于生成BorId实例
 */
public class BorIdGenerator {
    
    private final BorIdLayout layout;
    
    /**
     * 构造函数
     * 
     * @param layout BorId结构
     */
    public BorIdGenerator(BorIdLayout layout) {
        this.layout = layout;
    }
    
    /**
     * 生成下一个BorId
     * 
     * @return 生成的BorId
     */
    public BorId next() {
        BitSet result = new BitSet(layout.getTotalBits());
        int currentPosition = 0;
        
        // 遍历每个部分并获取其生成的BitSet
        for (BorIdPart part : layout.getParts()) {
            BitSet partBits = part.next();
            
            // 将部分的位复制到结果中
            for (int i = 0; i < part.getBits(); i++) {
                result.set(currentPosition + i,  partBits.get(i));
            }
            currentPosition += part.getBits();
        }

        byte[] byteArray = bitSetToByteArrayManual(result);


        return new BorId(byteArray);
    }

    public static byte[] bitSetToByteArrayManual(BitSet bitSet) {
        int bits = bitSet.length(); // 最大的置位索引 +1
        int numBytes = (bits + 7) / 8; // 计算所需字节数（向上取整）

        byte[] bytes = new byte[numBytes];
        for (int i = 0; i < bits; i++) {
            if (bitSet.get(i)) {
                int byteIndex = i / 8; // 计算字节索引
                int bitIndex = i % 8;  // 计算字节内的位位置
                bytes[byteIndex] |= (1 << bitIndex); // 设置该位为1
            }
        }
        return bytes;
    }
    
    /**
     * 获取BorId结构
     * 
     * @return BorId结构
     */
    public BorIdLayout getLayout() {
        return layout;
    }
}