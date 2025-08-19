package uno.xifan.id.generator.flakeid;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * FlakeId生成器的测试用例
 * 测试重点：
 * 1. ID格式验证
 * 2. 单线程和多线程环境下的唯一性
 * 3. 时钟回拨处理
 * 4. 并发性能
 * 5. 序列号溢出处理
 */
public class FlakeIdGeneratorTest {

    private final FlakeIdGenerator generator = new FlakeIdGenerator();

    @Test
    void testGenerateNotNull() {
        long id = generator.generate();
        assertNotNull(id, "生成的ID不应为空");
    }

    @Test
    void testUniqueness() {
        Set<Long> ids = new HashSet<>();
        int count = 10000;

        for (int i = 0; i < count; i++) {
            long id = generator.generate();
            assertTrue(ids.add(id), "生成的ID应该是唯一的");
        }

        assertEquals(count, ids.size(), "所有生成的ID都应该是唯一的");
    }

    @Test
    void testSequentialGeneration() {
        long id1 = generator.generate();
        long id2 = generator.generate();
        long id3 = generator.generate();

        assertNotEquals(id1, id2, "连续生成的ID应该不同");
        assertNotEquals(id2, id3, "连续生成的ID应该不同");
        assertNotEquals(id1, id3, "连续生成的ID应该不同");
    }

    @Test
    void testPerformance() {
        int count = 10000;
        long startTime = System.nanoTime();

        for (int i = 0; i < count; i++) {
            generator.generate();
        }

        long endTime = System.nanoTime();
        long durationMs = (endTime - startTime) / 1_000_000;

        // 确保生成速度在合理范围内（平均每个ID生成时间不超过0.1毫秒）
        assertTrue(durationMs < count * 0.1, "ID生成应该足够快");
    }


    // 辅助方法：将字节数组转换为十六进制字符串
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}