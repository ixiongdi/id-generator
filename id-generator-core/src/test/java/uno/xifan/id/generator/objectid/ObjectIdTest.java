package uno.xifan.id.generator.objectid;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class ObjectIdTest {

    @Test
    void testObjectIdFormat() {
        ObjectId objectId = new ObjectId();
        String hexString = objectId.toHexString();

        // 验证长度为24个字符
        assertEquals(24, hexString.length());

        // 验证是否都是十六进制字符
        assertTrue(ObjectId.isValid(hexString));
    }

    @Test
    void testObjectIdUniqueness() {
        Set<String> ids = new HashSet<>();
        int count = 1000;

        for (int i = 0; i < count; i++) {
            ObjectId objectId = new ObjectId();
            String hexString = objectId.toHexString();
            assertTrue(ids.add(hexString), "Generated duplicate ObjectId: " + hexString);
        }

        assertEquals(count, ids.size());
    }

    @Test
    void testTimestampGeneration() {
        Date now = new Date();
        ObjectId objectId = new ObjectId(now);

        // 验证时间戳部分是否正确
        assertEquals(now.getTime() / 1000, objectId.getTimestamp());
    }

    @Test
    void testCounterIncrement() {
        ObjectId id1 = new ObjectId();
        ObjectId id2 = new ObjectId();

        // 验证计数器是否递增
        assertTrue(id2.compareTo(id1) > 0, "Second ObjectId should be greater than first");
    }

    @Test
    void testConcurrentGeneration() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        Set<String> ids = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        ObjectId objectId = new ObjectId();
                        synchronized (ids) {
                            ids.add(objectId.toHexString());
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(threadCount * idsPerThread, ids.size(), "Duplicate ObjectIds generated in concurrent environment");
    }

    @Test
    void testIsValid() {
        // 有效的ObjectId
        assertTrue(ObjectId.isValid("123456789012345678901234"));

        // 无效的ObjectId
        assertFalse(ObjectId.isValid("12345"));
        assertFalse(ObjectId.isValid("123456789012345678901234567890"));
        assertFalse(ObjectId.isValid("12345678901234567890123g"));
    }

    @Test
    void testObjectIdComparison() {
        Date earlier = new Date(System.currentTimeMillis() - 1000);
        Date later = new Date(System.currentTimeMillis());

        ObjectId id1 = new ObjectId(earlier);
        ObjectId id2 = new ObjectId(later);

        assertTrue(id1.compareTo(id2) < 0, "Earlier ObjectId should be less than later ObjectId");
    }

    @Test
    void testByteArrayConversion() {
        ObjectId original = new ObjectId();
        byte[] bytes = original.toByteArray();
        ObjectId fromBytes = new ObjectId(bytes);

        assertEquals(original, fromBytes, "ObjectId should be equal after byte array conversion");
    }

    @Test
    void testHexStringConversion() {
        ObjectId original = new ObjectId();
        String hexString = original.toHexString();
        ObjectId fromHexString = new ObjectId(hexString);

        assertEquals(original, fromHexString, "ObjectId should be equal after hex string conversion");
    }
}