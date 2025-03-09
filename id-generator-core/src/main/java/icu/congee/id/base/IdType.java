package icu.congee.id.base;

public enum IdType {
    BroId("bro_id", "brother id"),
    COMBGUID("comb_guid", "组合全局唯一标识符，具有时间排序字节以提高数据库效率"),
    CosId("cos_id", "标准Cos标识符实现，具有均衡的功能特性"),
    CUIDv1("cuid_v1", "具有加密元素的防碰撞唯一标识符"),
    CUIDv2("cuid_v2", "具有加密元素的防碰撞唯一标识符"),
    CustomJavaScriptSafetyId("js_safety_id", ""),
    CustomTimeBasedBusinessId(
            "business_id", "自定义基于时间的业务标识符，内嵌时间戳"),
    CustomTimeBasedRandomId("random_id", "自定义基于时间的随机标识符，具有高熵值"),
    ElasticFlake("elastic_flake", "兼容Elasticsearch的雪花标识符，支持分片"),
    Flake("flake", "Twitter风格的雪花标识符，包含时间+工作机器+序列号"),
    FlakeID("flake_id", "扩展的雪花标识符，具有额外的元数据位"),
    KSUID("ksuid", "可排序的唯一标识符，使用base62编码"),
    LexicalUUID("lexical_uuid", "具有字典序排序能力的UUID变体"),
    MIST_ID("mist_id", "标准Mist标识符实现，具有均衡的功能特性"),
    MIST_FAST_ID("mist_fast_id", "高性能Mist标识符，优化生成速度"),
    MIST_SECURE_ID("mist_secure_id", "加密安全的Mist标识符，增强熵值"),
    NanoId("nano_id", "紧凑且URL安全的Nano标识符实现"),
    ObjectID("object_id", "MongoDB风格的对象标识符，包含时间戳+机器+计数"),
    orderedUuid("ordered_uuid", "时间排序的UUID变体，优化数据库性能"),
    pushID("push_id", "Firebase风格的推送标识符，具有时间顺序"),
    ShardingID("sharding_id", "数据库分片友好的分布式标识符格式"),
    SID("sid", "会话标识符格式，内嵌过期时间戳"),
    Snowflake("snowflake", "Twitter雪花标识符，64位时间+工作机器+序列号"),
    Sonyflake("sonyflake", "Twitter雪花标识符，64位时间+工作机器+序列号"),
    ULID("ulid", "可排序的通用唯一标识符"),
    UUIDv1("uuid_v1", "基于MAC地址和时间戳的UUID"),
    UUIDv2("uuid_v2", "DCE安全版本，使用POSIX UID/GID"),
    UUIDv3("uuid_v3", "基于MD5哈希的命名空间UUID（已弃用）"),
    UUIDv4("uuid_v4", "随机生成的UUID，具有122位熵值"),
    UUIDv5("uuid_v5", "基于SHA-1哈希的命名空间UUID"),
    UUIDv6("uuid_v6", "重排序的格里高利时间UUID（RFC草案）"),
    UUIDv7("uuid_v7", "基于Unix纪元时间戳的时间排序UUID"),
    UUIDv8("uuid_v8", "自定义UUID格式，支持供应商特定数据"),
    XID("xid", "全局唯一标识符，具有4字节前缀用于多系统协调");

    private final String name;
    private final String desc;

    IdType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }
}
