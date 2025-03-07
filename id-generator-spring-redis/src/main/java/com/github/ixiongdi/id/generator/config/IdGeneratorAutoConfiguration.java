package com.github.ixiongdi.id.generator.config;

import com.github.ixiongdi.id.generator.service.IdGeneratorService;
import com.github.ixiongdi.id.generator.service.impl.RedisIdGeneratorService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * ID生成器自动配置
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(IdGeneratorProperties.class)
public class IdGeneratorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public IdGeneratorService idGeneratorService(StringRedisTemplate redisTemplate, IdGeneratorProperties properties) {
        return new RedisIdGeneratorService(redisTemplate, properties.getKeyPrefix(), properties.getIdType());
    }
}