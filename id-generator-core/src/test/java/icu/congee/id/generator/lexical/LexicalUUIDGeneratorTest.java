package icu.congee.id.generator.lexical;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * LexicalUUID生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合LexicalUUID规范
 * - 单调递增性测试：验证生成的ID具有单调递增特性
 * - 并发测试：验证在多线程环境下的唯一性
 * </p>
 */
class LexicalUUIDGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个ID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern UUID_PATTERN = Pattern
            .compile("^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{16}$");

    private final LexicalUUIDGenerator generator = new LexicalUUIDGenerator();

    /**
     * 测试LexicalUUID生成器的唯一性
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
     * 测试LexicalUUID的格式正确性
     * <p>
     * 验证生成的ID符合LexicalUUID的格式规范
     * </p>
     */
    @Test
    void shouldMatchLexicalUUIDFormat() {
        for (int i = 0; i < 1000; i++) {
            String id = (String) generator.generate();
            assertNotNull(id, "生成的ID不能为空");
            assertTrue(UUID_PATTERN.matcher(id).matches(),
                    "ID不符合LexicalUUID格式: " + id);
        }
    }

    /**
     * 测试LexicalUUID的单调递增特性
     * <p>
     * LexicalUUID应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ids.add((String) generator.generate());
            // 短暂延迟确保时间戳变化
            if (i % 100 == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        // 验证生成的ID是否按时间顺序排列
        for (int i = 1; i < ids.size(); i++) {
            assertTrue(ids.get(i).compareTo(ids.get(i - 1)) >= 0,
                    "ID未保持单调递增特性");
        }
    }

    /**
     * 测试LexicalUUID的结构一致性
     * <p>
     * 验证LexicalUUID的结构符合规范：
     * - 64位时间戳
     * - 64位工作节点ID
     * </p>
     */
    @Test
    void shouldFollowLexicalUUIDSpecification() {
        // 生成多个ID，检查其结构
        for (int i = 0; i < 10; i++) {
            String id = (String) generator.generate();
            assertNotNull(id, "生成的ID不能为空");

            // 验证格式
            String[] parts = id.split("-");
            assertEquals(3, parts.length, "LexicalUUID应包含3个分隔符");
            assertEquals(8, parts[0].length(), "第一部分长度应为8");
            assertEquals(4, parts[1].length(), "第二部分长度应为4");
            assertEquals(4, parts[2].length(), "第三部分长度应为4");
            assertEquals(16, parts[3].length(), "第四部分长度应为16");
        }
    }

    /**
     * 测试并发环境下LexicalUUID的唯一性
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
     * 测试IdType返回值是否正确
     */
    @Test
    void shouldReturnCorrectIdType() {
        assertEquals(IdType.LexicalUUID, generator.idType(), "返回的IdType不正确");
    }
}