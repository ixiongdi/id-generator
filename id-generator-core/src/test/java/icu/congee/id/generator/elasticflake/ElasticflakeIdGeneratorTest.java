package icu.congee.id.generator.elasticflake;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ElasticFlake生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合ElasticFlake规范
 * - 安全性测试：验证生成的ID具有足够的随机性
 * - 并发测试：验证在多线程环境下的唯一性
 * </p>
 */
class ElasticflakeIdGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个ID进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/=]+$");

    private final ElasticflakeIdGenerator generator = new ElasticflakeIdGenerator();

    /**
     * 测试ElasticFlake生成器的唯一性
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
     * 测试ElasticFlake的格式正确性
     * <p>
     * 验证生成的ID符合ElasticFlake的格式规范
     * </p>
     */
    @Test
    void shouldMatchElasticFlakeFormat() {
        for (int i = 0; i < 1000; i++) {
            String id = generator.generate();
            assertNotNull(id, "生成的ID不能为空");
            assertTrue(id.length() > 0, "生成的ID长度必须大于0");

            // 验证ID是否符合Base64编码格式
            assertTrue(BASE64_PATTERN.matcher(id).matches(),
                    "ID不符合Base64编码格式: " + id);
        }
    }

    /**
     * 测试ElasticFlake的安全性
     * <p>
     * 验证生成的ID具有足够的随机性和不可预测性
     * </p>
     */
    @Test
    void shouldGenerateSecureIds() {
        Set<Integer> lengths = new HashSet<>();
        Set<Character> firstChars = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            String id = generator.generate();
            lengths.add(id.length());
            firstChars.add(id.charAt(0));
        }

        // 验证ID长度的一致性
        assertEquals(1, lengths.size(), "ID长度不一致");

        // 验证首字符的分布
        assertTrue(firstChars.size() > 1, "首字符分布不够随机");
    }

    /**
     * 测试ElasticFlake的单调递增特性
     * <p>
     * ElasticFlake应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        List<String> ids = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            ids.add(generator.generate());
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
            // 由于Base64编码的特性，可以直接比较字符串
            assertTrue(ids.get(i).compareTo(ids.get(i - 1)) >= 0,
                    "ID未保持单调递增特性");
        }
    }

    /**
     * 测试并发环境下ElasticFlake的唯一性
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
                String id = generator.generate();
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
        assertEquals(IdType.ElasticFlake, generator.idType(), "返回的IdType不正确");
    }
}