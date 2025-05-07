package icu.congee.id.generator.distributed.wxseq;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import org.redisson.api.*;
import org.redisson.api.options.LocalCachedMapOptions;
import org.springframework.stereotype.Component;

@Component
public class WxSeqGenerator implements IdGenerator {

    private final RedissonClient redisson;
    private final RLocalCachedMap<Long, RIdGenerator> generatorCache;


    public WxSeqGenerator(RedissonClient redisson) {
        this.redisson = redisson;
        this.generatorCache = redisson.getLocalCachedMap(LocalCachedMapOptions.name("IdGenerator:WxSeqGenerator:generatorCache"));
    }


    @Override
    public WxSeq generate() {
        throw new IllegalStateException("User ID required");
    }

    public WxSeq generate(Long userId) {
        RIdGenerator idGenerator = generatorCache.computeIfAbsent(userId, k -> {
            RIdGenerator gen = redisson.getIdGenerator("IdGenerator:WxSeqGenerator:userId:" + k);
            gen.tryInit(0, 10000);
            return gen;
        });
        return new WxSeq(userId, idGenerator.nextId());
    }

    @Override
    public IdType idType() {
        return IdType.WxSeq;
    }
}
