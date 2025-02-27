package icu.congee;

import java.math.BigInteger;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

public class FlakeIdGenerator {
    private final long workerId;              // 48-bit worker ID
    private final AtomicInteger sequence;     // 16-bit sequence number
    private volatile long lastTimestamp;      // Last used timestamp

    // Constructor: Initialize worker ID and defaults
    public FlakeIdGenerator() {
        this.workerId = getWorkerId();
        this.sequence = new AtomicInteger(0);
        this.lastTimestamp = -1L;
    }

    // Generate a 128-bit Flake ID
    public synchronized BigInteger generateId() {
        long timestamp = System.currentTimeMillis();

        // Check for clock moving backwards
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        // Handle sequence within the same millisecond
        if (timestamp == lastTimestamp) {
            int seq = sequence.incrementAndGet();
            if (seq >= (1 << 16)) { // Sequence overflow (2^16 = 65536)
                // Wait until the next millisecond
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
                sequence.set(0);
            }
        } else {
            sequence.set(0); // Reset sequence when timestamp advances
        }

        lastTimestamp = timestamp;

        // Assemble the 128-bit ID using BigInteger
        BigInteger timestampPart = BigInteger.valueOf(timestamp).shiftLeft(64); // Shift 64 bits
        BigInteger workerIdPart = BigInteger.valueOf(workerId).shiftLeft(16);   // Shift 16 bits
        BigInteger sequencePart = BigInteger.valueOf(sequence.get());

        return timestampPart.or(workerIdPart).or(sequencePart);
    }

    // Retrieve the worker ID from the MAC address
    private long getWorkerId() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                byte[] mac = ni.getHardwareAddress();
                if (mac != null && mac.length == 6) { // MAC address is 48 bits (6 bytes)
                    long id = 0;
                    for (int i = 0; i < 6; i++) {
                        id = (id << 8) | (mac[i] & 0xFF);
                    }
                    return id;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException("Unable to retrieve MAC address", e);
        }
        throw new RuntimeException("No valid MAC address found");
    }

    // Main method for demonstration
    public static void main(String[] args) {
        FlakeIdGenerator generator = new FlakeIdGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println(generator.generateId());
        }
    }
}