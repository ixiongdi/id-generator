package icu.congee.id.generator.elasticflake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class ElasticflakeIdGenerator implements IdGenerator {

    private static final TimeBasedUUIDGenerator INSTANCE = new TimeBasedUUIDGenerator();

    public static String next() { return INSTANCE.getBase64UUID(); }

    @Override
    public String generate() {
        return INSTANCE.getBase64UUID();
    }

    @Override
    public IdType idType() {
        return IdType.ElasticFlake;
    }
}
