package icu.congee.id.generator.distributed.dtsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import org.redisson.api.RIdGenerator;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;


@Component
public class DtsIdGenerator implements IdGenerator {

    private final RIdGenerator rIdGenerator;

    public DtsIdGenerator(RedissonClient redisson) {
        rIdGenerator = redisson.getIdGenerator("IdGenerator:DtsIdGenerator:current");
        rIdGenerator.tryInit(0, 1000);
    }


    @Override
    public DtsId generate() {
        return new DtsId(System.currentTimeMillis() / 1000, rIdGenerator.nextId());
    }

    @Override
    public IdType idType() {
        return IdType.DtsId;
    }
}
