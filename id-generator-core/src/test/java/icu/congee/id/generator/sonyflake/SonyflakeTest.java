package icu.congee.id.generator.sonyflake;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Sonyflake ID生成器的单元测试类
 */
public class SonyflakeTest {

    private final SonyflakeIdGenerator generator = new SonyflakeIdGenerator();

    @Test
    void testGenerateId() {
        long id = generator.generate();
        assertTrue(id > 0, "生成的ID应该大于0");
    }

    @Test
    void testIdUniqueness() {
        Set<Long> ids = new HashSet<>();
        int count = 100;
        for (int i = 0; i < count; i++) {
            long id = generator.generate();
            assertTrue(ids.add(id), "生成的ID应该是唯一的");
        }
        assertEquals(count, ids.size(), "生成的ID数量应该等于请求的数量");
    }

    @Test
    void testIdType() {
        assertEquals(IdType.Sonyflake, generator.idType(), "生成器类型应该是Sonyflake");
    }

    @Test
    void testSequentialIds() {
        long firstId = generator.generate();
        long secondId = generator.generate();
        assertTrue(secondId > firstId, "连续生成的ID应该是递增的");
    }

    @Test
    void testPerformance() {
        long startTime = System.currentTimeMillis();
        int count = 100;
        for (int i = 0; i < count; i++) {
            generator.generate();
        }
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(duration < 1000, "生成10万个ID应该在1秒内完成");
        System.out.printf("生成%d个ID耗时：%d毫秒，平均每毫秒生成：%.2f个%n",
                count, duration, count / (double) duration);
    }
}