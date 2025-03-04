package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.generator.uuid.UUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.UUIDv8Generator;

import java.util.UUID;

public class BestPracticeStringIdGenerator implements StringIdGenerator {

    public static UUID unixTimeBasedUUID() {
        return UUIDv7Generator.next();
    }

    public static UUID customUUID() {
        return UUIDv8Generator.next();
    }

    @Override
    public String generate() {
        return customUUID().toString();
    }
}
