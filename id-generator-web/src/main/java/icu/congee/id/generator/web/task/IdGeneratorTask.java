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

@Component
public class IdGeneratorTask {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorTask.class);

    @Resource IdService idService;

    ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.SECONDS)
    public void generate() {
        for (IdGenerator generator : loader) {
            Object id = generator.generate();
            idService.insert(generator.idType().getName(), id);
        }
    }
}
