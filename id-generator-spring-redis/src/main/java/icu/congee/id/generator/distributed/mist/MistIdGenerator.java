package icu.congee.id.generator.distributed.mist;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.distributed.segmentid.concurrent.IdSegmentChain;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@Log4j2
public class MistIdGenerator implements IdGenerator {

    private final Random random = new Random();

    private final IdSegmentChain idSegmentChain;

    public MistIdGenerator(RedissonClient redisson) {
        RAtomicLong atomicLong = redisson.getAtomicLong("IdGenerator:MistIdGenerator:NextMaxId");
        if (!atomicLong.isExists()) {
            atomicLong.set(0);
        }
        this.idSegmentChain = new IdSegmentChain(atomicLong);
    }

    @Override
    public MistId generate() {
        return new MistId(idSegmentChain.nextId(), random.nextInt(0, 65535));
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }
}
