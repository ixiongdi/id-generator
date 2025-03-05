package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.generator.custom.TimeBasedBusinessIdGenerator;
import com.github.ixiongdi.id.generator.custom.TimeBasedRandomIdGenerator;
import com.github.ixiongdi.id.generator.ulid.ULIDGenerator;
import com.github.ixiongdi.id.generator.uuid.DedicatedCounterUUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.IncreasedClockPrecisionUUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.UUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.UUIDv8Generator;

import java.util.UUID;

public class IdUtil {

    public static Long businessId() {
        return TimeBasedBusinessIdGenerator.next();
    }

    public static Long randomId() {
        return TimeBasedRandomIdGenerator.next();
    }

    public static UUID unixTimeBasedUUID() {
        return UUIDv7Generator.next();
    }

    public static UUID unixTimeBasedUUID1() {
        return DedicatedCounterUUIDv7Generator.next();
    }

    public static UUID unixTimeBasedUUID2() {
        return IncreasedClockPrecisionUUIDv7Generator.next();
    }

    public static UUID customUUID() {
        return UUIDv8Generator.next();
    }
    public static String ulid() {
        return ULIDGenerator.next();
    }
}
