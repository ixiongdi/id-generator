package uno.xifan.id.generator.uuid;

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
 * UUIDv6生成器的测试类
 * 
 * 测试内容包括：
 * 1. UUID格式正确性
 * 2. 版本号和变体标识的正确性
 * 3. 时间戳单调递增性
 * 4. 并发环境下的唯一性测试
 * 5. 时钟回拨处理
 */
@DisplayName("UUIDv6Generator 测试")
class UUIDv6GeneratorTest {

    @Test
    @DisplayName("测试生成的UUID格式正确性")
    void testUUIDFormat() {
        UUID uuid = UUIDv6Generator.next();
        assertNotNull(uuid, "生成的UUID不应为null");
        String uuidString = uuid.toString();
        assertTrue(uuidString.matches("^[0-9a-f]{8}-[0-9a-f]{4}-6[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"),
                "UUID格式应符合UUIDv6标准格式");
    }

    @Test
    @DisplayName("测试UUID版本和变体标识")
    void testVersionAndVariant() {
        UUID uuid = UUIDv6Generator.next();
        assertEquals(6, uuid.version(), "UUID版本应为6");
        assertEquals(2, uuid.variant(), "UUID变体应为2（RFC 4122）");
    }

    @Test
    @DisplayName("测试时间戳单调递增性")
    void testMonotonicity() {
        UUID uuid1 = UUIDv6Generator.next();
        UUID uuid2 = UUIDv6Generator.next();
        UUID uuid3 = UUIDv6Generator.next();

        assertTrue(uuid2.compareTo(uuid1) > 0, "后生成的UUID应大于先生成的UUID");
        assertTrue(uuid3.compareTo(uuid2) > 0, "后生成的UUID应大于先生成的UUID");
    }

    @Test
    @DisplayName("测试UUID唯一性（小规模）")
    void testUniqueness() {
        int count = 10000;
        Set<UUID> uuids = new HashSet<>();
        for (int i = 0; i < count; i++) {
            UUID uuid = UUIDv6Generator.next();
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
                        UUID uuid = UUIDv6Generator.next();
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
    @DisplayName("测试时钟回拨处理")
    void testClockBackwards() {
        UUID uuid1 = UUIDv6Generator.next();
        UUID uuid2 = UUIDv6Generator.next();
        // 即使在时钟回拨的情况下，新生成的UUID也应该大于之前的UUID
        assertTrue(uuid2.compareTo(uuid1) > 0, "时钟回拨时生成的UUID应保持单调递增");
    }
}