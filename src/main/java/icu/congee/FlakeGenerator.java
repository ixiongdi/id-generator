package icu.congee;

public class FlakeGenerator implements StringIdGenerator {

    private static final SnowflakeIdGenerator generator = new SnowflakeIdGenerator();

    @Override
    public String generate() {
        return generator.generate().toString();
    }
}
