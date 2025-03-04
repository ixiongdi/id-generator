package com.github.ixiongdi.id.generator;

import icu.congee.LexicalUUID.LexicalUUID;
import icu.congee.LexicalUUID.MicrosecondEpochClock;
import icu.congee.cuid.CUID;
import icu.congee.flake.FlakeIdGenerator;
import icu.congee.objectid.ObjectId;
import icu.congee.snowflake.OptimizedSnowflakeIdGenerator;
import icu.congee.ulid.ULID;
import icu.congee.uuid.UUIDv8Generator;

import java.util.Arrays;

public class IdUtil {

    public static final OptimizedSnowflakeIdGenerator snowflakeIdGenerator =
            new OptimizedSnowflakeIdGenerator(0, 0);
    private static final ULID ulid = new ULID();
    private static final FlakeIdGenerator flakeIdGenerator = new FlakeIdGenerator(0);

    public static String nextULID() {
        return ulid.nextULID();
    }

    public static String nextLexicalUUID() {
        return new LexicalUUID(MicrosecondEpochClock.getInstance()).toString();
    }

    public static Long nextSnowflakeId() {
        return snowflakeIdGenerator.nextId();
    }

    public static String nextFlakeId() {
        return Arrays.toString(flakeIdGenerator.nextId());
    }

    public static String nextObjectId() {
        return new ObjectId().toHexString();
    }

    public static String nextCuid1() {
        return CUID.randomCUID1().toString();
    }

    public static String nextCuid2() {
        return CUID.randomCUID2().toString();
    }
}
