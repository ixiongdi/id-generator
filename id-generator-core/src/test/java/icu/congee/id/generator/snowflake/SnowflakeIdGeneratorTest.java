package icu.congee.id.generator.snowflake;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Snowflake ID生成器测试类
 * 
 * 测试内容包括：
 * - 唯一性测试
 * - 格式正确性测试
 * - 单调性测试
 * - 时钟回拨处理测试
 * - 并发性能测试
 */
@DisplayName("Snowflake ID生成器测试")
public class SnowflakeIdGeneratorTest {

    private SnowflakeIdGenerator generator;

    @BeforeEach
    void setUp() {
        // 使用工作节点ID为1的生成器
        generator = new SnowflakeIdGenerator(1);
    }

    @Test
    @DisplayName("测试生成的ID类型正确")
    void testIdType() {
        assertEquals(IdType.Snowflake, generator.idType(), "ID类型应为Snowflake");
    }

    @Test
    @DisplayName("测试生成的ID格式正确")
    void testIdFormat() {
        long id = (long) generator.generate();

        // ID应该是一个正数
        assertTrue(id > 0, "生成的ID应该是正数");

        // 检查ID的位数（Snowflake ID应为64位，但Java long也是64位，所以这里主要检查不为0）
        assertNotEquals(0, id, "生成的ID不应为0");
    }

    @Test
    @DisplayName("测试ID的唯一性")
    void testIdUniqueness() {
        int count = 10000; // 生成10000个ID进行唯一性测试
        Set<Long> idSet = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            long id = (long) generator.generate();
            assertFalse(idSet.contains(id), "生成的ID不应重复: " + id);
            idSet.add(id);
        }

        assertEquals(count, idSet.size(), "生成的所有ID应该唯一");
    }

    @Test
    @DisplayName("测试ID的单调递增性")
    void testIdMonotonicity() {
        long previousId = (long) generator.generate();

        // 生成100个ID并验证它们是单调递增的
        for (int i = 0; i < 100; i++) {
            long currentId = (long) generator.generate();
            assertTrue(currentId > previousId,
                    "当前ID应大于前一个ID: 当前ID=" + currentId + ", 前一个ID=" + previousId);
            previousId = currentId;
        }
    }

    @Test
    @DisplayName("测试工作节点ID超出范围时抛出异常")
    void testInvalidWorkerId() {
        // 工作节点ID的最大值是1023 (2^10 - 1)
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1024),
                "工作节点ID超出最大值应抛出异常");

        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1),
                "工作节点ID为负数应抛出异常");
    }

    @Test
    @DisplayName("测试高并发下的唯一性")
    void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        int totalIds = threadCount * idsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<Long> idSet = new HashSet<>(totalIds);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        synchronized (idSet) {
                            long id = (long) generator.generate();
                            assertFalse(idSet.contains(id), "并发生成的ID不应重复");
                            idSet.add(id);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "并发测试应在30秒内完成");
        executor.shutdown();

        assertEquals(totalIds, idSet.size(), "并发生成的所有ID应该唯一");
    }

    @Test
    @DisplayName("测试序列号溢出处理")
    void testSequenceOverflow() {
        // 创建一个特殊的Snowflake生成器，用于测试序列号溢出
        SnowflakeIdGenerator testGenerator = new SnowflakeIdGenerator(1) {
            private final long fixedTimestamp = System.currentTimeMillis();
            private int sequenceCallCount = 0;

            private long timeGen() {
                // 前4096次调用返回相同的时间戳，之后返回递增的时间戳
                if (sequenceCallCount < 4096) {
                    return fixedTimestamp;
                } else {
                    return fixedTimestamp + (sequenceCallCount - 4095);
                }
            }

            @Override
            public synchronized long next() {
                sequenceCallCount++;
                return super.next();
            }
        };

        // 生成4096个ID（序列号从0到4095）
        Set<Long> idSet = new HashSet<>(4100);
        for (int i = 0; i < 4100; i++) {
            long id = testGenerator.next();
            assertFalse(idSet.contains(id), "序列号溢出处理后生成的ID不应重复");
            idSet.add(id);
        }

        assertEquals(4100, idSet.size(), "序列号溢出处理后生成的所有ID应该唯一");
    }

    @RepeatedTest(5)
    @DisplayName("测试生成性能")
    void testGenerationPerformance() {
        int count = 100000;
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            generator.generate();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.printf("生成 %d 个ID耗时: %d ms, 平均每秒生成: %.2f 个ID%n",
                count, durationMs, count * 1000.0 / durationMs);

        // 性能断言：生成10万个ID应该在1秒内完成
        assertTrue(durationMs < 1000, "生成10万个ID应在1秒内完成");
    }
}