package com.github.ixiongdi.id.generator.LexicalUUID;

/**
 * 时间戳生成器接口（Java 版本）
 */
public interface Clock {
    /**
     * 获取当前时间戳（毫秒级或更高精度）
     */
    long timestamp();

    /**
     * 获取时钟实例（适配 Java 的单例获取模式）
     */
    default Clock get() {
        return this;
    }
}