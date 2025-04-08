package icu.congee.id.generator.ulid;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ULID生成器测试类
 * 
 * 测试内容包括：
 * - ID格式正确性测试（26个字符的Crockford's Base32编码）
 * - 唯一性测试
 * - 单调递增性测试
 * - 时间戳部分正确性测试
 * - 随机性测试
 * - 自定义随机数生成器测试
 * - 高并发环境下的唯一性测试
 * - reseed方法测试
 * - 性能测试
 */
@DisplayName("ULID生成器测试")
public class ULIDGeneratorTest {

    private ULIDGenerator generator;
    private static final Pattern ULID_PATTERN = Pattern.compile("^[0123456789ABCDEFGHJKMNPQRSTVWXYZ]{26}$");

    @BeforeEach
    void setUp() {
        generator = new ULIDGenerator();
    }

    @Test
    @DisplayName("测试生成的ID类型正确")
    void testIdType() {
        assertEquals(IdType.ULID, generator.idType(), "ID类型应为ULID");
    }

    @Test
    @DisplayName("测试生成的ID格式正确")
    void testIdFormat() {
        String id = generator.create();

        // 验证长度为26个字符
        assertEquals(26, id.length(), "ULID应该是26个字符");

        // 验证是否符合Crockford's Base32字符集
        assertTrue(ULID_PATTERN.matcher(id).matches(),
                "ULID应该只包含Crockford's Base32字符集中的字符");
    }

    @Test
    @DisplayName("测试ID的唯一性")
    void testIdUniqueness() {
        int count = 10000; // 生成10000个ID进行唯一性测试
        Set<String> idSet = new HashSet<>(count);

        for (int i = 0; i < count; i++) {
            String id = generator.create();
            assertFalse(idSet.contains(id), "生成的ID不应重复: " + id);
            idSet.add(id);
        }

        assertEquals(count, idSet.size(), "生成的所有ID应该唯一");
    }

    @Test
    @DisplayName("测试ID的单调递增性")
    void testIdMonotonicity() {
        String previousId = generator.next();

        // 生成100个ID并验证它们是单调递增的
        for (int i = 0; i < 100; i++) {
            String currentId = generator.next();
            assertTrue(currentId.compareTo(previousId) > 0,
                    "当前ID应大于前一个ID: 当前ID=" + currentId + ", 前一个ID=" + previousId);
            previousId = currentId;
        }
    }

    @Test
    @DisplayName("测试时间戳部分正确性")
    void testTimestampComponent() {
        String id = generator.create();
        long timestamp = ULIDGenerator.unixTime(id);
        long currentTime = System.currentTimeMillis();

        // 验证时间戳在合理范围内（允许1秒的误差）
        assertTrue(Math.abs(currentTime - timestamp) <= 1000,
                "ULID中的时间戳应该接近当前时间");
    }

    @Test
    @DisplayName("测试自定义随机数生成器")
    void testCustomRandomGenerator() {
        SecureRandom customRandom = new SecureRandom();
        ULIDGenerator customGenerator = new ULIDGenerator(customRandom);

        String id = customGenerator.create();
        assertTrue(ULID_PATTERN.matcher(id).matches(),
                "使用自定义随机数生成器生成的ULID应该符合格式要求");
    }

    @Test
    @DisplayName("测试高并发下的唯一性")
    void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        int totalIds = threadCount * idsPerThread;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> idSet = new HashSet<>(totalIds);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        synchronized (idSet) {
                            String id = generator.create();
                            assertFalse(idSet.contains(id), "并发生成的ID不应重复");
                            idSet.add(id);
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(30, TimeUnit.SECONDS), "并发测试应在30秒内完成");
        executor.shutdown();

        assertEquals(totalIds, idSet.size(), "并发生成的所有ID应该唯一");
    }

    @Test
    @DisplayName("测试ULID字符串验证")
    void testULIDValidation() {
        String validId = generator.create();
        assertTrue(ULIDGenerator.isValid(validId), "有效的ULID应该通过验证");

        // 测试无效的ULID
        assertFalse(ULIDGenerator.isValid(null), "null不应该是有效的ULID");
        assertFalse(ULIDGenerator.isValid(""), "空字符串不应该是有效的ULID");
        assertFalse(ULIDGenerator.isValid("invalid"), "格式错误的字符串不应该是有效的ULID");
        assertFalse(ULIDGenerator.isValid("0123456789ABCDEF"), "长度不正确的字符串不应该是有效的ULID");
    }

    @RepeatedTest(5)
    @DisplayName("测试生成性能")
    void testGenerationPerformance() {
        int count = 100000;
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            generator.create();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        System.out.printf("生成 %d 个ID耗时: %d ms, 平均每秒生成: %.2f 个ID%n",
                count, durationMs, count * 1000.0 / durationMs);

        // 性能断言：生成10万个ID应该在1秒内完成
        assertTrue(durationMs < 1000, "生成10万个ID应在1秒内完成");
    }
}