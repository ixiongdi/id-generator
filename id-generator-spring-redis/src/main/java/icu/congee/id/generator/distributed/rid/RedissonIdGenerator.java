package icu.congee.id.generator.distributed.rid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import jakarta.annotation.PostConstruct;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RedissonIdGenerator implements IdGenerator {

    private final RIdGenerator generator;
    private final String name;
    private final long value;
    private final long allocationSize;

    public RedissonIdGenerator(RedissonClient redisson,
            @Value("${id.generator.rid.name:IdGenerator:RedissonIdGenerator:current}") String name,
            @Value("${id.generator.rid.value:0}") long value,
            @Value("${id.generator.rid.allocationSize:5000}") long allocationSize) {
        this.name = name;
        this.value = value;
        this.allocationSize = allocationSize;
        this.generator = redisson.getIdGenerator(this.name);
        this.generator.tryInit(this.value, this.allocationSize);
    }

    @PostConstruct
    public void init() {

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
