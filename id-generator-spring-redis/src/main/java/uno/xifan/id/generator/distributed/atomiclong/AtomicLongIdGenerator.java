package uno.xifan.id.generator.distributed.atomiclong;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;
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
