package icu.congee.id.generator.web.task;

import cn.hutool.core.util.StrUtil;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.generator.web.service.IdService;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ServiceLoader;

@Component
public class IdGeneratorTask {

    private static final Logger logger = LoggerFactory.getLogger(IdGeneratorTask.class);

    @Resource IdService idService;

    ServiceLoader<IdGenerator> loader = ServiceLoader.load(IdGenerator.class);

    private static String convertClassNameToTableName(String className) {
        // Remove the package name and "Generator" suffix
        String simpleName =
                className.substring(className.lastIndexOf('.') + 1).replace("Generator", "");
        // Convert camel case to underscore
        return StrUtil.toUnderlineCase(simpleName);
    }

    @Scheduled(fixedRate = 1)
    public void generate() {
        for (IdGenerator generator : loader) {
            Object id = generator.generate();
            idService.insert(convertClassNameToTableName(generator.getClass().getSimpleName()), id);
            logger.info(StrUtil.format("Generated id [{}]", id));
        }
    }
}
