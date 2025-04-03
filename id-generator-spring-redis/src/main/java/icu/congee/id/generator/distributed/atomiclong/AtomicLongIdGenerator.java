package icu.congee.id.generator.distributed.atomiclong;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

@Service
@DependsOn("redissonClient")
public enum AtomicLongIdGenerator implements IdGenerator {
    INSTANCE;

    @Resource
    private RedissonClient redisson;

    private RAtomicLong atomicLong;

    @PostConstruct
    public void init() {
        this.atomicLong = redisson.getAtomicLong("IdGenerator:AtomicLongIdGenerator:current");
    }

    @Override
    public Long generate() {
        return atomicLong.incrementAndGet();
    }

    @Override
    public IdType idType() {
        return IdType.RAtomicLong;
    }
}
