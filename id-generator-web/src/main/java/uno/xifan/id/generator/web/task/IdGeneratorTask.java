package uno.xifan.id.generator.web.task;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.generator.web.entity.IdGeneratorEntity;
import uno.xifan.id.generator.web.service.IdGeneratorService;
import jakarta.annotation.Resource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

/**
 * ID生成器任务类，负责定期生成各种类型的ID并存储。
 * 该类通过ServiceLoader加载所有可用的ID生成器实现，并按照固定时间间隔生成ID。
 */
@Component
public class IdGeneratorTask {

    /**
     * ID服务接口，用于存储生成的ID
     */
    @Resource
    IdGeneratorService idGeneratorService;

    /**
     * 用于加载所有ID生成器实现的ServiceLoader
     */
    ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

    /**
     * 定期执行的ID生成任务。
     * 每秒执行一次，遍历所有可用的ID生成器，生成ID并存储。
     */
    @Scheduled(cron = "* * * * * ?")
    public void generate() {
        for (IdGenerator generator : loader) {
            Object id = generator.generate();
            IdGeneratorEntity idGeneratorEntity = new IdGeneratorEntity();
            idGeneratorEntity.setId(id);
            idGeneratorEntity.setIdType(generator.idType().getName());
            idGeneratorService.save(idGeneratorEntity);
        }
    }
}
