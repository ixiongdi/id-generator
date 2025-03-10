package icu.congee.id.generator.borid;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** BorId结构定义 定义ID的结构，由多个BorIdPart组成 */
public class BorIdLayout {

    private final List<BorIdPart> parts;
    private final int totalBits;

    /**
     * 构造函数
     *
     * @param parts BorIdPart列表
     */
    public BorIdLayout(List<BorIdPart> parts) {
        this.parts = new ArrayList<>(parts);
        int bits = 0;
        for (BorIdPart part : parts) {
            bits += part.getBits();
        }
        this.totalBits = bits;

        // 验证总位数是否为8的倍数，以便能够正确转换为字节数组
        if (totalBits % 8 != 0) {
            throw new IllegalArgumentException("总位数必须是8的倍数，当前为: " + totalBits);
        }
    }

    /**
     * 获取BorIdPart列表
     *
     * @return BorIdPart列表
     */
    public List<BorIdPart> getParts() {
        return new ArrayList<>(parts);
    }

    /**
     * 获取总位数
     *
     * @return 总位数
     */
    public int getTotalBits() {
        return totalBits;
    }

    /**
     * 获取总字节数
     *
     * @return 总字节数
     */
    public int getTotalBytes() {
        return totalBits / 8;
    }

    /**
     * 将所有部分组合成一个字节数组
     *
     * @return 组合后的字节数组
     */
    public byte[] combine() {
        BitSet result = new BitSet(totalBits);
        int currentPosition = 0;

        // 按照每个部分的位长度，将其BitSet值复制到结果BitSet中
        for (BorIdPart part : parts) {
            BitSet partBits = part.next();
            for (int i = 0; i < part.getBits(); i++) {
                if (partBits.get(i)) {
                    result.set(currentPosition + i);
                }
            }
            currentPosition += part.getBits();
        }

        // 将BitSet转换为字节数组
        byte[] bytes = new byte[(totalBits + 7) / 8];
        for (int i = 0; i < result.length(); i++) {
            if (result.get(i)) {
                bytes[i / 8] |= 1 << (i % 8);
            }
        }
        return bytes;
    }
}
