package uno.xifan.id.generator.ksuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

public class KsuidGenerator implements IdGenerator {

    public static String next() {
        return KsuidCreator.getKsuid().toString();
    }

    @Override
    public String generate() {
        return KsuidCreator.getKsuid().toString();
    }

    @Override
    public IdType idType() {
        return IdType.KSUID;
    }
}
