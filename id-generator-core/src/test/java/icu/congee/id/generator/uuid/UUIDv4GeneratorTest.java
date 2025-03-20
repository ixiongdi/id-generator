package icu.congee.id.generator.uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv4生成器的测试类
 * 
 * 测试内容包括：
 * 1. UUID格式正确性
 * 2. 版本号和变体标识的正确性
 * 3. 唯一性测试
 * 4. 并发环境下的唯一性测试
 */
@DisplayName("UUIDv4Generator 测试")
class UUIDv4GeneratorTest {

    @Test
    @DisplayName("测试生成的UUID格式正确性")
    void testUUIDFormat() {
        UUID uuid = UUIDv4Generator.next();
        assertNotNull(uuid, "生成的UUID不应为null");
        String uuidString = uuid.toString();
        assertTrue(uuidString.matches("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"),
                "UUID格式应符合标准格式");
    }

    @Test
    @DisplayName("测试UUID版本和变体标识")
    void testVersionAndVariant() {
        UUID uuid = UUIDv4Generator.next();
        assertEquals(4, uuid.version(), "UUID版本应为4");
        assertEquals(2, uuid.variant(), "UUID变体应为2（RFC 4122）");
    }

    @Test
    @DisplayName("测试UUID唯一性（小规模）")
    void testUniqueness() {
        int count = 10000;
        Set<UUID> uuids = new HashSet<>();
        for (int i = 0; i < count; i++) {
            UUID uuid = UUIDv4Generator.next();
            assertTrue(uuids.add(uuid), "生成的UUID应该是唯一的");
        }
        assertEquals(count, uuids.size(), "生成的UUID数量应该等于预期数量");
    }

    @Test
    @DisplayName("测试并发环境下的UUID唯一性")
    @Execution(ExecutionMode.CONCURRENT)
    void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int uuidsPerThread = 10000;
        Set<UUID> uuids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < uuidsPerThread; j++) {
                        UUID uuid = UUIDv4Generator.next();
                        assertTrue(uuids.add(uuid), "并发环境下生成的UUID应该是唯一的");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();
        assertEquals(threadCount * uuidsPerThread, uuids.size(), "并发生成的UUID总数应该等于预期数量");
    }

    @RepeatedTest(5)
    @DisplayName("测试UUID随机性")
    void testRandomness() {
        UUID uuid1 = UUIDv4Generator.next();
        UUID uuid2 = UUIDv4Generator.next();
        assertNotEquals(uuid1, uuid2, "连续生成的UUID应该不相同");
    }
}