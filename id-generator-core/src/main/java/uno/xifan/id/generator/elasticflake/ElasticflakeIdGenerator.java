package uno.xifan.id.generator.elasticflake;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

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
