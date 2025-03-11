package icu.congee.id.generator.broid.impl;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.broid.BroIdGenerator;
import icu.congee.id.generator.broid.BroIdLayout;
import icu.congee.id.generator.broid.BroIdPart;
import icu.congee.id.generator.broid.part.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class UUIDv8BroIdGenerator extends BroIdGenerator<UUIDv8BroId> implements IdGenerator {

    private static final AtomicLong counter = new AtomicLong();
    private static final long EPOCH = 1645557742L;
    private static final Random random = ThreadLocalRandom.current();
    private static final UUIDv8BroIdGenerator instance = getInstance();

    public UUIDv8BroIdGenerator() {
        super(instance.getLayout(), instance.getConstructor());
    }

    /**
     * 构造函数
     *
     * @param layout BroId结构
     * @param constructor T类型的构造器引用
     */
    public UUIDv8BroIdGenerator(
            BroIdLayout layout, Function<List<Boolean>, UUIDv8BroId> constructor) {
        super(layout, constructor);
    }

    public static UUIDv8BroIdGenerator getInstance() {
        List<BroIdPart> parts = new ArrayList<>();
        // 1. 精确到毫秒的时间戳
        parts.add(new TimestampBroIdPart(TimeUnit.MILLISECONDS, EPOCH, 48));
        // 2. 版本
        parts.add(new VersionBroIdPart(8));
        // 3. 全局计数器
        parts.add(new CounterBroIdPart(counter, 12));
        // 4. 变体
        parts.add(new VariantBroIdPart());
        // 5. 空间字段，使用随机数初始化
        parts.add(new SpatiallyBroIdPart(random.nextLong(), 14));
        // 6. 随机数
        parts.add(new RandomBroIdPart(random, 48));
        BroIdLayout layout = new BroIdLayout(parts);
        return new UUIDv8BroIdGenerator(layout, UUIDv8BroId::new);
    }

    @Override
    public UUID generate() {
        return next().toUUID();
    }

    @Override
    public IdType idType() {
        return IdType.BroId;
    }
}
