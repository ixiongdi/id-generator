package icu.congee.id.generator.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * ID生成器配置属性
 */
@ConfigurationProperties(prefix = "id.generator")
public class IdGeneratorProperties {
    /**
     * Redis key前缀
     */
    private String keyPrefix = "id:generator";

    /**
     * ID生成器类型
     */
    private IdType idType = IdType.SNOWFLAKE;

    public enum IdType {
        /**
         * 雪花算法
         */
        SNOWFLAKE,
        /**
         * UUID
         */
        UUID
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public IdType getIdType() {
        return idType;
    }

    public void setIdType(IdType idType) {
        this.idType = idType;
    }
}