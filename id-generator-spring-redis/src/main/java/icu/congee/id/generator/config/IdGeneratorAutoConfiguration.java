package icu.congee.id.generator.config;

import icu.congee.id.generator.service.IdGeneratorService;
import icu.congee.id.generator.service.impl.RedisIdGeneratorService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ID生成器自动配置
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorAutoConfiguration {

    @Bean
    public IdGeneratorService idGeneratorService() {
        return new RedisIdGeneratorService();
    }

}