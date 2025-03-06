package com.github.ixiongdi.id.generator;

import com.github.ixiongdi.id.generator.custom.TimeBasedBusinessIdGenerator;
import com.github.ixiongdi.id.generator.custom.TimeBasedRandomIdGenerator;
import com.github.ixiongdi.id.generator.lexical.LexicalUUID;
import com.github.ixiongdi.id.generator.lexical.MicrosecondEpochClock;
import com.github.ixiongdi.id.generator.mist.MistGenerator;
import com.github.ixiongdi.id.generator.ulid.ULIDGenerator;
import com.github.ixiongdi.id.generator.uuid.DedicatedCounterUUIDv7Generator;
import com.github.ixiongdi.id.generator.uuid.FastUUIDToString;
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
        return ULIDGenerator.generate();
    }

    public static String lexicalUUID() {
        return LexicalUUID.generate(MicrosecondEpochClock.getInstance()).toString();
    }
    
    /**
     * 使用高效的toString方法将UUID转换为字符串
     *
     * @param uuid 要转换的UUID
     * @return UUID的字符串表示
     */
    public static String fastToString(UUID uuid) {
        return FastUUIDToString.toString(uuid);
    }
    
    /**
     * 生成一个UUID v7并使用高效的toString方法转换为字符串
     *
     * @return UUID v7的字符串表示
     */
    public static String fastUUIDv7String() {
        return FastUUIDToString.toString(unixTimeBasedUUID());
    }
    
    /**
     * 生成一个薄雾算法ID
     *
     * @return 薄雾算法生成的唯一ID
     */
    public static long mistId() {
        return MistGenerator.next();
    }
}
