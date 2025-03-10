package icu.congee.id.generator.broid;

import java.util.ArrayList;
import java.util.List;

/** 位操作工具类 */
public class BitUtils {

    /**
     * 将long值转换为List<Boolean>
     *
     * @param value long值
     * @param bits 位数
     * @return List<Boolean>对象
     */
    public static List<Boolean> longToList(long value, int bits) {
        if (bits < 0 || bits > 64) {
            throw new IllegalArgumentException("bits 必须在 0 到 64 之间");
        }

        List<Boolean> booleanList = new ArrayList<>(bits);
        for (int i = bits - 1; i >= 0; i--) {
            boolean isSet = ((value >> i) & 1) == 1;
            booleanList.add(isSet);
        }

        return booleanList;
    }

    /**
     * 将List<Boolean>转换为byte数组
     *
     * @param booleanList List<Boolean>对象
     * @return byte数组
     */
    public static byte[] listToByteArray(List<Boolean> booleanList) {
        int size = booleanList.size();
        int byteSize = (size + 7) / 8;
        byte[] bytes = new byte[byteSize];

        for (int i = 0; i < size; i++) {
            if (booleanList.get(i)) {
                bytes[i / 8] |= (byte) (1 << (7 - (i % 8)));
            }
        }

        return bytes;
    }

    /**
     * 将List<Boolean>转换为long值
     *
     * @param booleanList List<Boolean>对象
     * @return long值
     */
    public static long listToLong(List<Boolean> booleanList) {
        long result = 0;
        int size = Math.min(booleanList.size(), 64);

        for (int i = 0; i < size; i++) {
            if (booleanList.get(i)) {
                result |= (1L << (size - 1 - i));
            }
        }

        return result;
    }
}
