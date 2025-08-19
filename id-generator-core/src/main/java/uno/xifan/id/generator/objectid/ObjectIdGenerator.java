package uno.xifan.id.generator.objectid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

public class ObjectIdGenerator implements IdGenerator {
    @Override
    public String generate() {
        return ObjectId.get().toString();
    }

    @Override
    public IdType idType() {
        return IdType.ObjectID;
    }
}
