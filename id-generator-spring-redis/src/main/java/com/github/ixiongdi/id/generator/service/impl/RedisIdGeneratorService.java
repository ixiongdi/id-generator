package com.github.ixiongdi.id.generator.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.ixiongdi.id.generator.config.IdGeneratorProperties;
import com.github.ixiongdi.id.generator.service.IdGeneratorService;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.util.concurrent.TimeUnit;

/**
 * 基于Redis分布式锁的ID生成器实现
 */
public class RedisIdGeneratorService implements IdGeneratorService {

    private final StringRedisTemplate redisTemplate;
    private final String keyPrefix;
    private final IdGeneratorProperties.IdType idType;
    private static final long LOCK_TIMEOUT = 5000; // 锁超时时间，单位毫秒

    public RedisIdGeneratorService(StringRedisTemplate redisTemplate, String keyPrefix, IdGeneratorProperties.IdType idType) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
        this.idType = idType;
    }

    @Override
    public long nextId(String key) {
        String lockKey = getLockKey(key);
        try {
            // 尝试获取分布式锁
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TIMEOUT,
                    TimeUnit.MILLISECONDS);
            if (acquired == null || !acquired) {
                throw new RuntimeException("Failed to acquire lock for ID generation");
            }
            // 根据配置的idType生成ID
            return generateId();
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
        }
    }

    @Override
    public long[] nextId(String key, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive");
        }
        String lockKey = getLockKey(key);
        try {
            // 尝试获取分布式锁
            Boolean acquired = redisTemplate.opsForValue().setIfAbsent(lockKey, "1", LOCK_TIMEOUT,
                    TimeUnit.MILLISECONDS);
            if (acquired == null || !acquired) {
                throw new RuntimeException("Failed to acquire lock for ID generation");
            }
            // 批量生成ID
            long[] ids = new long[size];
            for (int i = 0; i < size; i++) {
                ids[i] = generateId();
            }
            return ids;
        } finally {
            // 释放锁
            redisTemplate.delete(lockKey);
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