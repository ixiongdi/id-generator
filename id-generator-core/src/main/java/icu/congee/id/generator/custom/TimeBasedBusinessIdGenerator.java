package icu.congee.id.generator.custom;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class TimeBasedBusinessIdGenerator implements IdGenerator {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyMMddHHmmss")
            .withZone(ZoneId.systemDefault());
    private static final int MAX_SEQUENCE = 9999;
    private static final AtomicInteger sequence = new AtomicInteger(0);
    private static volatile long lastTimestamp = -1L;

    /**
     * 此ID组成结构：
     * 前12位数为时间戳格式化为：yyMMddHHmmss
     * 后4位数为：0-9999
     * 时间回拨采用上一次的时间
     * 序号超过9999停止发号，等待进入下一秒
     * @return 生成的业务ID
     */
    public static synchronized Long next() {
        long currentTimestamp = System.currentTimeMillis();
        String timestampStr;
        
        // 处理时间回拨问题
        if (currentTimestamp < lastTimestamp) {
            // 时间回拨，使用上一次的时间
            currentTimestamp = lastTimestamp;
        }
        
        // 格式化时间戳
        Instant instant = Instant.ofEpochMilli(currentTimestamp);
        timestampStr = FORMATTER.format(instant);
        
        // 如果是同一秒内，序列号递增
        if (currentTimestamp / 1000 == lastTimestamp / 1000) {
            int currentSequence = sequence.incrementAndGet();
            
            // 序列号超过最大值，等待进入下一秒
            if (currentSequence > MAX_SEQUENCE) {
                // 阻塞到下一秒
                do {
                    currentTimestamp = System.currentTimeMillis();
                } while (currentTimestamp / 1000 == lastTimestamp / 1000);
                
                // 重置序列号并更新时间戳
                sequence.set(0);
                lastTimestamp = currentTimestamp;
                instant = Instant.ofEpochMilli(currentTimestamp);
                timestampStr = FORMATTER.format(instant);
            }
        } else {
            // 时间戳改变，重置序列号
            sequence.set(0);
            lastTimestamp = currentTimestamp;
        }
        
        // 组合ID：时间戳(12位) + 序列号(4位，不足4位前面补0)
        String sequenceStr = String.format("%04d", sequence.get());
        return Long.parseLong(timestampStr + sequenceStr);
    }

    @Override
    public Long generate() {
        return next();
    }

    @Override
    public IdType idType() {
        return IdType.TimeBasedBusinessId;
    }
}
