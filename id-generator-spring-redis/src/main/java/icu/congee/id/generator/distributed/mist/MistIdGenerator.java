package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@Component
@DependsOn("redissonClient")
public enum MistIdGenerator implements IdGenerator {
    INSTANCE;

    @Resource private RedissonClient redisson;

    @Value("${id.generator.mist.name:IdGenerator:AtomicLongIdGenerator:current}")
    private String name;

    @Value("${id.generator.mist.value:-1}")
    private Long value;

    @Value("${id.generator.mist.secret:false}")
    private Boolean secret;

    private RAtomicLong atomicLong;

    private Random random;

    @PostConstruct
    public void init() {
        atomicLong = redisson.getAtomicLong(name);
        if (value >= 0) {
            atomicLong.set(value);
        }
        if (secret) {
            random = new SecureRandom();
        } else {
            random = ThreadLocalRandom.current();
        }
    }

    @Override
    public MistId generate() {
        return new MistId(atomicLong.getAndIncrement(), random.nextInt(256), random.nextInt(256));
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }
}
