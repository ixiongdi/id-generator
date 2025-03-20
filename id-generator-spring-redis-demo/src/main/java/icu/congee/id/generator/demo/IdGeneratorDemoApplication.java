package icu.congee.id.generator.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ID生成器示例应用
 */
@SpringBootApplication
@EnableScheduling
/**
 * 基于Spring Boot的分布式ID生成器示例应用
 * <p>
 * 演示如何通过Redis实现分布式ID生成服务
 * </p>
 * 
 * @author congee
 * @version 1.0
 * @since 2024-05-20
 */
public class IdGeneratorDemoApplication {

    /**
     * 默认构造器
     * <p>
     * 创建一个新的IdGeneratorDemoApplication实例
     * </p>
     */
    public IdGeneratorDemoApplication() {
        // 默认构造器
    }

    /**
     * 应用主入口
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorDemoApplication.class, args);
    }
}