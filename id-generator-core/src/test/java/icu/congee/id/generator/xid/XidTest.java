package icu.congee.id.generator.xid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.HashSet;
import java.util.Set;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@Execution(ExecutionMode.CONCURRENT)
class XidTest {

    @Test
    void testXidFormat() {
        Xid xid = new Xid();
        String xidStr = xid.toString();
        assertEquals(20, xidStr.length(), "Xid should be 20 characters long");
        assertTrue(Xid.isValid(xidStr), "Generated Xid should be valid");
    }

    @Test
    void testXidUniqueness() {
        Set<String> xids = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            String xid = new Xid().toString();
            assertTrue(xids.add(xid), "Generated Xid should be unique");
        }
    }

    @Test
    void testXidWithCustomDate() {
        Date date = new Date();
        Xid xid = new Xid(date);
        assertNotNull(xid);
        assertTrue(Xid.isValid(xid.toString()));
    }

    @Test
    void testXidWithCustomCounter() {
        Date date = new Date();
        int counter = 123456;
        Xid xid = new Xid(date, counter);
        assertNotNull(xid);
        assertTrue(Xid.isValid(xid.toString()));
    }

    @Test
    void testInvalidXid() {
        assertFalse(Xid.isValid("invalid-xid"));
        assertFalse(Xid.isValid(""));
    }

    @RepeatedTest(5)
    void testConcurrentXidGeneration() throws InterruptedException {
        int threadCount = 10;
        int idsPerThread = 1000;
        Set<String> xids = ConcurrentHashMap.newKeySet();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < idsPerThread; j++) {
                        String xid = new Xid().toString();
                        assertTrue(xids.add(xid), "Concurrent Xid generation should produce unique IDs");
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        assertEquals(threadCount * idsPerThread, xids.size(), "All generated Xids should be unique");
    }

    @Test
    void testXidFromString() {
        Xid originalXid = new Xid();
        String xidStr = originalXid.toString();
        Xid parsedXid = new Xid(xidStr);
        assertEquals(originalXid.toString(), parsedXid.toString(), "Parsed Xid should match original");
    }

    @Test
    void testXidComparison() throws InterruptedException {
        Xid xid1 = new Xid();
        Thread.sleep(1); // Ensure different timestamp
        Xid xid2 = new Xid();
        assertTrue(xid1.compareTo(xid2) < 0, "Earlier Xid should be less than later Xid");
    }

    @Test
    void testXidEquality() {
        Xid xid1 = new Xid();
        Xid xid2 = new Xid(xid1.toString());
        assertEquals(xid1, xid2, "Xids with same value should be equal");
        assertNotEquals(xid1, new Xid(), "Different Xids should not be equal");
    }
}