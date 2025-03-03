package icu.congee;

import icu.congee.shardingid.IdGenerator;

public class ShardingIdGenerator implements NumberIdGenerator {

    private static final IdGenerator generator = new IdGenerator(0);

    @Override
    public Number generate() {
        return generator.nextId();
    }
}
