package com.github.ixiongdi.id.generator.lexical;

/**
 * 时钟接口，用于生成时间戳
 */
public interface Clock {
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    long timestamp();

    /**
     * 获取Clock实例
     * 这个方法主要是为了方便Java代码获取单例实例
     * @return Clock实例
     */
    default Clock get() {
        return this;
    }
}