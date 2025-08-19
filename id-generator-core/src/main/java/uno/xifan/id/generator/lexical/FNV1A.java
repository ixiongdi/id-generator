package uno.xifan.id.generator.lexical;

/**
 * FNV1A哈希算法实现
 * 用于生成分布式系统中的workerId
 */
public class FNV1A {
    private static final long FNV_64_INIT = 0xcbf29ce484222325L;
    private static final long FNV_64_PRIME = 0x100000001b3L;

    public static long hash(byte[] bytes) {
        long hash = FNV_64_INIT;
        for (byte b : bytes) {
            hash ^= (b & 0xff);
            hash *= FNV_64_PRIME;
        }
        return hash;
    }

    public static long hash(String str) {
        return hash(str.getBytes());
    }
}