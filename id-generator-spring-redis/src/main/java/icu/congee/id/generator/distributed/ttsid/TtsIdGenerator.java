package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;


/**
 * TtsId生成器实现
 * 生成的ID结构：41位时间戳 + 10位线程ID + 12位序列号
 */
@Component
public class TtsIdGenerator implements IdGenerator {

    /**
     * 使用ThreadLocal存储线程ID和序列号，避免多线程冲突
     */
    private final ThreadLocal<TtsIdThreadLocalHolder> threadLocalHolder;

    /**
     * 序列号最大值（12位）
     */
    private static final short MAX_SEQUENCE = 4095; // 2^12 - 1

    /**
     * 构造函数
     * 
     * @param redisson Redis客户端，用于获取全局唯一的线程ID
     */
    public TtsIdGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdGenerator:threadId");

        threadLocalHolder = ThreadLocal
                .withInitial(() -> new TtsIdThreadLocalHolder((short) threadId.getAndIncrement(), (short) 0));
    }


    @Override
    public TtsId generate() {
        TtsIdThreadLocalHolder holder = threadLocalHolder.get();

        // 处理序列号溢出，当达到最大值时重置为0
        if (holder.sequence > MAX_SEQUENCE) {
            holder.sequence = 0;
        }

        return new TtsId(TtsId.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId;
    }

    /**
     * ThreadLocal持有的对象，存储线程ID和序列号
     */
    @AllArgsConstructor
    private static class TtsIdThreadLocalHolder {
        // 10bit 线程ID
        short threadId;
        // 12bit 序列号，取值范围 0-4095
        short sequence;
    }
}
