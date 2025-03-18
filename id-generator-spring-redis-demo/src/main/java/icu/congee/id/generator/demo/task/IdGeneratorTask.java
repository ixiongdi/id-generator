package icu.congee.id.generator.demo.task;

import icu.congee.id.base.IdType;
import icu.congee.id.generator.service.IdGeneratorService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component

/**
 * 定时ID生成任务执行器
 * <p>
 * 演示通过定时任务自动生成分布式ID的工作模式
 * </p>
 * 
 * @author congee
 * @version 1.0
 */
public class IdGeneratorTask {

    /**
     * 默认构造器创建定时任务实例
     */

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorTask.class);

    @Resource
    IdGeneratorService idGeneratorService;

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)

    /**
     * 执行定时ID生成任务
     * 
     * @throws InterruptedException 当获取分布式锁时被中断抛出
     */
    @Scheduled(fixedRate = 5000)
    public void generate() throws InterruptedException {
        // 生成一个ID
        Object id = idGeneratorService.generate(IdType.CosId);
        logger.info("Cos Id: {}", id);
        // 生成多个ID
        Object[] ids = idGeneratorService.generate(IdType.CosId, 100);
        logger.info("id count: {}", ids.length);
        for (Object o : ids) {
            logger.info("Cos Id: {}", o);
        }
    }
}
