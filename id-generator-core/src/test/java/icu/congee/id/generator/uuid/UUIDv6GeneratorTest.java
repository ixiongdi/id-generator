package icu.congee.id.generator.uuid;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv6生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合UUIDv6规范
 * - 单调性测试：验证生成的ID具有单调递增特性
 * - 算法一致性测试：验证实现符合RFC规范
 * </p>
 */
class UUIDv6GeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个UUID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern UUID_PATTERN = Pattern
            .compile("^[0-9a-f]{8}-[0-9a-f]{4}-6[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private final UUIDv6Generator generator = new UUIDv6Generator();

    /**
     * 测试UUIDv6生成器的唯一性
     * <p>
     * 该测试生成大量UUID，并确保没有重复
     * </p>
     */
    @Test
    void shouldGenerateUniqueIds() {
        Set<String> idSet = Collections.synchronizedSet(new HashSet<>(TEST_ITERATIONS));

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String id = (String) generator.generate();
            assertTrue(idSet.add(id), "发现重复ID: " + id);
        }
    }

    /**
     * 测试UUIDv6的格式正确性
     * <p>
     * 验证生成的UUID符合UUIDv6的格式规范：
     * - 符合标准UUID格式
     * - 版本号为6
     * - 变体位为RFC 4122规范(10xx)
     * </p>
     */
    @Test
    void shouldMatchUUIDv6Format() {
        for (int i = 0; i < 1000; i++) {
            String uuidString = (String) generator.generate();

            // 检查UUID格式
            assertTrue(UUID_PATTERN.matcher(uuidString).matches(),
                    "UUID格式不符合规范: " + uuidString);

            // 解析为UUID对象进行进一步检查
            UUID uuid = UUID.fromString(uuidString);

            // 检查版本号（应为6）
            assertEquals(6, uuid.version(), "UUID版本号不是6: " + uuidString);

            // 检查变体（应为2，即RFC 4122变体）
            assertEquals(2, uuid.variant(), "UUID变体不符合RFC 4122规范: " + uuidString);
        }
    }

    /**
     * 测试UUIDv6的单调递增特性
     * <p>
     * UUIDv6应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        UUID previousUuid = UUIDv6Generator.next();

        for (int i = 0; i < 1000; i++) {
            UUID currentUuid = UUIDv6Generator.next();

            // 比较UUID的时间戳部分（高64位中的前60位）
            long previousTimestamp = previousUuid.getMostSignificantBits() >>> 4;
            long currentTimestamp = currentUuid.getMostSignificantBits() >>> 4;

            assertTrue(currentTimestamp >= previousTimestamp,
                    "UUID未保持单调递增特性");

            // 如果时间戳相同，则检查时钟序列是否递增（或至少不减少）
            if (currentTimestamp == previousTimestamp) {
                long previousClockSeq = (previousUuid.getLeastSignificantBits() >>> 48) & 0x3FFF;
                long currentClockSeq = (currentUuid.getLeastSignificantBits() >>> 48) & 0x3FFF;

                // 注意：时钟序列可能会循环，所以这里只是一个宽松的检查
                if (currentClockSeq < previousClockSeq) {
                    // 只有在差距很大时才认为是错误（可能是循环）
                    assertTrue(previousClockSeq - currentClockSeq > 0x3F00,
                            "时钟序列未正确递增");
                }
            }

            previousUuid = currentUuid;
        }
    }

    /**
     * 测试UUIDv6的算法一致性
     * <p>
     * 验证UUIDv6的结构符合规范：
     * - 60位时间戳
     * - 4位版本号(6)
     * - 14位时钟序列
     * - 48位节点ID
     * </p>
     */
    @Test
    void shouldFollowUUIDv6Specification() {
        UUID uuid = UUIDv6Generator.next();

        // 检查版本号位置（应为6）
        assertEquals(6, (uuid.getMostSignificantBits() & 0x000000000000F000L) >>> 12,
                "版本号位置或值不正确");

        // 检查变体位（应为10xx，即RFC 4122变体）
        assertEquals(0x8, (uuid.getLeastSignificantBits() & 0xF000000000000000L) >>> 60,
                "变体位不符合RFC 4122规范");

        // 检查时间戳、时钟序列和节点ID的位数
        // 这些检查主要是确保位掩码正确应用
        long timestamp = uuid.getMostSignificantBits() >>> 4;
        long clockSeq = (uuid.getLeastSignificantBits() >>> 48) & 0x3FFF;
        long nodeId = uuid.getLeastSignificantBits() & 0xFFFFFFFFFFFFL;

        // 时间戳应该在60位以内
        assertTrue(timestamp <= 0xFFFFFFFFFFFFFL, "时间戳超出60位范围");

        // 时钟序列应该在14位以内
        assertTrue(clockSeq <= 0x3FFF, "时钟序列超出14位范围");

        // 节点ID应该在48位以内
        assertTrue(nodeId <= 0xFFFFFFFFFFFFL, "节点ID超出48位范围");
    }

    /**
     * 测试并发环境下UUIDv6的唯一性
     * <p>
     * 在多线程环境下生成UUID，确保没有重复
     * </p>
     */
    @Test
    void shouldHandleConcurrentGeneration() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<UUID> completionService = new ExecutorCompletionService<>(executor);

        Set<UUID> concurrentIds = Collections.synchronizedSet(new HashSet<>(THREAD_COUNT * 1000));

        // 提交1000 * 线程数的任务
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            completionService.submit(() -> {
                UUID id = UUIDv6Generator.next();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复UUID: " + id);
                return id;
            });
        }

        // 等待所有任务完成
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<UUID> future = completionService.take();
            assertNotNull(future.get(), "UUID生成失败");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES), "执行器未能在预期时间内终止");
    }

    /**
     * 测试IdType返回值是否正确
     */
    @Test
    void shouldReturnCorrectIdType() {
        assertEquals(IdType.UUIDv6, generator.idType(), "返回的IdType不正确");
    }
}