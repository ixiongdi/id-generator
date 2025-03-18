/*
 * MIT License
 *
 * Copyright (c) 2024 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package icu.congee.id.base;

import lombok.Getter;

/**
 * 标识符类型枚举，定义了系统支持的各种ID生成策略。
 * 每种类型都具有特定的生成规则和应用场景，可以根据实际需求选择合适的标识符类型。
 *
 * @since 1.0.0
 */
@Getter
public enum IdType {
    /**
     * Brother ID，一种简单且易于理解的标识符格式。
     * 适用于小型系统或测试环境。
     */
    BroId("bro_id", "brother id"),

    /**
     * 组合全局唯一标识符（COMB GUID）。
     * 在标准UUID基础上增加时间排序字节，提高数据库索引效率。
     * 适用于需要高性能数据库操作的场景。
     */
    COMBGUID("comb_guid", "组合全局唯一标识符，具有时间排序字节以提高数据库效率"),

    /**
     * 标准Cos标识符实现。
     * 提供均衡的功能特性，包括唯一性、排序性和可读性。
     * 适用于一般业务系统。
     */
    CosId("cos_id", "标准Cos标识符实现，具有均衡的功能特性"),

    /**
     * CUID版本1实现。
     * 使用加密元素生成防碰撞的唯一标识符。
     * 适用于需要高安全性的场景。
     */
    CUIDv1("cuid_v1", "具有加密元素的防碰撞唯一标识符"),

    /**
     * CUID版本2实现。
     * 在v1基础上进行了优化，提供更好的性能和安全性。
     * 适用于需要高安全性的场景。
     */
    CUIDv2("cuid_v2", "具有加密元素的防碰撞唯一标识符"),

    /**
     * JavaScript安全的自定义ID。
     * 生成的ID可以安全地在JavaScript环境中使用。
     * 适用于前端应用场景。
     */
    CustomJavaScriptSafetyId("js_safety_id", ""),

    /**
     * 基于时间的业务标识符。
     * 包含时间戳信息，便于业务追踪和排序。
     * 适用于需要时间追踪的业务系统。
     */
    CustomTimeBasedBusinessId(
            "business_id", "自定义基于时间的业务标识符，内嵌时间戳"),

    /**
     * 基于时间的随机标识符。
     * 结合时间信息和随机性，提供高熵值。
     * 适用于需要高随机性的场景。
     */
    CustomTimeBasedRandomId("random_id", "自定义基于时间的随机标识符，具有高熵值"),

    /**
     * Elasticsearch兼容的雪花ID。
     * 支持分片功能，优化Elasticsearch性能。
     * 适用于Elasticsearch环境。
     */
    ElasticFlake("elastic_flake", "兼容Elasticsearch的雪花标识符，支持分片"),

    /**
     * Twitter风格的雪花ID。
     * 包含时间戳、工作机器ID和序列号。
     * 适用于分布式系统。
     */
    Flake("flake", "Twitter风格的雪花标识符，包含时间+工作机器+序列号"),

    /**
     * 扩展的雪花ID。
     * 在标准雪花ID基础上增加元数据位。
     * 适用于需要额外信息的分布式系统。
     */
    FlakeID("flake_id", "扩展的雪花标识符，具有额外的元数据位"),

    /**
     * MyBatis-Flex框架的ID生成器。
     * 与MyBatis-Flex框架集成的标识符生成策略。
     * 适用于MyBatis-Flex项目。
     */
    FlexId("flex_id", "MyBatis-Flex中内置的Id生成器"),

    /**
     * 可排序的唯一标识符。
     * 使用base62编码，保证可读性和排序性。
     * 适用于需要可读性的场景。
     */
    KSUID("ksuid", "可排序的唯一标识符，使用base62编码"),

    /**
     * 支持字典序排序的UUID。
     * 优化的UUID变体，支持字典序排序。
     * 适用于需要排序的场景。
     */
    LexicalUUID("lexical_uuid", "具有字典序排序能力的UUID变体"),

    /**
     * 标准Mist ID实现。
     * 提供均衡的功能特性。
     * 适用于一般业务系统。
     */
    MIST_ID("mist_id", "标准Mist标识符实现，具有均衡的功能特性"),

    /**
     * 高性能Mist ID实现。
     * 优化生成速度的Mist标识符变体。
     * 适用于高并发场景。
     */
    MIST_FAST_ID("mist_fast_id", "高性能Mist标识符，优化生成速度"),

    /**
     * 安全增强的Mist ID实现。
     * 增加熵值的Mist标识符变体。
     * 适用于需要高安全性的场景。
     */
    MIST_SECURE_ID("mist_secure_id", "加密安全的Mist标识符，增强熵值"),

