package icu.congee.id.generator.cosid;

import icu.congee.id.base.Base62Codec;
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.time.Instant;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicLong;

/**
 * CosId生成器实现
 * 基于时间戳、机器ID和序列号组合生成全局唯一ID
 */
public class CosIdGenerator implements IdGenerator {

    private static final long EPOCH = 1609459200000L; // 2021-01-01 00:00:00
    private static final int MACHINE_BITS = 20;
    private static final int SEQUENCE_BITS = 16;
    
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    private static final long MACHINE_LEFT_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS + MACHINE_BITS;
    
    private static final long machineId;
    private static final AtomicLong sequence;
    private static long lastTimestamp;
    
    static {
        // 这里简单使用随机数作为机器ID，实际应用中应该使用配置或其他方式获取
        machineId = (int) (Math.random() * (1L << MACHINE_BITS));
        sequence = new AtomicLong(0L);
        lastTimestamp = -1L;
    }

    
    public static synchronized String next() {
        long timestamp = timeGen();
        
        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id");
        }
        
        // 如果是同一时间生成的，则进行序列号递增
        if (lastTimestamp == timestamp) {
            sequence.set((sequence.get() + 1) & MAX_SEQUENCE);
            // 序列号已经达到最大值
            if (sequence.get() == 0) {
                // 阻塞到下一个毫秒，获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，序列重置
            sequence.set(0L);
        }
        
        lastTimestamp = timestamp;
        
        // 使用字节数组来支持80位的数值
        byte[] result = new byte[10]; // 80位 = 10字节
        
        // 写入时间戳部分 (44位)
        long timestampPart = timestamp - EPOCH;
        for (int i = 0; i < 6; i++) {
            result[i] = (byte) (timestampPart >> ((5 - i) * 8));
        }
        
        // 写入机器ID部分 (20位)
        long machinePart = machineId << MACHINE_LEFT_SHIFT;
        result[5] |= (byte) ((machinePart >> 16) & 0x0F);
        result[6] = (byte) (machinePart >> 8);
        result[7] = (byte) machinePart;
        
        // 写入序列号部分 (16位)
        long sequencePart = sequence.get();
        result[8] = (byte) (sequencePart >> 8);
        result[9] = (byte) sequencePart;
        
        // 将字节数组转换为URL安全的Base64编码字符串
        return Base62Codec.encode(result);
    }
    
    private static long timeGen() {
        return Instant.now().toEpochMilli();
    }
    
    private static long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }
}