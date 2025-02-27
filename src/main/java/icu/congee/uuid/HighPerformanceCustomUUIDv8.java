package icu.congee.uuid;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class HighPerformanceCustomUUIDv8 {

    private static final SecureRandom random = new SecureRandom();
    private static final AtomicLong sequenceNumber = new AtomicLong(0);

    public static String generateCustomUUIDv8() {
        // 48-bit timestamp (in milliseconds since epoch)
        long timestampPart = System.currentTimeMillis(); // Mask to get lower 48 bits

        // 4-bit version number (custom UUIDv8)
        long version = 8;

        // 12-bit sequence number (incremented atomically)
        long seqNum = sequenceNumber.getAndIncrement(); // Ensure it wraps around after reaching max 12-bit value

        // 2-bit variant
        long variant = 2; // RFC 4122 variant in binary is '10', which is 2 in decimal

        // 62-bit random number
        long randomNumber = random.nextLong();

        // Combine all parts into a single long array

        long high = (timestampPart << 16) | (version << 12) | seqNum;
        long low  = (variant << 62) | randomNumber;

        // Convert to hexadecimal string for readability
        return new UUID(high, low).toString();
    }


    public static void main(String[] args) {
        System.out.println(generateCustomUUIDv8());
        System.out.println(generateCustomUUIDv8());
        System.out.println(generateCustomUUIDv8());
    }
}



