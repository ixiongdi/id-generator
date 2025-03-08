package icu.congee.id.generator.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IdGeneratorWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorWebApplication.class, args);
    }
}