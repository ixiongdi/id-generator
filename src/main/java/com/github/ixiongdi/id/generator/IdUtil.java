package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.generator.custom.TimeBasedAndBusinessIdGenerator;
import com.github.ixiongdi.id.generator.custom.TimeBasedAndRandomIdGenerator;
import com.github.ixiongdi.id.generator.uuid.UUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.UUIDv8Generator;

import java.util.UUID;

public class IdUtil {

    public static Long businessId() {
        return TimeBasedAndBusinessIdGenerator.next();
    }

    public static Long randomId() {
        return TimeBasedAndRandomIdGenerator.next();
    }

    public static UUID v7UUID() {
        return UUIDv7Generator.next();
    }

    public static UUID v8UUID() {
        return UUIDv8Generator.next();
    }
}
