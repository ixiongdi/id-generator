package icu.congee.id.generator.web;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ID生成器Web应用程序的启动类。
 * 该类负责启动Spring Boot应用程序并启用定时任务功能。
 */
@SpringBootApplication
@EnableScheduling
@MapperScan("icu.congee.id.generator.web.mapper")
public class IdGeneratorWebApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorWebApplication.class, args);
    }
}