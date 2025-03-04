package com.github.ixiongdi.id.generator.shardingid;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    // Custom epoch (January 1, 2011, in milliseconds)
    private static final long CUSTOM_EPOCH = 1314220021721L;

    // Masks for different parts of the ID
    private static final long TIME_MASK = ~0L << (64 - 41);
    private static final long SHARD_ID_MASK = ~0L << (64 - 41 - 13);
    private static final long SEQUENCE_MASK = ~0L << (64 - 41 - 13 - 10);

    // Maximum values for shard ID and sequence
    private static final long MAX_SHARD_ID = (1L << 13) - 1;
    private static final long MAX_SEQUENCE = (1L << 10) - 1;

    // Shard ID and sequence for the generator
    private final long shardId;
    private final AtomicLong sequence;

    public IdGenerator(long shardId) {
        this.shardId = shardId;
        this.sequence = new AtomicLong(0);
    }

    public synchronized long nextId() {
        long currentTime = Instant.now().toEpochMilli();
        long timeSinceEpoch = currentTime - CUSTOM_EPOCH;

        // Ensure the sequence is reset if the time has moved backward
        if (timeSinceEpoch < (timeSinceEpoch >> 41)) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
        }

        long sequenceValue = sequence.getAndIncrement();
        if (sequenceValue > MAX_SEQUENCE) {
            sequenceValue = 0;
            sequence.set(sequenceValue);
        }

        // Combine the time, shard ID, and sequence into the final ID
        long id = (timeSinceEpoch << 23) | (shardId << 10) | sequenceValue;
        return id;
    }

    public static void main(String[] args) {
        // Example usage
        IdGenerator idGenerator = new IdGenerator(1341); // Shard ID
        for (int i = 0; i < 10; i++) {
            long id = idGenerator.nextId();
            System.out.println("Generated ID: " + id);
        }
    }
}