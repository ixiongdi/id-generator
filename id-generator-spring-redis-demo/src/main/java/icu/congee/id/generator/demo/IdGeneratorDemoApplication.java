package icu.congee.id.generator.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/** ID生成器示例应用 */
@SpringBootApplication
@EnableScheduling
public class IdGeneratorDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorDemoApplication.class, args);
    }
}
