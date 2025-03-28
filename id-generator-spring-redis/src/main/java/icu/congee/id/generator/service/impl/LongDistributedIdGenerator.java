package icu.congee.id.generator.service.impl;

import icu.congee.id.generator.service.DistributedIdGenerator;

import jakarta.annotation.Resource;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Service
public class LongDistributedIdGenerator implements DistributedIdGenerator {

    @Resource private RedissonClient redisson;

    private final RAtomicLong atomicLong = redisson.getAtomicLong("RAtomicLong");

    @Override
    public Long generate() {
        return atomicLong.incrementAndGet();
    }

    @Override
    public Long[] generate(int count) {
        Long[] ids = new Long[count];
        long current = atomicLong.get();
        for (int i = 0; i < count; i++) {
            ids[i] = ++current;
        }
        atomicLong.set(current);
        return ids;
    }
}
