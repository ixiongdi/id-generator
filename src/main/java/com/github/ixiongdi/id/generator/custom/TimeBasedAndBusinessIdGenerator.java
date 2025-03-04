package com.github.ixiongdi.id.generator.custom;

import com.github.ixiongdi.id.generator.NumberIdGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeBasedAndBusinessIdGenerator implements NumberIdGenerator {
    // 定义日期时间格式化器，将当前时间格式化为 "yyMMddHHmmss" 格式的字符串
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyMMddHHmmss");
    // 用于记录同一时间戳内生成的 ID 数量，使用 AtomicInteger 保证线程安全
    private static final AtomicInteger COUNTER = new AtomicInteger(0);
    // 用于生成 4 位序列号，使用 AtomicInteger 保证线程安全
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);

    /**
     * 生成下一个唯一的数字 ID
     *
     * @return 生成的唯一数字 ID
     */
    public static Long next() {
        // 获取当前时间，并按照指定格式进行格式化
        String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        // 获取当前的计数器值
        int currentCounterValue = COUNTER.get();

        // 当计数器达到 9999 时，需要等待时间戳发生变化
        if (currentCounterValue >= 9999) {
            String newTime;
            // 循环等待，直到时间戳发生变化
            do {
                newTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            } while (newTime.equals(currentTime));

            // 时间戳变化后，将计数器重置为 0
            COUNTER.set(0);
            // 更新当前时间为新的时间戳
            currentTime = newTime;
        }

        // 获取当前的序列号，并将序列号加 1
        int currentSequenceValue = SEQUENCE.getAndIncrement();

        // 当序列号达到 9999 时，将序列号重置为 0
        if (currentSequenceValue >= 9999) {
            SEQUENCE.set(0);
        }

        // 计数器加 1
        COUNTER.incrementAndGet();

        // 将格式化后的时间戳和 4 位序列号拼接成字符串，并转换为 Long 类型返回
        return Long.parseLong(String.format("%s%04d", currentTime, currentSequenceValue));
    }

    /**
     * 实现 NumberIdGenerator 接口的 generate 方法，调用 next 方法生成唯一的数字 ID
     *
     * @return 生成的唯一数字 ID
     */
    @Override
    public Number generate() {
        return next();
    }
}
