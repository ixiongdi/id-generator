package icu.congee.id.generator.borid;

import java.util.BitSet;
import java.util.function.Function;

/**
 * BorId组成部分接口
 * ID组成部分，是一个接口，属性value返回一个BitSet，bit限制字段长度
 */
public interface BorIdPart {

    /**
     * 获取该部分的位长度
     * 
     * @return 位长度
     */
    int getBits();

    /**
     * 生成下一个值
     * 
     * @return 生成的BitSet对象
     */
    BitSet next();

    /**
     * 编码该部分的值
     * 
     * @param encoder 自定义编码器
     * @return 编码后的对象
     */
    default Object encode(Function<BitSet, Object> encoder) {
        return encoder.apply(next());
    }

    /**
     * 将BitSet转换为字节数组
     * 
     * @return 字节数组
     */
    default byte[] toByteArray() {
        BitSet bits = next();
        byte[] bytes = new byte[(bits.length() + 7) / 8];

        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[i / 8] |= (byte) (1 << (i % 8));
            }
        }

        return bytes;
    }
}