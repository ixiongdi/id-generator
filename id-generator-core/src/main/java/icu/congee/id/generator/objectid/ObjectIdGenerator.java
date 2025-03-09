package icu.congee.id.generator.objectid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

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
