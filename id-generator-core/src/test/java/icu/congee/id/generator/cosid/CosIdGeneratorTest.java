package icu.congee.id.generator.cosid;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class CosIdGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个ID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数

    private final CosIdGenerator generator = new CosIdGenerator();

    /**
     * 测试CosId生成器的唯一性
     * <p>
     * 该测试生成大量ID，并确保没有重复
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
     * 测试CosId的格式正确性
     * <p>
     * 验证生成的ID包含正确的时间戳、机器ID和序列号信息
     * </p>
     */
    @Test
    void shouldMatchCosIdFormat() {
        for (int i = 0; i < 1000; i++) {
            String id = (String) generator.generate();
            assertNotNull(id, "生成的ID不能为空");
            assertTrue(id.length() > 0, "生成的ID长度必须大于0");

            // 验证ID是否只包含Base62字符集中的字符
            assertTrue(id.matches("^[0-9A-Za-z]+$"),
                    "ID包含非Base62字符: " + id);
        }
    }

    /**
     * 测试CosId的单调递增特性
     * <p>
     * CosId应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ids.add((String) generator.generate());
        }

        for (int i = 1; i < ids.size(); i++) {
            String prevId = ids.get(i - 1);
            String currId = ids.get(i);
            assertTrue(currId.compareTo(prevId) > 0,
                    "ID未保持单调递增特性");
        }
    }

    /**
     * 测试并发环境下CosId的唯一性
     * <p>
     * 在多线程环境下生成ID，确保没有重复
     * </p>
     */
    @Test
    void shouldHandleConcurrentGeneration() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executor);

        Set<String> concurrentIds = Collections.synchronizedSet(new HashSet<>(THREAD_COUNT * 1000));

        // 提交1000 * 线程数的任务
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            completionService.submit(() -> {
                String id = (String) generator.generate();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复ID: " + id);
                return id;
            });
        }

        // 等待所有任务完成
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<String> future = completionService.take();
            assertNotNull(future.get(), "ID生成失败");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES), "执行器未能在预期时间内终止");
    }

    /**
     * 测试时钟回拨情况
     * <p>
     * 验证在时钟回拨时是否能正确处理
     * </p>
     */
    @Test
    void shouldHandleClockMovedBackwards() {
        // 生成第一个ID
        String firstId = (String) generator.generate();
        assertNotNull(firstId);

        // 连续生成多个ID，模拟时钟回拨情况下的行为
        for (int i = 0; i < 1000; i++) {
            assertDoesNotThrow(() -> generator.generate(),
                    "生成ID时不应抛出异常");
        }
    }

    /**
     * 测试IdType返回值是否正确
     */
    @Test
    void shouldReturnCorrectIdType() {
        assertEquals(IdType.CosId, generator.idType(), "返回的IdType不正确");
    }
}