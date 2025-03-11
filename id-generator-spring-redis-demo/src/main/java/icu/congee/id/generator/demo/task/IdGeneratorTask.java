package icu.congee.id.generator.demo.task;



import icu.congee.id.base.IdType;
import icu.congee.id.generator.service.IdGeneratorService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class IdGeneratorTask {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorTask.class);

    @Resource IdGeneratorService idGeneratorService;

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
