package icu.congee.LexicalUUID;

public final class FNV1A {
    private static final long OFFSET_BASIS = 0xcbf29ce484222325L;
    private static final long PRIME = 0x100000001b3L;

    // 工具类禁止实例化
    private FNV1A() {}

    /**
     * FNV1-A 64位哈希算法实现
     * @param bytes 输入字节数组
     * @return 64位哈希值
     * @throws NullPointerException 如果输入为null
     */
    public static long hash(byte[] bytes) {
        long hash = OFFSET_BASIS;
        for (byte b : bytes) {
            // 将字节视为无符号处理 (0x00 ~ 0xFF)
            hash = (hash ^ (b & 0xFFL)) * PRIME;
        }
        return hash;
    }
}