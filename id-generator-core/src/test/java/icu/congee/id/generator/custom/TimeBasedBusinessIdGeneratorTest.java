package icu.congee.id.generator.custom;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class TimeBasedBusinessIdGeneratorTest {
    private static final int TEST_ITERATIONS = 1_000_000;
    private static final int THREAD_COUNT = 32;

    @Test
    void shouldGenerateUniqueIds() {
        Set<Long> idSet = Collections.synchronizedSet(new HashSet<>(TEST_ITERATIONS));

        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).submit(() -> {
            for (int i = 0; i < TEST_ITERATIONS; i++) {
                Long id = TimeBasedBusinessIdGenerator.next();
                assertTrue(idSet.add(id), "发现重复ID: " + id);
            }
            return null;
        });
    }

    @Test
    void shouldMatchFormatPattern() {
        Long id = TimeBasedBusinessIdGenerator.next();
        String idStr = String.valueOf(id);

        assertEquals(16, idStr.length(), "ID长度不符合要求");

        String timestampPart = idStr.substring(0, 12);
        String sequencePart = idStr.substring(12);

        assertDoesNotThrow(() -> LocalDateTime.parse(timestampPart,
                DateTimeFormatter.ofPattern("yyMMddHHmmss")), "时间戳格式错误");

        int sequence = Integer.parseInt(sequencePart);
        assertTrue(sequence >= 0 && sequence <= 9999, "序列号范围错误");
    }


    @Test
    void shouldGenerateMonotonicallyIncreasingIds() {
        Long previousId = TimeBasedBusinessIdGenerator.next();
        for (int i = 0; i < 1000; i++) {
            Long currentId = TimeBasedBusinessIdGenerator.next();
            assertTrue(currentId > previousId, "ID未保持单调递增");
            previousId = currentId;
        }
    }

    @Test
    void shouldHandleConcurrentGeneration() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CompletionService<Long> completionService = new ExecutorCompletionService<>(executor);

        Set<Long> concurrentIds = Collections.synchronizedSet(new HashSet<>(THREAD_COUNT * 1000));

        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            completionService.submit(() -> {
                Long id = TimeBasedBusinessIdGenerator.next();
                assertTrue(concurrentIds.add(id), "发现并发环境下的重复ID: " + id);
                return id;
            });
        }

        for (int i = 0; i < THREAD_COUNT * 1000; i++) {
            Future<Long> future = completionService.take();
            assertNotNull(future.get(), "ID生成失败");
        }
    }
}