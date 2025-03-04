package com.github.ixiongdi.id.generator;

import java.util.UUID;

public class CassandraLexicalUUIDGenerater {
    private final MonotonicClock clock;
    private final long workerId;

    public CassandraLexicalUUIDGenerater() {
        this.clock = new MonotonicClock();
        this.workerId = WorkerIdGenerator.getWorkerId();
    }

    public String generateId() {
        long timestamp = clock.getTimestamp(); // 最高64位
        long mostSignificantBits = timestamp;
        long leastSignificantBits = workerId; // 最低64位
        return new UUID(mostSignificantBits, leastSignificantBits).toString();
    }
}