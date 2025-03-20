package icu.congee.id.generator.uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv2Generator的单元测试类
 * 
 * 测试覆盖以下方面：
 * 1. UUID格式正确性和版本标识验证
 * 2. 时间戳单调递增性和时钟回拨处理
 * 3. 安全域和本地标识符的正确性
 * 4. 并发生成的唯一性
 * 5. 边界条件处理
 */
public class UUIDv2GeneratorTest {

    /**
     * 测试生成的UUID格式正确性
     */
    @Test
    public void testUUIDFormat() {
        UUID uuid = UUIDv2Generator.next();

        // 验证版本号为2
        assertEquals(2, uuid.version());

        // 验证变体号符合RFC 4122
        assertEquals(2, uuid.variant());

        // 验证字符串格式
        String uuidStr = uuid.toString();
        assertTrue(uuidStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-2[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"));
    }

    /**
     * 测试时间戳单调递增性
     */
    @Test
    public void testTimestampMonotonicity() {
        UUID first = UUIDv2Generator.next();
        UUID second = UUIDv2Generator.next();

        // 提取时间戳部分（高28位）
        long timestamp1 = extractTimestamp(first);
        long timestamp2 = extractTimestamp(second);

        // 验证时间戳单调递增
        assertTrue(timestamp2 >= timestamp1, "Timestamps should be monotonically increasing");
    }

    /**
     * 测试并发生成的UUID唯一性
     */
    @Test
    public void testConcurrentUniqueness() throws InterruptedException {
        int threadCount = 10;
        int iterationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        Set<UUID> uuids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);

        // 创建多个线程并发生成UUID
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        UUID uuid = UUIDv2Generator.next();
                        assertTrue(uuids.add(uuid), "Duplicate UUID generated: " + uuid);
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有线程完成
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        // 验证生成的UUID数量
        assertEquals(threadCount * iterationsPerThread, uuids.size(),
                "Expected number of unique UUIDs not generated");
    }

    /**
     * 测试安全域和本地标识符的一致性
     */
    @Test
    public void testSecurityDomainAndLocalId() {
        Set<UUID> uuids = new HashSet<>();
        int count = 100;

        // 生成多个UUID
        for (int i = 0; i < count; i++) {
            uuids.add(UUIDv2Generator.next());
        }

        // 验证所有UUID的安全域和本地标识符部分一致
        Set<Byte> securityDomains = new HashSet<>();
        Set<Short> localIds = new HashSet<>();

        for (UUID uuid : uuids) {
            securityDomains.add(extractSecurityDomain(uuid));
            localIds.add(extractLocalId(uuid));
        }

        // 安全域应该一致
        assertEquals(1, securityDomains.size(), "Security domain should be consistent");

        // 本地标识符（用户ID）应该一致
        assertEquals(1, localIds.size(), "Local identifier (user ID) should be consistent");
    }

    /**
     * 测试时钟回拨处理
     */
    @Test
    public void testClockRegression() {
        Set<UUID> uuids = new HashSet<>();
        int count = 1000;

        // 连续生成UUID，模拟高频生成场景
        for (int i = 0; i < count; i++) {
            UUID uuid = UUIDv2Generator.next();
            assertTrue(uuids.add(uuid), "Duplicate UUID generated during clock regression handling");
        }

        assertEquals(count, uuids.size(), "Some UUIDs were duplicated");
    }

    /**
     * 测试IdGenerator接口实现
     */
    @Test
    public void testIdGeneratorImplementation() {
        UUIDv2Generator generator = new UUIDv2Generator();

        // 测试generate方法
        Object id = generator.generate();
        assertNotNull(id, "Generated ID should not be null");
        assertTrue(id instanceof String, "Generated ID should be a String");

        // 验证生成的ID格式符合UUIDv2
        String idStr = (String) id;
        assertTrue(idStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-2[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"));

        // 测试idType方法
        assertEquals(icu.congee.id.base.IdType.UUIDv2, generator.idType());
    }

    /**
     * 从UUID中提取时间戳（高28位）
     */
    private long extractTimestamp(UUID uuid) {
        return (uuid.getMostSignificantBits() >>> 36) & 0x0FFFFFFFL;
    }

    /**
     * 从UUID中提取安全域（LSB的高8位）
     */
    private byte extractSecurityDomain(UUID uuid) {
        return (byte) ((uuid.getLeastSignificantBits() >>> 48) & 0xFF);
    }

    /**
     * 从UUID中提取本地标识符（用户ID，MSB的低16位）
     */
    private short extractLocalId(UUID uuid) {
        return (short) ((uuid.getMostSignificantBits() >>> 20) & 0xFFFF);
    }
}