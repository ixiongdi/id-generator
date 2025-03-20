package icu.congee.id.generator.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ID生成器Web应用程序的启动类。
 * 该类负责启动Spring Boot应用程序并启用定时任务功能。
 */
@SpringBootApplication
@EnableScheduling
public class IdGeneratorWebApplication {

    /**
     * 默认构造器创建Web应用实例
     * <p>
     * 创建一个新的ID生成器Web应用实例，用于启动Spring Boot应用程序。
     * 该实例负责初始化Spring容器，配置自动扫描和启用定时任务功能。
     * </p>
     */

    /**
     * 应用程序入口点。
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorWebApplication.class, args);
    }
}