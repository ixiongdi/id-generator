package icu.congee.id.generator.shardingid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

public class ShardingIdGenerator implements IdGenerator {

    private static final InstagramIdGenerator idGenerator = new InstagramIdGenerator(0);

    @Override
    public Long generate() {
        return idGenerator.generateId();
    }

    @Override
    public IdType idType() {
        return IdType.ShardingID;
    }
}
