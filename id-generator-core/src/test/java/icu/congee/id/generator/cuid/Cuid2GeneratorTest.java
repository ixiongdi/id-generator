package icu.congee.id.generator.cuid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Cuid2 ID generator.
 * Tests focus on:
 * 1. ID format and length validation
 * 2. Uniqueness in single and multi-threaded scenarios
 * 3. Security (non-predictability)
 * 4. Performance under concurrent load
 */
public class Cuid2GeneratorTest {

    private final CUIDv2Generator generator = new CUIDv2Generator();

    @Test
    void testGenerateNotNull() {
        String id = generator.generate();
        assertNotNull(id, "Generated ID should not be null");
    }

    @Test
    void testIdFormat() {
        String id = generator.generate();
        // Cuid2 uses base36 encoding (0-9 and a-z)
        assertTrue(id.matches("^[0-9a-z]+$"), "ID should only contain lowercase letters and numbers");
    }

    @Test
    void testIdLength() {
        String id = generator.generate();
        assertEquals(24, id.length(), "ID length should match standard length");
    }

    @Test
    void testUniqueness() {
        Set<String> ids = new HashSet<>();
        int count = 10000;

        for (int i = 0; i < count; i++) {
            String id = generator.generate();
            assertTrue(ids.add(id), "Generated IDs should be unique");
        }

        assertEquals(count, ids.size(), "All generated IDs should be unique");
    }

    @Test
    void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        Set<String> ids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String id = generator.generate();
                        assertTrue(ids.add(id), "Generated IDs should be unique even in concurrent scenarios");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        assertEquals(threadCount * idsPerThread, ids.size(), "All concurrently generated IDs should be unique");
    }

    @Test
    void testSequentialIdsNotPredictable() {
        String id1 = generator.generate();
        String id2 = generator.generate();
        String id3 = generator.generate();

        assertNotEquals(id1, id2, "Sequential IDs should be different");
        assertNotEquals(id2, id3, "Sequential IDs should be different");
        assertNotEquals(id1, id3, "Sequential IDs should be different");

        // Check that IDs are not simply incremental
        assertFalse(id2.equals(incrementString(id1)), "IDs should not be simply incremental");
        assertFalse(id3.equals(incrementString(id2)), "IDs should not be simply incremental");
    }


    @Test
    void testPerformance() {
        int count = 10000;
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            generator.generate();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // 确保生成速度在合理范围内（平均每个ID生成时间不超过0.1毫秒）
        assertTrue(durationMs < count * 0.1, "ID generation should be reasonably fast");
    }

    // 辅助方法：将字符串视为base36数字并加1
    private String incrementString(String input) {
        // 简单实现，仅用于测试目的
        try {
            long value = Long.parseLong(input, 36);
            return Long.toString(value + 1, 36);
        } catch (NumberFormatException e) {
            return input;
        }
    }
}