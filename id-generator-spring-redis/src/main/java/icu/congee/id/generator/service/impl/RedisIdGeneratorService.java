package icu.congee.id.generator.service.impl;

import cn.hutool.core.util.IdUtil;
import icu.congee.id.generator.config.IdGeneratorProperties;
import icu.congee.id.generator.service.IdGeneratorService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redisson分布式锁的ID生成器实现
 */
public class RedisIdGeneratorService implements IdGeneratorService {

    private final RedissonClient redissonClient;
    private final String keyPrefix;
    private final IdGeneratorProperties.IdType idType;
    private static final long LOCK_TIMEOUT = 5000; // 锁超时时间，单位毫秒

    public RedisIdGeneratorService(RedissonClient redissonClient, String keyPrefix, IdGeneratorProperties.IdType idType) {
        this.redissonClient = redissonClient;
        this.keyPrefix = keyPrefix;
        this.idType = idType;
    }

    @Override
    public long nextId(String key) {
        RLock lock = redissonClient.getLock(getLockKey(key));
        try {
            // 尝试获取分布式锁
            if (!lock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Failed to acquire lock for ID generation");
            }
            // 根据配置的idType生成ID
            return generateId();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while acquiring lock", e);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public long[] nextId(String key, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        RLock lock = redissonClient.getLock(getLockKey(key));
        try {
            // 尝试获取分布式锁
            if (!lock.tryLock(LOCK_TIMEOUT, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Failed to acquire lock for ID generation");
            }
            // 批量生成ID
            long[] ids = new long[size];
            for (int i = 0; i < size; i++) {
                ids[i] = generateId();
            }
            return ids;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted while acquiring lock", e);
        } finally {
            // 释放锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    private String getLockKey(String key) {
        return keyPrefix + ":lock:" + key;
    }

    private long generateId() {
        switch (idType) {
            case SNOWFLAKE:
                return IdUtil.getSnowflakeNextId();
            case UUID:
                // 将UUID转换为正数的long值
                return Math.abs(IdUtil.fastSimpleUUID().hashCode());
            default:
                throw new IllegalStateException("Unsupported ID type: " + idType);
        }
    }
}