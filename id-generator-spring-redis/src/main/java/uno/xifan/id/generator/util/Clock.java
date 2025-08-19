package uno.xifan.id.generator.util;

import uno.xifan.id.generator.util.time.NtpClient;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock {

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();
    private static long now = System.currentTimeMillis();

    static {
        sync();
        scheduler.scheduleWithFixedDelay(Clock::sync, 64, 1024, TimeUnit.SECONDS);
    }

    public static void sync() {
        Instant instant;
        try {
            instant = NtpClient.getTime("pool.ntp.org");
        } catch (Exception e) {
            instant = Instant.now();
        }
        now = instant.getEpochSecond() * 1000_000_000 + instant.getNano() + System.nanoTime();
    }

    // 返回unix纳秒时间戳
    public static long currentTimeNanos() {
        return now - System.nanoTime();
    }

    public static long currentTimeSeconds() {
        return currentTimeNanos() / 1_000_000_000L;
    }

    public static long currentTimeMicros() {
        return currentTimeNanos() / 1_000L;
    }

    public static long currentTimeMillis() {
        return currentTimeNanos() / 1_000_000L;
    }
}
