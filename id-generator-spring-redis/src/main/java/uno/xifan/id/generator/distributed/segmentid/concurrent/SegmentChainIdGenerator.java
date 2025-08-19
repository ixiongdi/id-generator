package uno.xifan.id.generator.distributed.segmentid.concurrent;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SegmentChainIdGenerator implements IdGenerator {

    private final IdSegmentChain idSegmentChain;

    public SegmentChainIdGenerator(RedissonClient redissonClient) {
        RAtomicLong atomicLong = redissonClient.getAtomicLong("SegmentChainIdGenerator:NextMaxId");
        this.idSegmentChain = new IdSegmentChain(atomicLong);
    }

    @Override
    public Long generate() {
        return idSegmentChain.nextId();
    }

    @Override
    public IdType idType() {
        return IdType.SegmentChainId;
    }
}
