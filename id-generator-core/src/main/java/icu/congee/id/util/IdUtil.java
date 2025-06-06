package icu.congee.id.util;

import icu.congee.id.generator.combguid.CombGuidGenerator;
import icu.congee.id.generator.cuid.CUID;
import icu.congee.id.generator.custom.TimeBasedBusinessIdGenerator;
import icu.congee.id.generator.custom.TimeBasedEntropyIdGenerator;
import icu.congee.id.generator.elasticflake.ElasticflakeIdGenerator;
import icu.congee.id.generator.js.JavaScriptSafetyIdGenerator;
import icu.congee.id.generator.ksuid.KsuidGenerator;
import icu.congee.id.generator.lexical.LexicalUUIDGenerator;
import icu.congee.id.generator.nano.NanoIdGenerator;
import icu.congee.id.generator.objectid.ObjectId;
import icu.congee.id.generator.ordereduuid.OrderedUuidGenerator;
import icu.congee.id.generator.pushid.PushIDGenerator;
import icu.congee.id.generator.sid.SIDGenerator;
import icu.congee.id.generator.ulid.ULIDGenerator;
import icu.congee.id.generator.uuid.*;
import icu.congee.id.generator.xid.XidGenerator;

public class IdUtil {

    private static final ULIDGenerator ulidGenerator = new ULIDGenerator();

    public static String combguid() {
        return CombGuidGenerator.next().toString();
    }

    public static String cuid1() {
        return CUID.randomCUID1().toString();
    }

    public static String cuid2() {
        return CUID.randomCUID2().toString();
    }

    public static String elasticflake() {
        return ElasticflakeIdGenerator.next();
    }

    public static long entropy() {
        return TimeBasedEntropyIdGenerator.next();
    }

    public static long javaScriptSafetyId() { return JavaScriptSafetyIdGenerator.next(); }

    public static String ksuid() {
        return KsuidGenerator.next();
    }

    public static String lexicalUuid() {
        return LexicalUUIDGenerator.next();
    }

    public static String nanoId() {
        return NanoIdGenerator.next();
    }

    public static String objectId() {
        return ObjectId.get().toString();
    }

    public static String orderedUuid() {
        return OrderedUuidGenerator.next().toString();
    }

    public static String pushId() {
        return PushIDGenerator.generatePushID();
    }

    public static String sid() {
        return SIDGenerator.next();
    }

    public static long businessId() {
        return TimeBasedBusinessIdGenerator.next();
    }

    public static String ulid() {
        return ulidGenerator.next();
    }

    public static String uuid1() {
        return UUIDv1Generator.next().toString();
    }

    public static String uuid2() {
        return UUIDv2Generator.next().toString();
    }

    public static String uuid4() {
        return UUIDv4Generator.next().toString();
    }

    public static String uuid6() {
        return UUIDv6Generator.next().toString();
    }

    public static String uuid7() {
        return UUIDv7Generator.next().toString();
    }

    public static String xid() {
        return XidGenerator.next();
    }
}
