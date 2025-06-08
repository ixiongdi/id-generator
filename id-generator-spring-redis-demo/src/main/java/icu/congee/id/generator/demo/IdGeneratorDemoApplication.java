package icu.congee.id.generator.demo;

import icu.congee.id.generator.distributed.mist.MistIdGenerator;
import icu.congee.id.generator.distributed.segmentid.SegmentChainIdGenerator;
import icu.congee.id.generator.distributed.uuid.UUIDv8Generator;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * ID生成器示例应用
 */
@SpringBootApplication
@EnableScheduling
@ComponentScans(value = {@ComponentScan("icu.congee.id.generator"),})
@MapperScan("icu.congee.id.generator.demo.mapper")
@Slf4j
public class IdGeneratorDemoApplication implements CommandLineRunner {
    @Resource
    private MistIdGenerator mistIdGenerator;
    @Resource
    private UUIDv8Generator uuiDv8Generator;

    @Resource
    private SegmentChainIdGenerator segmentChainIdGenerator;



    public static void main(String[] args) {
        SpringApplication.run(IdGeneratorDemoApplication.class, args);
    }

    @Override
    public void run(String... args) {

        // 薄雾算法（基于原子自增和随机数的ID生成算法）
        for (int i = 0; i < 10; i++) {
            System.out.println(mistIdGenerator.generate().toLong());
        }
        // UUID v8（基于时间戳、循环计数器、节点ID的生成算法，符合UUID最新标准）
        for (int i = 0; i < 10; i++) {
            System.out.println(uuiDv8Generator.generate().toUUID());
        }
        // SegmentChainIdGenerator
        while (true) {
            segmentChainIdGenerator.generate();
//            log.info("SegmentChainIdGenerator: {}", segmentChainIdGenerator.generate());
        }

    }
}
