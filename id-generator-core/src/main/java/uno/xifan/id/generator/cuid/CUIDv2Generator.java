package uno.xifan.id.generator.cuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

public class CUIDv2Generator implements IdGenerator {
    @Override
    public String generate() {
        return CUID.randomCUID2().toString();
    }

    @Override
    public IdType idType() {
        return IdType.CUIDv2;
    }
}
