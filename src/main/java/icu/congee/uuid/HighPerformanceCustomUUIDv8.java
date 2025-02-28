package icu.congee.uuid;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class HighPerformanceCustomUUIDv8 {

    private static final ThreadLocal<Sequence> threadLocalTimestampSeq =
            ThreadLocal.withInitial(Sequence::new);

    public static UUID generateCustomUUIDv8() {
        return new UUID(
                ((System.currentTimeMillis() & 0xFFFFFFFFFFFFL) << 16)
                        | 0x8000L
                        | ((threadLocalTimestampSeq.get().sequence++) & 0xFFF),
                0x8000000000000000L
                        | (ThreadLocalRandom.current().nextLong() & 0x3FFFFFFFFFFFFFFFL));
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 4096; i++) {
            UUID uuid = generateCustomUUIDv8();
            System.out.println("UUID: " + uuid);
            System.out.println("Version: " + uuid.version()); // 8
            System.out.println("Variant: " + uuid.variant()); // 2
        }
    }

    private static class Sequence {
        int sequence;
    }
}
