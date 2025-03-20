package icu.congee.id.generator.web.task;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.generator.web.service.IdService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;
import java.util.concurrent.TimeUnit;

/**
 * ID生成器任务类，负责定期生成各种类型的ID并存储。
 * 该类通过ServiceLoader加载所有可用的ID生成器实现，并按照固定时间间隔生成ID。
 */
@Component
public class IdGeneratorTask {

    /**
     * 默认构造器创建定时任务实例
     * <p>
     * 创建一个新的ID生成器任务实例，用于定期生成和存储各种类型的ID。
     * 该实例会被Spring容器管理，并自动注入所需的依赖。
     * </p>
     */

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorTask.class);

    /** ID服务接口，用于存储生成的ID */
    @Resource
    IdService idService;

    /** 用于加载所有ID生成器实现的ServiceLoader */
    ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

    /**
     * 定期执行的ID生成任务。
     * 每秒执行一次，遍历所有可用的ID生成器，生成ID并存储。
     */
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void generate() {
        for (IdGenerator generator : loader) {
            Object id = generator.generate();
            idService.insert(generator.idType().getName(), id);
        }
    }
}
