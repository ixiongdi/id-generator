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
 * UUIDv7生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合UUIDv7规范
 * - 单调性测试：验证生成的ID具有单调递增特性
 * - 算法一致性测试：验证实现符合RFC规范
 * </p>
 */
class UUIDv7GeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个UUID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern UUID_PATTERN = Pattern
            .compile("^[0-9a-f]{8}-[0-9a-f]{4}-7[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private final UUIDv7Generator generator = new UUIDv7Generator();

    /**
     * 测试UUIDv7生成器的唯一性
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
     * 测试UUIDv7的格式正确性
     * <p>
     * 验证生成的UUID符合UUIDv7的格式规范：
     * - 符合标准UUID格式
     * - 版本号为7
     * - 变体位为RFC 4122规范(10xx)
     * </p>
     */
    @Test
    void shouldMatchUUIDv7Format() {
        for (int i = 0; i < 1000; i++) {
            String uuidString = (String) generator.generate();

            // 检查UUID格式
            assertTrue(UUID_PATTERN.matcher(uuidString).matches(),
                    "UUID格式不符合规范: " + uuidString);

            // 解析为UUID对象进行进一步检查
            UUID uuid = UUID.fromString(uuidString);

            // 检查版本号（应为7）
            assertEquals(7, uuid.version(), "UUID版本号不是7: " + uuidString);

            // 检查变体（应为2，即RFC 4122变体）
            assertEquals(2, uuid.variant(), "UUID变体不符合RFC 4122规范: " + uuidString);
        }
    }

    /**
     * 测试UUIDv7的单调递增特性
     * <p>
     * UUIDv7应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        UUID previousUuid = UUIDv7Generator.next();

        for (int i = 0; i < 1000; i++) {
            UUID currentUuid = UUIDv7Generator.next();

            // 比较UUID的时间戳部分（高64位中的前48位）
            long previousTimestamp = previousUuid.getMostSignificantBits() >>> 16;
            long currentTimestamp = currentUuid.getMostSignificantBits() >>> 16;

            assertTrue(currentTimestamp >= previousTimestamp,
                    "UUID未保持单调递增特性");

            previousUuid = currentUuid;
        }
    }

    /**
     * 测试UUIDv7的算法一致性
     * <p>
     * 验证UUIDv7的结构符合规范：
     * - 48位时间戳
     * - 4位版本号(7)
     * - 12位随机数
     * - 2位变体位
     * - 62位随机数
     * </p>
     */
    @Test
    void shouldFollowUUIDv7Specification() {
        UUID uuid = UUIDv7Generator.next();

        // 检查版本号位置（应为7）
        assertEquals(7, (uuid.getMostSignificantBits() & 0x000000000000F000L) >>> 12,
                "版本号位置或值不正确");

        // 检查变体位（应为10xx，即RFC 4122规范）
        long variant = (uuid.getLeastSignificantBits() & 0xF000000000000000L) >>> 60;
        assertEquals(0x2, variant >>> 2, "变体位不符合RFC 4122规范（前两位必须为10）");

        // 检查时间戳的位数
        long timestamp = uuid.getMostSignificantBits() >>> 16;

        // 时间戳应该在48位以内
        assertTrue(timestamp <= 0xFFFFFFFFFFFFL, "时间戳超出48位范围");

        // 检查随机数部分
        long randomBits1 = uuid.getMostSignificantBits() & 0xFFF;
        assertTrue(randomBits1 <= 0xFFF, "高位随机数超出12位范围");

        // 检查低位随机数（除去变体位的62位）
        long randomBits2 = uuid.getLeastSignificantBits() & 0x3FFFFFFFFFFFFFFFL;
        assertTrue(randomBits2 <= 0x3FFFFFFFFFFFFFFFL, "低位随机数超出62位范围");
    }

    /**
     * 测试并发环境下UUIDv7的唯一性
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
                UUID id = UUIDv7Generator.next();
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
        assertEquals(IdType.UUIDv7, generator.idType(), "返回的IdType不正确");
    }
}