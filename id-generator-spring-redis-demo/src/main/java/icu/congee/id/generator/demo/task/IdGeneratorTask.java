package icu.congee.id.generator.demo.task;

import cn.hutool.log.LogFactory;
import icu.congee.id.generator.distributed.atomiclong.AtomicLongIdGenerator;
import icu.congee.id.generator.distributed.cosid.CosIdGenerator;
import icu.congee.id.generator.distributed.mist.MistIdGenerator;
import icu.congee.id.generator.distributed.snowflake.SnowflakeIdGenerator;
import icu.congee.id.generator.distributed.rid.RedissonIdGenerator;

import jakarta.annotation.Resource;

import cn.hutool.log.Log;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.LockSupport;

@Component

/**
 * 定时ID生成任务执行器
 *
 * <p>演示通过定时任务自动生成分布式ID的工作模式
 *
 * @author congee
 * @version 1.0
 */

public class IdGeneratorTask implements CommandLineRunner {

    Log log = LogFactory.get();

    /** 默认构造器创建定时任务实例 */
    @Resource RedissonIdGenerator redissonIdGenerator;

    @Resource SnowflakeIdGenerator snowflakeIdGenerator;

    @Resource MistIdGenerator mistIdGenerator;

    @Resource AtomicLongIdGenerator atomicLongIdGenerator;

    @Resource CosIdGenerator cosIdGenerator;

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            log.info("redisson id: {}", redissonIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("snowflake id: {}", snowflakeIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("mist id: {}", mistIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("atomic long id: {}", atomicLongIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("cos id: {}", cosIdGenerator.generate().toBase62());
        }
    }
}
