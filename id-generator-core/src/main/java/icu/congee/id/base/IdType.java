/*
 * MIT License
 *
 * Copyright (c) 2025 ixiongdi
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
    BroId("bro_id", "自定义的二进制ID实现，适用于需要特定二进制结构或自定义长度的场景"),

    /**
     * 组合全局唯一标识符（COMB GUID）。
     * 在标准UUID基础上增加时间排序字节，提高数据库索引效率。
     * 适用于需要高性能数据库操作的场景。
     */
    COMBGUID("comb_guid", "组合型GUID，结合了时间有序性和UUID的唯一性，提高数据库索引性能"),

    /**
     * 标准Cos标识符实现。
     * 提供均衡的功能特性，包括唯一性、排序性和可读性。
     * 适用于一般业务系统。
     */
    CosId("cos_id", "CosId标准实现，一种灵活的、可配置的分布式ID生成器"),

    /**
     * CUID版本1实现。
     * 使用加密元素生成防碰撞的唯一标识符。
     * 适用于需要高安全性的场景。
     */
    CUIDv1("cuid_v1", "Collision-resistant Unique ID v1，抗碰撞的唯一ID，适用于需要高度唯一性的场景"),

    /**
     * CUID版本2实现。
     * 在v1基础上进行了优化，提供更好的性能和安全性。
     * 适用于需要高安全性的场景。
     */
    CUIDv2("cuid_v2", "Collision-resistant Unique ID v2，CUID的改进版本，提供更好的性能和安全性"),
    /**
     * Brother ID，一种简单且易于理解的标识符格式。
     * 适用于小型系统或测试环境。
     */
    DtsId("dts_id", "分布式时间服务ID，通常用于需要精确时间同步的分布式系统"),

    /**
     * Elasticsearch兼容的雪花ID。
     * 支持分片功能，优化Elasticsearch性能。
     * 适用于Elasticsearch环境。
     */
    ElasticFlake("elastic_flake", "针对Elasticsearch优化的雪花ID，通常用于ES文档ID以提高分片性能"),

    /**
     * Twitter风格的雪花ID。
     * 包含时间戳、工作机器ID和序列号。
     * 适用于分布式系统。
     */
    Flake("flake", "Twitter Snowflake算法的变种，生成基于时间、机器ID和序列号的分布式ID"),

    /**
     * 扩展的雪花ID。
     * 在标准雪花ID基础上增加元数据位。
     * 适用于需要额外信息的分布式系统。
     */
    FlakeID("flake_id", "扩展的雪花ID，可能包含额外的自定义数据位，增强了标准雪花算法的灵活性"),

    /**
     * MyBatis-Flex框架的ID生成器。
     * 与MyBatis-Flex框架集成的标识符生成策略。
     * 适用于MyBatis-Flex项目。
     */
    FlexId("flex_id", "MyBatis-Flex框架内置的ID生成器，与框架深度集成"),

    /**
     * JavaScript安全的自定义ID。
     * 生成的ID可以安全地在JavaScript环境中使用。
     * 适用于前端应用场景。
     */
    JavaScriptSafetyId("js_safety_id", "JavaScript安全ID，确保生成的ID在JavaScript环境中不会超出Number类型的安全整数范围"),

    /**
     * 可排序的唯一标识符。
     * 使用base62编码，保证可读性和排序性。
     * 适用于需要可读性的场景。
     */
    KSUID("ksuid", "K-Sortable Globally Unique ID，一种时间可排序的全局唯一ID，使用Base62编码以提高可读性"),

    /**
     * 支持字典序排序的UUID。
     * 优化的UUID变体，支持字典序排序。
     * 适用于需要排序的场景。
     */
    LexicalUUID("lexical_uuid", "按字典序可排序的UUID，适用于需要UUID按生成顺序排序的场景"),

    /**
     * 标准Mist ID实现。
     * 提供均衡的功能特性。
     * 适用于一般业务系统。
     */
    MIST_ID("mist_id", "Mist ID标准实现，一种通用的分布式ID生成方案"),

    /**
     * Nano ID实现。
     * 紧凑且URL安全的标识符格式。
     * 适用于URL友好的场景。
     */
    NanoId("nano_id", "一种紧凑、URL安全的唯一字符串ID生成器"),

    /**
     * MongoDB风格的对象ID。
     * 包含时间戳、机器标识和计数器。
     * 适用于MongoDB环境。
     */
    ObjectID("object_id", "MongoDB风格的ObjectID，包含时间戳、机器标识、进程ID和计数器"),

    /**
     * 时间排序的UUID。
     * 优化数据库性能的UUID变体。
     * 适用于需要数据库性能优化的场景。
     */
    OrderedUuid("ordered_uuid", "时间有序的UUID，将时间戳信息编码到UUID中，改善数据库索引性能"),

    /**
     * Firebase风格的推送ID。
     * 具有时间顺序的标识符格式。
     * 适用于实时数据库场景。
     */
    PushID("push_id", "Firebase风格的Push ID，按时间顺序生成，适用于实时数据同步场景"),

    RAtomicLong("atomic_id", "基于Redisson的AtomicLong实现的ID生成器，适用于分布式环境下的原子递增ID"),

    RID("rid", "Redisson提供的通用ID生成器"),

    /**
     * 会话标识符。
     * 包含过期时间戳的会话ID格式。
     * 适用于会话管理场景。
     */
    SID("sid", "Session ID，通常用于Web会话管理，可能内嵌过期时间等信息"),

    /**
     * 分片友好的分布式ID。
     * 优化数据库分片的标识符格式。
     * 适用于分片数据库环境。
     */
    ShardingID("sharding_id", "对数据库分片友好的ID，设计时考虑了数据在分片环境下的均匀分布和查询效率"),

    /**
     * Twitter雪花算法实现。
     * 64位标识符，包含时间戳、工作机器ID和序列号。
     * 适用于分布式系统。
     */
    Snowflake("snowflake", "Twitter Snowflake算法标准实现，生成64位、时间有序、全局唯一的ID"),

    /**
     * Sony版本的雪花算法实现。
     * 类似Twitter雪花算法的变体。
     * 适用于分布式系统。
     */
    Sonyflake("sonyflake", "Sonyflake算法实现，Snowflake的一种变体，调整了各部分占用的位数"),

    /**
     * 基于时间的业务标识符。
     * 包含时间戳信息，便于业务追踪和排序。
     * 适用于需要时间追踪的业务系统。
     */
    TimeBasedBusinessId(
            "business_id", "基于时间的业务ID，通常结合业务类型和时间信息生成，便于业务追踪和分析"),

    /**
     * 基于时间的随机标识符。
     * 结合时间信息和随机性，提供高熵值。
     * 适用于需要高随机性的场景。
     */
    TimeBasedEntropyId("entropy_id", "基于时间和多个熵值的ID，结合了时间有序性和随机性，提供较高的唯一性保证"),
    /**
     * Brother ID，一种简单且易于理解的标识符格式。
     * 适用于小型系统或测试环境。
     */
    TtsId("tts_id", "时间戳序列ID，通常基于高精度时间戳和序列号生成"),

    /**
     * 可排序的通用唯一标识符。
     * 结合了唯一性和可排序性。
     * 适用于需要排序的分布式系统。
     */
    ULID("ulid", "Universally Unique Lexicographically Sortable Identifier，一种按字典序可排序的全局唯一ID"),

    /**
     * UUID版本1。
     * 基于MAC地址和时间戳生成。
     * 适用于需要硬件相关性的场景。
     */
    UUIDv1("uuid_v1", "UUID版本1，基于时间戳和MAC地址生成，保证全局唯一性"),

    /**
     * UUID版本2。
     * DCE安全版本，使用POSIX UID/GID。
     * 适用于POSIX系统。
     */
    UUIDv2("uuid_v2", "UUID版本2，DCE安全版本，基于时间戳、时钟序列和POSIX UID/GID"),

    /**
     * UUID版本3（已弃用）。
     * 基于MD5哈希的命名空间UUID。
     * 不推荐使用。
     */
    UUIDv3("uuid_v3", "UUID版本3，基于命名空间和名称的MD5哈希生成，已不推荐使用"),

    /**
     * UUID版本4。
     * 随机生成的UUID，提供高熵值。
     * 适用于需要高随机性的场景。
     */
    UUIDv4("uuid_v4", "UUID版本4，基于随机数生成，提供约122位的随机性"),

    /**
     * UUID版本5。
     * 基于SHA-1哈希的命名空间UUID。
     * 适用于需要确定性UUID的场景。
     */
    UUIDv5("uuid_v5", "UUID版本5，基于命名空间和名称的SHA-1哈希生成"),

    /**
     * UUID版本6。
     * 重排序的格里高利时间UUID。
     * 适用于需要时间排序的场景。
     */
    UUIDv6("uuid_v6", "UUID版本6，按时间排序的UUID，将时间戳信息重新排列以提高数据库索引效率 (RFC草案)"),

    /**
     * UUID版本7。
     * 基于Unix纪元时间戳的时间排序UUID。
     * 适用于需要时间排序的场景。
     */
    UUIDv7("uuid_v7", "UUID版本7，基于Unix纪元时间戳的UUID，设计为时间有序且包含随机性 (RFC草案)"),

    /**
     * UUID版本8。
     * 自定义UUID格式，支持供应商特定数据。
     * 适用于需要自定义格式的场景。
     */
    UUIDv8("uuid_v8", "UUID版本8，为自定义或供应商特定用途保留的UUID格式 (RFC草案)"),

    /**
     * 微信序列号生成器。
     * 适用于微信相关业务场景。
     */
    WxSeq("wx_seq", "微信序列号生成器，模拟微信的序列号生成机制"),

    /**
     * 全局唯一标识符。
     * 包含4字节前缀用于多系统协调。
     * 适用于多系统集成场景。
     */
    XID("xid", "全局唯一ID，一种紧凑、高效、可排序的分布式ID生成算法");

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
