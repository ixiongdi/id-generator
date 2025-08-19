package uno.xifan.id.generator.custom;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;


public class TimeBasedEntropyIdGenerator implements IdGenerator {

    // 北京时间2025年5月1日凌晨
    private static final int epoch = 1746028800;

    public static long next() {
        return (System.currentTimeMillis() / 1000 - epoch) << 32 | (new EntropyKey().hashCode() & 0x7FFFFFFFL);
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