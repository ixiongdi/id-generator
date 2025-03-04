package icu.congee;

import icu.congee.uuid.UUIDv7Generator;
import icu.congee.uuid.UUIDv8Generator;

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
