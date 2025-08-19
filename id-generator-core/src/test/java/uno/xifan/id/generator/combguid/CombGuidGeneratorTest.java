package uno.xifan.id.generator.combguid;

import uno.xifan.id.base.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CombGuid生成器的测试类
 * 
 * 测试内容包括：
 * 1. 功能测试
 * - ID格式正确性（验证GUID和时间戳的组合）
 * - 唯一性测试
 * - 时间戳部分正确性测试
 * - 排序性测试
 * 2. 性能测试
 * - 单线程生成性能
 * - 多线程并发生成
 * 3. 可靠性测试
 * - 长时间运行测试
 * - 边界条件测试
 */
@DisplayName("CombGuid Generator Tests")
public class CombGuidGeneratorTest {

    private final CombGuidGenerator generator = new CombGuidGenerator();

    @Test
    @DisplayName("测试生成的ID类型是否正确")
    void testIdType() {
        assertEquals(IdType.COMBGUID, generator.idType());
    }

    @Test
    @DisplayName("测试生成的ID格式是否符合UUID标准")
    void testIdFormat() {
        String id = generator.generate();
        assertNotNull(id);
        assertTrue(id.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"));
    }

    @Test
    @DisplayName("测试生成的ID是否包含正确的时间戳")
    void testTimestampEncoding() {
        long beforeGeneration = Instant.now().toEpochMilli();
        UUID uuid = CombGuidGenerator.next();
        long afterGeneration = Instant.now().toEpochMilli();

        // 从UUID中提取时间戳
        long timestamp = uuid.getMostSignificantBits() >>> 16;

        // 验证时间戳是否在合理范围内
        assertTrue(timestamp >= beforeGeneration && timestamp <= afterGeneration,
                "Generated timestamp should be between before and after generation time");
    }

    @Test
    @DisplayName("测试生成的ID是否保持时间顺序")
    void testTimeOrdering() {
        UUID first = CombGuidGenerator.next();
        UUID second = CombGuidGenerator.next();

        // 提取时间戳部分进行比较
        long firstTimestamp = first.getMostSignificantBits() >>> 16;
        long secondTimestamp = second.getMostSignificantBits() >>> 16;

        assertTrue(firstTimestamp <= secondTimestamp,
                "Second generated ID should have greater or equal timestamp");
    }

    @Test
    @DisplayName("测试单线程下生成大量ID的唯一性")
    void testUniqueness() {
        int count = 100_000;
        Set<String> ids = new HashSet<>();

        for (int i = 0; i < count; i++) {
            String id = generator.generate();
            assertTrue(ids.add(id), "Generated ID should be unique");
        }

        assertEquals(count, ids.size());
    }

    @Test
    @DisplayName("测试多线程并发生成ID的唯一性")
    void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 10_000;
        Set<String> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = generator.generate();
                        assertTrue(ids.add(id), "Generated ID should be unique in concurrent environment");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * idsPerThread, ids.size());
    }

    @Test
    @DisplayName("测试长时间运行的稳定性")
    void testLongRunningStability() {
        int count = 1_000_000;
        Set<String> ids = new HashSet<>();
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < count; i++) {
            String id = generator.generate();
            assertTrue(ids.add(id), "Generated ID should be unique during long running test");
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertEquals(count, ids.size());
        assertTrue(duration < 60000, "Generation of 1 million IDs should complete within 60 seconds");
    }

    @Test
    @DisplayName("测试边界条件")
    void testEdgeCases() {
        // 测试连续快速生成
        String id1 = generator.generate();
        String id2 = generator.generate();
        assertNotEquals(id1, id2, "Consecutive generations should produce different IDs");

        // 测试生成大量ID后的行为
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < 10_000; i++) {
            String id = generator.generate();
            assertTrue(ids.add(id), "Should generate unique IDs even after many generations");
        }
    }
}