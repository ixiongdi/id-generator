package icu.congee.id.base;

import java.time.Instant;

public class TimeUtils {
    /**
     * 获取当前时间自 Unix 纪元以来的纳秒数
     * @return 纳秒数
     */
    public static long getCurrentUnixNano() {
        Instant now = Instant.now();
        // 先获取秒数并转换为纳秒
        long secondsInNanos = now.getEpochSecond() * 1_000_000_000;
        // 再加上当前秒内的纳秒数
        long nanos = now.getNano();
        return secondsInNanos + nanos;
    }

    public static void main(String[] args) {
        long unixNano = getCurrentUnixNano();
        System.out.println("当前时间自 Unix 纪元以来的纳秒数: " + unixNano);
    }
}