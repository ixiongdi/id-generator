package icu.congee.id.generator.combguid;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

class CombGuidGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个ID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern UUID_PATTERN = Pattern
            .compile("^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$");

    private final CombGuidGenerator generator = new CombGuidGenerator();

    /**
     * 测试CombGuid生成器的唯一性
     * <p>
     * 该测试生成大量ID，并确保没有重复
     * </p>
     */
    @Test
    void shouldGenerateUniqueIds() {
        Set<String> idSet = Collections.synchronizedSet(new HashSet<>(TEST_ITERATIONS));

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            String id = generator.generate();
            assertTrue(idSet.add(id), "发现重复ID: " + id);
        }
    }

    /**
     * 测试CombGuid的格式正确性
     * <p>
     * 验证生成的ID符合UUID格式规范，并且包含正确的时间戳信息
     * </p>
     */
    @Test
    void shouldMatchCombGuidFormat() {
        for (int i = 0; i < 1000; i++) {
            String guidString = generator.generate();

            // 检查UUID格式
            assertTrue(UUID_PATTERN.matcher(guidString).matches(),
                    "UUID格式不符合规范: " + guidString);

            // 解析为UUID对象进行进一步检查
            UUID uuid = UUID.fromString(guidString);

            // 检查版本号（应为4）
            assertEquals(4, uuid.version(), "UUID版本号不是4: " + guidString);

            // 检查变体（应为2，即RFC 4122变体）
            assertEquals(2, uuid.variant(), "UUID变体不符合RFC 4122规范: " + guidString);

            // 提取时间戳并验证
            long timestamp = uuid.getMostSignificantBits() >>> 16;
            assertTrue(timestamp <= Instant.now().toEpochMilli(),
                    "时间戳大于当前时间: " + timestamp);
        }
    }

    /**
     * 测试CombGuid的单调递增特性
     * <p>
     * CombGuid应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        List<UUID> uuids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            uuids.add(CombGuidGenerator.next());
        }

        for (int i = 1; i < uuids.size(); i++) {
            long prevTimestamp = uuids.get(i - 1).getMostSignificantBits() >>> 16;
            long currTimestamp = uuids.get(i).getMostSignificantBits() >>> 16;
            assertTrue(currTimestamp >= prevTimestamp,
                    "ID未保持单调递增特性");
        }
    }

    /**
     * 测试并发环境下CombGuid的唯一性
     * <p>
     * 在多线程环境下生成ID，确保没有重复
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
                UUID id = CombGuidGenerator.next();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复ID: " + id);
                return id;
            });
        }

        // 等待所有任务完成
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<UUID> future = completionService.take();
            assertNotNull(future.get(), "ID生成失败");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES), "执行器未能在预期时间内终止");
    }

    /**
     * 测试IdType返回值是否正确
     */
    @Test
    void shouldReturnCorrectIdType() {
        assertEquals(IdType.COMBGUID, generator.idType(), "返回的IdType不正确");
    }
}