package icu.congee.id.generator.clock;

import icu.congee.id.generator.util.time.NtpClient;

import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Clock {

    private static Instant now;

    private static final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    static {
        sync();
        scheduler.scheduleWithFixedDelay(Clock::sync, 64, 1024, TimeUnit.SECONDS);
    }

    public static void sync() {
        try {
            now = NtpClient.getTime("pool.ntp.org");
        } catch (Exception e) {
            now = Instant.now();
        }
    }

    // 返回unix纳秒时间戳
    public static long currentTimeNanos() {
        return now.getEpochSecond() * 1000_000_000L + now.getNano();
    }
}
