package icu.congee.uuid;

import icu.congee.IdGenerator;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 一个高性能的自定义 UUID v8 生成器。 该类通过结合时间戳、线程本地序列和随机数生成 UUID，确保高效性和唯一性。
 *
 * @author [您的姓名]
 */
public class UUIDv8Generator implements IdGenerator {

    // 常量定义，用于位掩码和版本/变体的标识
    /** 时间戳掩码，占用 48 位 */
    private static final long TIMESTAMP_MASK = 0xFFFFFFFFFFFFL;

    /** UUID 版本 8 的标识符 */
    private static final long VERSION_IDENTIFIER = 0x8000L;

    /** 序列号掩码，占用 12 位 */
    private static final long SEQUENCE_MASK = 0xFFF;

    /** UUID 变体 2 的标识符 */
    private static final long VARIANT_IDENTIFIER = 0x8000000000000000L;

    /** 随机数掩码，占用 62 位 */
    private static final long RANDOM_MASK = 0x3FFFFFFFFFFFFFFFL;

    // 线程本地的序列生成器，用于确保线程间的唯一性
    private static final ThreadLocal<ThreadLocalSequence> threadLocalTimestampSeq =
            ThreadLocal.withInitial(ThreadLocalSequence::new);

    /**
     * 生成一个自定义的 UUID v8。 该方法使用当前时间戳、线程本地序列和随机数构建 UUID。
     *
     * @return 一个新的自定义 UUID v8
     */
    public static java.util.UUID next() {
        ThreadLocalSequence seq = threadLocalTimestampSeq.get();
        long timestamp = System.currentTimeMillis() & TIMESTAMP_MASK;
        long sequence = seq.sequence++ & SEQUENCE_MASK;
        long mostSigBits = (timestamp << 16) | VERSION_IDENTIFIER | sequence;
        long leastSigBits =
                VARIANT_IDENTIFIER | (ThreadLocalRandom.current().nextLong() & RANDOM_MASK);
        return new java.util.UUID(mostSigBits, leastSigBits);
    }

    @Override
    public Object generate() {
        return next();
    }

    /** 线程本地序列持有者。 每个线程拥有独立的序列号，以避免线程间的竞争。 */
    private static class ThreadLocalSequence {
        /** 序列号初始值 */
        long sequence;
    }
}