    /**
     * Nano ID实现。
     * 紧凑且URL安全的标识符格式。
     * 适用于URL友好的场景。
     */
    NanoId("nano_id", "紧凑且URL安全的Nano标识符实现"),

    /**
     * MongoDB风格的对象ID。
     * 包含时间戳、机器标识和计数器。
     * 适用于MongoDB环境。
     */
    ObjectID("object_id", "MongoDB风格的对象标识符，包含时间戳+机器+计数"),

    /**
     * 时间排序的UUID。
     * 优化数据库性能的UUID变体。
     * 适用于需要数据库性能优化的场景。
     */
    orderedUuid("ordered_uuid", "时间排序的UUID变体，优化数据库性能"),

    /**
     * Firebase风格的推送ID。
     * 具有时间顺序的标识符格式。
     * 适用于实时数据库场景。
     */
    pushID("push_id", "Firebase风格的推送标识符，具有时间顺序"),

    /**
     * 分片友好的分布式ID。
     * 优化数据库分片的标识符格式。
     * 适用于分片数据库环境。
     */
    ShardingID("sharding_id", "数据库分片友好的分布式标识符格式"),

    /**
     * 会话标识符。
     * 包含过期时间戳的会话ID格式。
     * 适用于会话管理场景。
     */
    SID("sid", "会话标识符格式，内嵌过期时间戳"),

    /**
     * Twitter雪花算法实现。
     * 64位标识符，包含时间戳、工作机器ID和序列号。
     * 适用于分布式系统。
     */
    Snowflake("snowflake", "Twitter雪花标识符，64位时间+工作机器+序列号"),

    /**
     * Sony版本的雪花算法实现。
     * 类似Twitter雪花算法的变体。
     * 适用于分布式系统。
     */
    Sonyflake("sonyflake", "Twitter雪花标识符，64位时间+工作机器+序列号"),

    /**
     * 可排序的通用唯一标识符。
     * 结合了唯一性和可排序性。
     * 适用于需要排序的分布式系统。
     */
    ULID("ulid", "可排序的通用唯一标识符"),

    /**
     * UUID版本1。
     * 基于MAC地址和时间戳生成。
     * 适用于需要硬件相关性的场景。
     */
    UUIDv1("uuid_v1", "基于MAC地址和时间戳的UUID"),

    /**
     * UUID版本2。
     * DCE安全版本，使用POSIX UID/GID。
     * 适用于POSIX系统。
     */
    UUIDv2("uuid_v2", "DCE安全版本，使用POSIX UID/GID"),

    /**
     * UUID版本3（已弃用）。
     * 基于MD5哈希的命名空间UUID。
     * 不推荐使用。
     */
    UUIDv3("uuid_v3", "基于MD5哈希的命名空间UUID（已弃用）"),

    /**
     * UUID版本4。
     * 随机生成的UUID，提供高熵值。
     * 适用于需要高随机性的场景。
     */
    UUIDv4("uuid_v4", "随机生成的UUID，具有122位熵值"),

    /**
     * UUID版本5。
     * 基于SHA-1哈希的命名空间UUID。
     * 适用于需要确定性UUID的场景。
     */
    UUIDv5("uuid_v5", "基于SHA-1哈希的命名空间UUID"),

    /**
     * UUID版本6。
     * 重排序的格里高利时间UUID。
     * 适用于需要时间排序的场景。
     */
    UUIDv6("uuid_v6", "重排序的格里高利时间UUID（RFC草案）"),

    /**
     * UUID版本7。
     * 基于Unix纪元时间戳的时间排序UUID。
     * 适用于需要时间排序的场景。
     */
    UUIDv7("uuid_v7", "基于Unix纪元时间戳的时间排序UUID"),

    /**
     * UUID版本8。
     * 自定义UUID格式，支持供应商特定数据。
     * 适用于需要自定义格式的场景。
     */
    UUIDv8("uuid_v8", "自定义UUID格式，支持供应商特定数据"),

    /**
     * 微信序列号生成器。
     * 适用于微信相关业务场景。
     */
    WxSeq("wx_seq", "微信序列号生成器"),

    /**
     * 全局唯一标识符。
     * 包含4字节前缀用于多系统协调。
     * 适用于多系统集成场景。
     */
    XID("xid", "全局唯一标识符，具有4字节前缀用于多系统协调");

    private final String name;
    private final String desc;

    /**
     * 构造函数
     *
     * @param name 标识符类型的名称
     * @param desc 标识符类型的描述
     */
    IdType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

}
