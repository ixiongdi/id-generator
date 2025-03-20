/*
 * MIT License
 *
 * Copyright (c) 2024 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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

/**
 * 基于Redisson分布式锁的ID生成器实现类
 * <p>
 * 提供通过Redis分布式锁实现的线程安全ID生成服务
 * </p>
 * 
 * @author congee
 * @since 1.0.0
 */
@Service
/**
 * 默认构造器创建RedisID生成器实例
 * <p>
 * 创建一个基于Redis的分布式ID生成器服务实例。该实例使用Redisson客户端
 * 实现分布式锁机制，确保在分布式环境下ID生成的唯一性和线程安全性。
 * </p>
 * 
 * @since 1.0.0
 */
public class RedisIdGeneratorService implements IdGeneratorService {

    /**
     * 默认构造器创建RedisID生成器实例
     * <p>
     * 创建一个基于Redis的分布式ID生成器服务实例。该实例使用Redisson客户端
     * 实现分布式锁机制，确保在分布式环境下ID生成的唯一性和线程安全性。
     * </p>
     * 
     * @since 1.0.0
     */

    private static final Map<IdType, IdGenerator> idGeneratorMap = IdUtil.getIdGeneratorMap();
    @Resource
    private RedissonClient redisson;

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
