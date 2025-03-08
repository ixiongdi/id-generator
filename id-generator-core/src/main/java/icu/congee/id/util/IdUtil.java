package icu.congee.id.util;

import icu.congee.id.generator.cosid.CosIdGenerator;
import icu.congee.id.generator.custom.TimeBasedBusinessIdGenerator;
import icu.congee.id.generator.custom.TimeBasedRandomIdGenerator;
import icu.congee.id.generator.lexical.LexicalUUIDGenerator;
import icu.congee.id.generator.lexical.MicrosecondEpochClock;
import icu.congee.id.generator.ulid.ULIDGenerator;
import icu.congee.id.generator.uuid.DedicatedCounterUUIDv7Generator;
import icu.congee.id.generator.uuid.FastUUIDToString;
import icu.congee.id.generator.uuid.IncreasedClockPrecisionUUIDv7Generator;
import icu.congee.id.generator.uuid.UUIDv7Generator;
import icu.congee.id.generator.uuid.UUIDv8Generator;

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
        return "";
    }

    public static String cosid() {
        return CosIdGenerator.next();
    }

    public static String lexicalUUID() {
        return LexicalUUIDGenerator.generate(MicrosecondEpochClock.getInstance()).toString();
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

}
