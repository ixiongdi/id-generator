package icu.congee.id.generator.borid;

import icu.congee.id.base.Base62Codec;
import java.util.BitSet;

/**
 * BorId部分编码器接口
 * 用于将BitSet编码为特定格式
 */
public interface BorIdPartEncoder {

    /**
     * 将BitSet编码为特定格式
     * 
     * @param bits 要编码的BitSet
     * @return 编码后的对象
     */
    Object encode(BitSet bits);

    /**
     * 默认的Base62编码器实现
     */
    BorIdPartEncoder BASE62_ENCODER = bits -> {
        // 将BitSet转换为字节数组
        byte[] bytes = new byte[(bits.length() + 7) / 8];
        for (int i = 0; i < bits.length(); i++) {
            if (bits.get(i)) {
                bytes[i / 8] |= (byte) (1 << (i % 8));
            }
        }
        return Base62Codec.encode(bytes);
    };
}