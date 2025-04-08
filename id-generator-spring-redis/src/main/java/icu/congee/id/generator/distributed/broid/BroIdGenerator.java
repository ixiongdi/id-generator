package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import lombok.AllArgsConstructor;
import lombok.Data;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public enum BroIdGenerator implements IdGenerator {
    INSTANCE;

    @Resource private RedissonClient redisson;

    private RAtomicLong threadId;

    private ThreadLocal<BroIdThreadLocalHolder> threadLocal;

    @PostConstruct
    public void init() {
        this.threadId = redisson.getAtomicLong("IdGenerator:BroIdGenerator:threadId");
        this.threadLocal =
                ThreadLocal.withInitial(
                        () -> new BroIdThreadLocalHolder(this.threadId.getAndIncrement(), 0));
    }

    @Override
    public Long generate() {
        BroIdThreadLocalHolder broIdThreadLocalHolder = this.threadLocal.get();
        return broIdThreadLocalHolder.threadId << 48 | broIdThreadLocalHolder.sequence++;
    }

    @Override
    public IdType idType() {
        return IdType.BroId;
    }

    @Data
    @AllArgsConstructor
    public static class BroIdThreadLocalHolder {
        long threadId;
        long sequence;
    }
}
