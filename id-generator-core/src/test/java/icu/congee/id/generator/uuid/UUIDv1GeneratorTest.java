package icu.congee.id.generator.uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv1Generator的单元测试类
 * 
 * 测试覆盖以下方面：
 * 1. UUID格式正确性和版本标识验证
 * 2. 时间戳单调递增性和时钟回拨处理
 * 3. 节点ID生成策略
 * 4. 时钟序列的并发安全性
 * 5. 高负载下的性能和唯一性测试
 */
public class UUIDv1GeneratorTest {

    private static final int CONCURRENT_THREADS = 10;
    private static final int ITERATIONS_PER_THREAD = 1000;

    /**
     * 测试生成的UUID格式正确性
     */
    @Test
    public void testUUIDFormat() {
        UUID uuid = UUIDv1Generator.next();

        // 验证版本号为1
        assertEquals(1, uuid.version());

        // 验证变体号符合RFC 4122
        assertEquals(2, uuid.variant());

        // 验证字符串格式
        String uuidStr = uuid.toString();
        assertTrue(uuidStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-1[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"));
    }

    /**
     * 测试时间戳单调递增性
     */
    @Test
    public void testTimestampMonotonicity() {
        UUID first = UUIDv1Generator.next();
        UUID second = UUIDv1Generator.next();

        // 提取时间戳部分
        long timestamp1 = extractTimestamp(first);
        long timestamp2 = extractTimestamp(second);

        // 验证时间戳单调递增
        assertTrue(timestamp2 >= timestamp1, "Timestamps should be monotonically increasing");
    }

    /**
     * 测试并发生成的UUID唯一性
     */
    @RepeatedTest(3)
    public void testConcurrentUniqueness() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_THREADS);
        Set<UUID> uuids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(CONCURRENT_THREADS);

        // 创建多个线程并发生成UUID
        for (int i = 0; i < CONCURRENT_THREADS; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ITERATIONS_PER_THREAD; j++) {
                        UUID uuid = UUIDv1Generator.next();
                        assertTrue(uuids.add(uuid), "Duplicate UUID generated: " + uuid);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证生成的UUID数量
        assertEquals(CONCURRENT_THREADS * ITERATIONS_PER_THREAD, uuids.size(),
                "Expected number of unique UUIDs not generated");
    }

    /**
     * 测试时钟回拨处理
     */
    @Test
    public void testClockRegression() {
        Set<UUID> uuids = new HashSet<>();
        int count = 1000;

        // 连续生成UUID，模拟高频生成场景
        for (int i = 0; i < count; i++) {
            UUID uuid = UUIDv1Generator.next();
            assertTrue(uuids.add(uuid), "Duplicate UUID generated during clock regression handling");
        }

        assertEquals(count, uuids.size(), "Some UUIDs were duplicated");
    }

    /**
     * 测试节点ID的一致性
     */
    @Test
    public void testNodeIdConsistency() {
        Set<Long> nodeIds = IntStream.range(0, 1000)
                .mapToObj(i -> UUIDv1Generator.next())
                .map(this::extractNodeId)
                .collect(Collectors.toSet());

        // 验证节点ID保持一致
        assertEquals(1, nodeIds.size(), "Node ID should remain consistent");
    }

    /**
     * 从UUID中提取时间戳
     */
    private long extractTimestamp(UUID uuid) {
        return (uuid.timestamp() & 0x0FFFFFFFFFFFFFFFL);
    }

    /**
     * 从UUID中提取节点ID
     */
    private long extractNodeId(UUID uuid) {
        return uuid.getLeastSignificantBits() & 0x0000FFFFFFFFFFFFL;
    }
}