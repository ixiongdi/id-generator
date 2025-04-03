package icu.congee.id.generator.distributed.snowflake;

import static org.junit.jupiter.api.Assertions.*;

import jakarta.annotation.Resource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@SpringBootTest
class SnowflakeIdGeneratorTest {

    @Resource
    SnowflakeIdGenerator snowflakeIdGenerator;

    @Test
    void testGenerateId() {
        long id1 = snowflakeIdGenerator.generate();
        long id2 = snowflakeIdGenerator.generate();

        assertNotEquals(id1, id2);
        assertTrue(id1 > 0);
        assertTrue(id2 > 0);
    }

    @Test
    void testIdIncrement() {
        long id1 = snowflakeIdGenerator.generate();
        long id2 = snowflakeIdGenerator.generate();

        assertTrue(id2 > id1);
    }

    @Test
    void testIdUniqueness() {
        Set<Long> idSet = new HashSet<>();
        int count = 10000;

        for (int i = 0; i < count; i++) {
            long id = snowflakeIdGenerator.generate();
            assertTrue(idSet.add(id), "Generated ID should be unique");
        }

        assertEquals(count, idSet.size(), "All generated IDs should be unique");
    }

    @Test
    void testIdMonotonicity() {
        int count = 10000;
        long previousId = snowflakeIdGenerator.generate();

        for (int i = 1; i < count; i++) {
            long currentId = snowflakeIdGenerator.generate();
            assertTrue(currentId > previousId, "Each ID should be greater than the previous one");
            previousId = currentId;
        }
    }

    @Test
    void testPerformance() {
        // 测试持续时间为1秒
        long testDuration = 1000L;
        Set<Long> idSet = new HashSet<>();
        long startTime = System.currentTimeMillis();
        long endTime = startTime + testDuration;

        // 在1秒内持续生成ID
        while (System.currentTimeMillis() < endTime) {
            idSet.add(snowflakeIdGenerator.generate());
        }

        // 计算实际执行时间（毫秒）
        long actualDuration = System.currentTimeMillis() - startTime;
        // 计算每毫秒生成的ID数量
        double idsPerMillisecond = (double) idSet.size() / actualDuration;

        System.out.printf("Generated %d unique IDs in %d ms, average %.2f IDs per millisecond%n",
                idSet.size(), actualDuration, idsPerMillisecond);

        // 验证生成速率是否达到要求（每毫秒1000个以上）
        assertTrue(idsPerMillisecond >= 1000,
                String.format("Performance requirement not met: %.2f ids/ms < 1000 ids/ms", idsPerMillisecond));
    }
}
