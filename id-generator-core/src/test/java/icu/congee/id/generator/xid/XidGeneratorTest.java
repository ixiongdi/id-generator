package icu.congee.id.generator.xid;

import icu.congee.id.base.IdType;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Xid生成器的单元测试类
 * <p>
 * 根据测试规范，测试包括：
 * - 唯一性测试：验证生成的ID在大量样本中不重复
 * - 格式正确性测试：验证生成的ID符合Xid规范
 * - 单调性测试：验证生成的ID具有单调递增特性
 * - 算法一致性测试：验证实现符合规范
 * </p>
 */
class XidGeneratorTest {
    private static final int TEST_ITERATIONS = 100_000; // 生成10万个Xid进行测试
    private static final int THREAD_COUNT = 16; // 并发测试的线程数
    private static final Pattern XID_PATTERN = Pattern
            .compile("^[0-9a-v]{20}$"); // Xid使用base32hex编码，字符范围是0-9和a-v

    private final XidGenerator generator = new XidGenerator();

    /**
     * 测试Xid生成器的唯一性
     * <p>
     * 该测试生成大量Xid，并确保没有重复
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
     * 测试Xid的格式正确性
     * <p>
     * 验证生成的Xid符合格式规范：
     * - 20个字符长度
     * - 使用base32hex编码（字符范围是0-9和a-v）
     * </p>
     */
    @Test
    void shouldMatchXidFormat() {
        for (int i = 0; i < 1000; i++) {
            String xidString = (String) generator.generate();

            // 检查Xid格式
            assertTrue(XID_PATTERN.matcher(xidString).matches(),
                    "Xid格式不符合规范: " + xidString);

            // 检查长度
            assertEquals(20, xidString.length(), "Xid长度不是20个字符: " + xidString);
        }
    }

    /**
     * 测试Xid的单调递增特性
     * <p>
     * Xid应该是基于时间的，具有单调递增特性
     * </p>
     */
    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        Xid previousXid = Xid.get();

        for (int i = 0; i < 1000; i++) {
            Xid currentXid = Xid.get();

            // 比较Xid的时间戳部分
            int previousTimestamp = previousXid.getTimestamp();
            int currentTimestamp = currentXid.getTimestamp();

            assertTrue(currentTimestamp >= previousTimestamp,
                    "Xid未保持单调递增特性");

            previousXid = currentXid;
        }
    }

    /**
     * 测试Xid的算法一致性
     * <p>
     * 验证Xid的结构符合规范：
     * - 4字节时间戳
     * - 5字节随机值
     * - 3字节计数器
     * </p>
     */
    @Test
    void shouldFollowXidSpecification() {
        Xid xid = Xid.get();

        // 检查时间戳部分
        int timestamp = xid.getTimestamp();
        assertTrue(timestamp > 0, "时间戳应该大于0");

        // 检查生成的ID长度为20个字符（base32hex编码后）
        String xidString = xid.toString();
        assertEquals(20, xidString.length(), "Xid字符串长度应为20");

        // 检查字符集是否符合base32hex编码（0-9, a-v）
        assertTrue(XID_PATTERN.matcher(xidString).matches(),
                "Xid不符合base32hex编码规范");
    }

    /**
     * 测试并发环境下Xid的唯一性
     * <p>
     * 在多线程环境下生成Xid，确保没有重复
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
                String id = Xid.string();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复Xid: " + id);
                return id;
            });
        }

        // 等待所有任务完成
        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<String> future = completionService.take();
            assertNotNull(future.get(), "Xid生成失败");
        }

        executor.shutdown();
        assertTrue(executor.awaitTermination(1, TimeUnit.MINUTES), "执行器未能在预期时间内终止");
    }

    /**
     * 测试IdType返回值是否正确
     */
    @Test
    void shouldReturnCorrectIdType() {
        assertEquals(IdType.XID, generator.idType(), "返回的IdType不正确");
    }
}