package icu.congee.id.generator.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

/** ID生成器示例应用 */
@SpringBootApplication
@EnableScheduling
@ComponentScans(value = {
        @ComponentScan("icu.congee.id.generator"),
})
@MapperScan("icu.congee.id.generator.demo.mapper")
public class IdGeneratorDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorDemoApplication.class, args);
    }
}
