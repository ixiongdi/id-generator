package icu.congee.uuid;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

public class UUIDv7Generator {
    private static final long EPOCH_OFFSET = Instant.parse("2020-01-01T00:00:00Z").toEpochMilli();
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();
    private static final ThreadLocal<byte[]> RANDOM_BUFFER = ThreadLocal.withInitial(() -> new byte[10]);
    private static final ThreadLocal<char[]> STRING_BUFFER = ThreadLocal.withInitial(() -> new char[36]);

    public static String generate() {
        long timestamp = System.currentTimeMillis() - EPOCH_OFFSET;
        long msb = (timestamp << 16) & 0xFFFFFFFFFFFF0000L;
        msb |= 0x7000L; // Version 7

        byte[] randBytes = RANDOM_BUFFER.get();
        SECURE_RANDOM.nextBytes(randBytes);

        msb |= ((randBytes[0] & 0xFFL) << 8) | (randBytes[1] & 0xFFL);
        msb |= (randBytes[2] & 0xFFL) << 24;

        long lsb = (0x8000L << 48) // Variant 10b
                | ((randBytes[3] & 0xFFL) << 48)
                | ((randBytes[4] & 0xFFL) << 40)
                | ((randBytes[5] & 0xFFL) << 32)
                | ((randBytes[6] & 0xFFL) << 24)
                | ((randBytes[7] & 0xFFL) << 16)
                | ((randBytes[8] & 0xFFL) << 8)
                | (randBytes[9] & 0xFFL);

        return toString(msb, lsb);
    }

    public static String[] generateBatch(int size) {
        String[] result = new String[size];
        long baseTimestamp = System.currentTimeMillis() - EPOCH_OFFSET;
        byte[] bulkRand = new byte[size * 10];
        SECURE_RANDOM.nextBytes(bulkRand);

        for (int i = 0; i < size; i++) {
            int offset = i * 10;
            long ts = (baseTimestamp << 16) | (i & 0xFFFF);
            long msb = (ts & 0xFFFFFFFFFFFF0000L) | 0x7000L;

            msb |= ((bulkRand[offset] & 0xFFL) << 8) | (bulkRand[offset + 1] & 0xFFL);
            msb |= (bulkRand[offset + 2] & 0xFFL) << 24;

            long lsb = (0x8000L << 48)
                    | ((bulkRand[offset + 3] & 0xFFL) << 48)
                    | ((bulkRand[offset + 4] & 0xFFL) << 40)
                    | ((bulkRand[offset + 5] & 0xFFL) << 32)
                    | ((bulkRand[offset + 6] & 0xFFL) << 24)
                    | ((bulkRand[offset + 7] & 0xFFL) << 16)
                    | ((bulkRand[offset + 8] & 0xFFL) << 8)
                    | (bulkRand[offset + 9] & 0xFFL);

            result[i] = toString(msb, lsb);
        }
        return result;
    }

    private static String toString(long msb, long lsb) {
        char[] buf = STRING_BUFFER.get();

        // Format: 8-4-4-4-12 (36 chars with hyphens)
        writeHex(buf, 0, msb >>> 32, 8);
        buf[8] = '-';
        writeHex(buf, 9, msb >>> 16, 4);
        buf[13] = '-';
        writeHex(buf, 14, msb, 4);
        buf[18] = '-';
        writeHex(buf, 19, lsb >>> 48, 4);
        buf[23] = '-';
        writeHex(buf, 24, lsb, 12);

        return new String(buf);
    }

    private static void writeHex(char[] buf, int offset, long value, int digits) {
        for (int i = digits - 1; i >= 0; i--) {
            int digit = (int) (value & 0xF);
            buf[offset + i] = HEX_CHARS[digit];
            value >>>= 4;
        }
    }

    public static void main(String[] args) {
        // Single UUID test
        String uuid = generate();
        System.out.println("UUIDv7: " + uuid);
        UUID parsed = UUID.fromString(uuid);
        System.out.println("Version: " + parsed.version()); // Should be 7
        System.out.println("Variant: " + parsed.variant()); // Should be 2

        // Performance test
        long start = System.currentTimeMillis();
        int batchSize = 1_000_000;
        String[] batch = generateBatch(batchSize);
        long end = System.currentTimeMillis();
        System.out.printf("Generated %d UUIDs in %d ms (%.2f UUIDs/sec)%n",
                batchSize, (end - start), batchSize * 1000.0 / (end - start));
    }
}