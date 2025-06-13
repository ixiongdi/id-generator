package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/** TtsId生成器实现 生成的ID结构：31位时间戳 + 10位线程ID + 12位序列号 */
@Component
public class TtsIdMiniGenerator implements IdGenerator {

    // 2^12 - 1
    /** 使用ThreadLocal存储线程ID和序列号，避免多线程冲突 */
    private final ThreadLocal<TtsIdThreadLocalHolder> threadLocalHolder;

    /**
     * 构造函数
     *
     * @param redisson Redis客户端，用于获取全局唯一的线程ID
     */
    public TtsIdMiniGenerator(RedissonClient redisson, TtsIdMiniGeneratorConfig config) {
        RAtomicLong threadId =
                redisson.getAtomicLong(
                        "IdGenerator:TtsIdMiniGenerator:%s:NextThreadId"
                                .formatted(config.getNamespace()));

        threadLocalHolder =
                ThreadLocal.withInitial(
                        () ->
                                new TtsIdThreadLocalHolder(
                                        Math.abs(threadId.getAndIncrement() % 1024)));
    }

    @Override
    public TtsIdMini generate() {
        TtsIdThreadLocalHolder holder = threadLocalHolder.get();
        return holder.next();
    }

    @Override
    public IdType idType() {
        return IdType.TtsId;
    }

    /** ThreadLocal持有的对象，存储线程ID和序列号 */
    private static final class TtsIdThreadLocalHolder {
        // 10bit 线程ID
        private final long threadId;
        // 12bit 序列号，取值范围 0-4095
        private long sequence = 0;

        private long lastTimestamp = TtsIdMini.currentTimestamp();

        private TtsIdThreadLocalHolder(long threadId) {
            this.threadId = threadId;
        }

        private TtsIdMini next() {
            long timestamp = TtsIdMini.currentTimestamp();
            if (timestamp < lastTimestamp) {
                timestamp = waitNextSecond(lastTimestamp);
            } else if (timestamp == lastTimestamp) {
                sequence++;
                if (sequence >= 4096) {
                    timestamp = waitNextSecond(lastTimestamp);
                    sequence = 0;
                }
            } else {
                sequence = 0;
            }
            lastTimestamp = timestamp;
            return new TtsIdMini(timestamp, threadId, sequence);
        }

        private long waitNextSecond(long lastTimestamp) {
            long timestamp = TtsIdMini.currentTimestamp();
            while (timestamp <= lastTimestamp) {
                timestamp = TtsIdMini.currentTimestamp();
            }
            return timestamp;
        }
    }
}
