package icu.congee.id.generator.distributed.atomiclong;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class AtomicLongIdGenerator implements IdGenerator {

    private final RAtomicLong atomicLong;


    public AtomicLongIdGenerator(RedissonClient redisson) {
        this.atomicLong = redisson.getAtomicLong("IdGenerator:AtomicLongIdGenerator:current");
    }


    @Override
    public Long generate() {
        return atomicLong.getAndIncrement();
    }

    @Override
    public IdType idType() {
        return IdType.RAtomicLong;
    }
}
