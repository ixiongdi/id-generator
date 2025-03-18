package icu.congee.id.generator.flake;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Flake生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合Flake规范
 * - 单调递增性测试：验证生成的ID具有单调递增特性
 * - 并发测试：验证在多线程环境下的唯一性
 * </p>
 */
class FlakeIdGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个ID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数

    private final FlakeIdGenerator generator = new FlakeIdGenerator();

    /**
     * 测试Flake生成器的唯一性
     * <p>
     * 该测试生成大量ID，并确保没有重复
     * </p>
     */
    @Test
    void shouldGenerateUniqueIds() {
        Set<Long> idSet = Collections.synchronizedSet(new HashSet<>(TEST_ITERATIONS));

        for (int i = 0; i < TEST_ITERATIONS; i++) {
            Long id = generator.generate();
            assertTrue(idSet.add(id), "发现重复ID: " + id);
        }
    }

    /**
     * 测试Flake的格式正确性
     * <p>
     * 验证生成的ID符合Flake的格式规范
     * </p>
     */
    @Test
    void shouldMatchFlakeFormat() {
        for (int i = 0; i < 1000; i++) {
            Long id = generator.generate();
            assertNotNull(id, "生成的ID不能为空");
            assertTrue(id > 0, "生成的ID必须为正数");
        }
    }

    /**
     * 测试Flake的单调递增特性
     * <p>
     * Flake应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(generator.generate());
        }

        // 验证生成的ID是否按时间顺序排列
        for (int i = 1; i < ids.size(); i++) {
            assertTrue(ids.get(i) > ids.get(i - 1),
                    "ID未保持单调递增特性");
        }
    }

    /**
     * 测试Flake的结构一致性
     * <p>
     * 验证Flake ID的结构符合规范：
     * - 时间戳部分
     * - 工作节点ID部分
     * - 序列号部分
     * </p>
     */
    @Test
    void shouldFollowFlakeSpecification() {
        // 生成多个ID，提取时间戳部分
        List<Long> timestamps = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Long id = generator.generate();
            // 提取时间戳部分（最高位）
            long timestamp = id >>> (48 + 16); // 工作节点ID位数 + 序列号位数
            timestamps.add(timestamp);

            // 确保时间戳在合理范围内（2021年之后）
            long currentTimeMillis = System.currentTimeMillis();
            long epochDiff = currentTimeMillis - 1609459200000L; // 2021-01-01 00:00:00 UTC
            assertTrue(timestamp <= epochDiff, "时间戳超出当前时间范围");
            assertTrue(timestamp > 0, "时间戳必须为正数");
        }

        // 验证时间戳是递增或相等的
        for (int i = 1; i < timestamps.size(); i++) {
            assertTrue(timestamps.get(i) >= timestamps.get(i - 1),
                    "时间戳部分未保持递增特性");
        }
    }

    /**
     * 测试并发环境下Flake的唯一性
     * <p>
     * 在多线程环境下生成ID，确保没有重复
     * </p>
     */
    @Test
    void shouldHandleConcurrentGeneration() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<Long> completionService = new ExecutorCompletionService<>(executor);

        Set<Long> concurrentIds = Collections.synchronizedSet(new HashSet<>(THREAD_COUNT * 1000));

        // 提交1000 * 线程数的任务
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            completionService.submit(() -> {
                Long id = generator.generate();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复ID: " + id);
                return id;
            });
        }

        // 等待所有任务完成
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<Long> future = completionService.take();
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
        assertEquals(IdType.Flake, generator.idType(), "返回的IdType不正确");
    }
}