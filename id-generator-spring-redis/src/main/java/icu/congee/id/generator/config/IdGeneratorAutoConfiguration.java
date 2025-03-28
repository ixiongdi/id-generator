/*
 * MIT License
 *
 * Copyright (c) 2024 ixiongdi
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package icu.congee.id.generator.config;

import icu.congee.id.generator.service.IdGeneratorService;
import icu.congee.id.generator.service.impl.IdGeneratorServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * ID生成器自动配置
 */
/**
 * 基于Redis的ID生成器自动配置类
 * <p>
 * 自动配置Redisson客户端并注册ID生成器服务Bean
 * </p>
 * 
 * @author congee
 * @since 1.0.0
 */
@AutoConfiguration(after = RedisAutoConfiguration.class)
@EnableConfigurationProperties(IdGeneratorProperties.class)
/**
 * 默认构造器创建ID生成器自动配置实例
 * <p>
 * 创建一个自动配置实例，用于初始化和配置基于Redis的分布式ID生成器。
 * 该实例负责注册必要的Spring Bean，并确保ID生成器服务的正确配置。
 * </p>
 * 
 * @since 1.0.0
 */
/**
 * 默认构造器创建ID生成器自动配置实例
 * <p>
 * 创建一个自动配置实例，用于初始化和配置基于Redis的分布式ID生成器。
 * 该实例负责注册必要的Spring Bean，并确保ID生成器服务的正确配置。
 * </p>
 * 
 * @since 1.0.0
 */
public class IdGeneratorAutoConfiguration {

    /**
     * 创建Redis实现的ID生成器服务Bean
     * 
     * @return 配置好的ID生成器服务实例
     */
    @Bean
    public IdGeneratorService idGeneratorService() {
        return new IdGeneratorServiceImpl();
    }

}