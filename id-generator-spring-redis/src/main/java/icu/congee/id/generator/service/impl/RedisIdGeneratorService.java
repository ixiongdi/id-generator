package icu.congee.id.generator.service.impl;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.service.IdGeneratorService;
import icu.congee.id.util.IdUtil;

import jakarta.annotation.Resource;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
/** 基于Redisson分布式锁的ID生成器实现 */
public class RedisIdGeneratorService implements IdGeneratorService {

    private static final Map<IdType, IdGenerator> idGeneratorMap = IdUtil.getIdGeneratorMap();
    @Resource private RedissonClient redisson;

    @Override
    public Object generate(IdType idType) throws InterruptedException {
        RLock lock = redisson.getLock(idType.getName());
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        Object id = null;
        if (res) {
            try {
                IdGenerator idGenerator = idGeneratorMap.get(idType);
                id = idGenerator.generate();
            } finally {
                lock.unlock();
            }
        }
        return id;
    }

    @Override
    public Object[] generate(IdType idType, int count) throws InterruptedException {
        RLock lock = redisson.getLock(idType.getName());
        boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
        Object[] ids = null;
        if (res) {
            try {
                IdGenerator idGenerator = idGeneratorMap.get(idType);
                ids = idGenerator.generate(count);
            } finally {
                lock.unlock();
            }
        }
        return ids;
    }
}
