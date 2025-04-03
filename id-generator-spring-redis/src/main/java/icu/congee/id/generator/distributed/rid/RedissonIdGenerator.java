package icu.congee.id.generator.distributed.rid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("redissonClient")
public enum RedissonIdGenerator implements IdGenerator {
    INSTANCE;

    @Resource private RedissonClient redisson;

    @Value("${id.generator.rid.name:rid}")
    private String name;

    @Value("${id.generator.rid.value:0}")
    private long value;

    @Value("${id.generator.rid.allocationSize:5000}")
    private long allocationSize;

    private RIdGenerator generator;

    @PostConstruct
    public void init() {
        generator = redisson.getIdGenerator(name);
        generator.tryInit(value, allocationSize);
    }

    @Override
    public Long generate() {
        return generator.nextId();
    }

    @Override
    public IdType idType() {
        return IdType.RID;
    }
}
