package icu.congee;

public class SnowflakeIdGenerator implements NumberIdGenerator {

    private static final icu.congee.snowflake.SnowflakeIdGenerator generator =
            new icu.congee.snowflake.SnowflakeIdGenerator(0, 0);

    @Override
    public Number generate() {
        return generator.nextId();
    }
}
