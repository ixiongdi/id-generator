package icu.congee.id.generator.demo.task;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import icu.congee.id.generator.distributed.atomiclong.AtomicLongIdGenerator;
import icu.congee.id.generator.distributed.broid.BroIdGenerator;
import icu.congee.id.generator.distributed.broid.BroIdGeneratorPro;
import icu.congee.id.generator.distributed.broid.BroIdGeneratorUltra;
import icu.congee.id.generator.distributed.cosid.CosIdGenerator;
import icu.congee.id.generator.distributed.mist.MistIdGenerator;
import icu.congee.id.generator.distributed.rid.RedissonIdGenerator;
import icu.congee.id.generator.distributed.snowflake.SnowflakeIdGenerator;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

@Component

/**
 * 定时ID生成任务执行器
 *
 * <p>演示通过定时任务自动生成分布式ID的工作模式
 *
 * @author congee
 * @version 1.0
 */ public class IdGeneratorTask implements CommandLineRunner {

    Log log = LogFactory.get();

    /**
     * 默认构造器创建定时任务实例
     */
    @Resource
    RedissonIdGenerator redissonIdGenerator;

    @Resource
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Resource
    MistIdGenerator mistIdGenerator;

    @Resource
    AtomicLongIdGenerator atomicLongIdGenerator;

    @Resource
    CosIdGenerator cosIdGenerator;

    @Resource
    BroIdGenerator broIdGenerator;

    @Resource
    BroIdGeneratorPro broIdGeneratorPro;

    @Resource
    BroIdGeneratorUltra broIdGeneratorUltra;

    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public void run(String... args) {
        for (int i = 0; i < 10; i++) {
            log.info("redisson id: {}", redissonIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("snowflake id: {}", snowflakeIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("atomic long id: {}", atomicLongIdGenerator.generate());
        }

        for (int i = 0; i < 10; i++) {
            log.info("cos id: {}", cosIdGenerator.generate().toBase62());
        }

        for (int i = 0; i < 10; i++) {
            log.info("bro id: {}", broIdGenerator.generate().toLong());
        }

        for (int i = 0; i < 10; i++) {
            log.info("bro id pro encode base64: {}", broIdGeneratorPro.generate().toBase64());
            log.info("bro id pro encode base62: {}", broIdGeneratorPro.generate().toBase62());
            log.info("bro id pro encode base36: {}", broIdGeneratorPro.generate().toBase36());
            log.info("bro id pro encode base32: {}", broIdGeneratorPro.generate().toBase32());
            log.info("bro id pro encode base16: {}", broIdGeneratorPro.generate().toBase16());
            log.info("bro id pro encode base10: {}", broIdGeneratorPro.generate().toBase10());
        }

        for (int i = 0; i < 10; i++) {
            log.info("bro id pro max encode base64: {}", broIdGeneratorUltra.generate().toBase64());
            log.info("bro id pro max encode base62: {}", broIdGeneratorUltra.generate().toBase62());
            log.info("bro id pro max encode base36: {}", broIdGeneratorUltra.generate().toBase36());
            log.info("bro id pro max encode base32: {}", broIdGeneratorUltra.generate().toBase32());
            log.info("bro id pro max encode base16: {}", broIdGeneratorUltra.generate().toBase16());
            log.info("bro id pro max encode base10: {}", broIdGeneratorUltra.generate().toBase10());
        }

        SpringApplication.exit(context, () -> 0);
        System.exit(0);
    }
}
