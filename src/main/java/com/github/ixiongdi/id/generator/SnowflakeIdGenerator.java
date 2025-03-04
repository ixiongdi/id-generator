package com.github.ixiongdi.id.generator;

public class SnowflakeIdGenerator implements NumberIdGenerator {

    private static final icu.congee.snowflake.OptimizedSnowflakeIdGenerator generator =
            new icu.congee.snowflake.OptimizedSnowflakeIdGenerator(0, 0);

    @Override
    public Number generate() {
        return generator.nextId();
    }
}
