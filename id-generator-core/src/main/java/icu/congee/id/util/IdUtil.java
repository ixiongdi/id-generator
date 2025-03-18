package icu.congee.id.util;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.broid.impl.UUIDv8BroIdGenerator;
import icu.congee.id.generator.cosid.CosIdGenerator;
import icu.congee.id.generator.custom.TimeBasedBusinessIdGenerator;
import icu.congee.id.generator.custom.TimeBasedRandomIdGenerator;
import icu.congee.id.generator.lexical.LexicalUUIDGenerator;
import icu.congee.id.generator.uuid.DedicatedCounterUUIDv7Generator;
import icu.congee.id.generator.uuid.FastUUIDToString;
import icu.congee.id.generator.uuid.IncreasedClockPrecisionUUIDv7Generator;
import icu.congee.id.generator.uuid.UUIDv7Generator;
import icu.congee.id.generator.uuid.UUIDv8Generator;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.UUID;

/**
 * ID生成工具类
 * <p>
 * 该工具类提供了多种ID生成策略的静态方法，包括：
 * - 基于时间的业务ID和随机ID
 * - 多种版本的UUID（v7、v8等）
 * - 分布式ID（如CosId）
 * - 其他特殊用途的ID
 * </p>
 * 
 * @author ixiongdi
 * @since 1.0
 */
public class IdUtil {

    private static final UUIDv8BroIdGenerator broIdGenerator = UUIDv8BroIdGenerator.getInstance();

    /**
     * 生成基于时间的业务ID
     *
     * @return 生成的业务ID
     */
    public static Long businessId() {
        return TimeBasedBusinessIdGenerator.next();
    }

    /**
     * 生成基于时间的随机ID
     *
     * @return 生成的随机ID
     */
    public static Long randomId() {
        return TimeBasedRandomIdGenerator.next();
    }

    /**
     * 生成基于Unix时间戳的UUID v7
     *
     * @return 生成的UUID v7
     */
    public static UUID unixTimeBasedUUID() {
        return UUIDv7Generator.next();
    }

    /**
     * 生成使用专用计数器的UUID v7
     *
     * @return 生成的UUID v7
     */
    public static UUID unixTimeBasedUUID1() {
        return DedicatedCounterUUIDv7Generator.next();
    }

    /**
     * 生成具有增强时钟精度的UUID v7
     *
     * @return 生成的UUID v7
     */
    public static UUID unixTimeBasedUUID2() {
        return IncreasedClockPrecisionUUIDv7Generator.next();
    }

    /**
     * 生成自定义UUID v8
     *
     * @return 生成的UUID v8
     */
    public static UUID customUUID() {
        return UUIDv8Generator.next();
    }

    /**
     * 生成BroId并转换为UUID
     *
     * @return 生成的UUID格式的BroId
     */
    public static UUID broId() {
        return broIdGenerator.next().toUUID();
    }

    /**
     * 生成ULID（Universally Unique Lexicographically Sortable Identifier）
     *
     * @return 生成的ULID字符串
     */
    public static String ulid() {
        return "";
    }

    /**
     * 生成CosId（Coordinated Snowflake ID）
     *
     * @return 生成的CosId字符串
     */
    public static String cosid() {
        return CosIdGenerator.next();
    }

    /**
     * 生成字典序UUID
     *
     * @return 生成的字典序UUID字符串
     */
    public static String lexicalUUID() {
        return LexicalUUIDGenerator.next();
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
     * 获取所有可用的ID生成器映射
     * <p>
     * 使用Java的ServiceLoader机制加载所有实现了IdGenerator接口的生成器，
     * 并按照其ID类型进行映射。
     * </p>
     *
     * @return ID类型到生成器的映射
     */
    public static Map<IdType, IdGenerator> getIdGeneratorMap() {
        ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);
        Map<IdType, IdGenerator> map = new HashMap<>();
        for (IdGenerator generator : loader) {
            map.put(generator.idType(), generator);
        }
        return map;
    }

}
