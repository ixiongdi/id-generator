package uno.xifan.id.util;

import uno.xifan.id.generator.combguid.CombGuidGenerator;
import uno.xifan.id.generator.cuid.CUID;
import uno.xifan.id.generator.custom.TimeBasedBusinessIdGenerator;
import uno.xifan.id.generator.custom.TimeBasedEntropyIdGenerator;
import uno.xifan.id.generator.elasticflake.ElasticflakeIdGenerator;
import uno.xifan.id.generator.js.JavaScriptSafetyIdGenerator;
import uno.xifan.id.generator.ksuid.KsuidGenerator;
import uno.xifan.id.generator.lexical.LexicalUUIDGenerator;
import uno.xifan.id.generator.nano.NanoIdGenerator;
import uno.xifan.id.generator.objectid.ObjectId;
import uno.xifan.id.generator.ordereduuid.OrderedUuidGenerator;
import uno.xifan.id.generator.pushid.PushIDGenerator;
import uno.xifan.id.generator.sid.SIDGenerator;
import uno.xifan.id.generator.ulid.ULIDGenerator;
import uno.xifan.id.generator.uuid.*;
import uno.xifan.id.generator.xid.XidGenerator;

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
