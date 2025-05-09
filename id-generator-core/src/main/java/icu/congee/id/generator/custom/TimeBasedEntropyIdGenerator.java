package icu.congee.id.generator.custom;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;


public class TimeBasedEntropyIdGenerator implements IdGenerator {


    private static final int epoch = 1746028800;

    public static long next() {
        return (System.currentTimeMillis() / 1000 - epoch) << 32 | Math.abs(new EntropyKey().hashCode());
    }

    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.TimeBasedEntropyId;
    }
}