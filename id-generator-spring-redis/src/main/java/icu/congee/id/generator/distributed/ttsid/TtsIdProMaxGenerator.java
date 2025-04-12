package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class TtsIdProMaxGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdProMaxThreadLocalHolder> threadLocalHolder;

    public TtsIdProMaxGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdProMaxGenerator:threadId");

        threadLocalHolder = ThreadLocal.withInitial(() -> {
            long currentThreadId = threadId.getAndIncrement();
            return new TtsIdProMaxThreadLocalHolder((int) currentThreadId, 0);
        });
    }

    @Override
    public TtsIdProMax generate() {
        TtsIdProMaxThreadLocalHolder holder = threadLocalHolder.get();
        return new TtsIdProMax(TtsIdProMax.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId; // 使用现有的TtsId类型，如果需要可以在IdType中添加TtsIdProMax类型
    }

    @AllArgsConstructor
    private static class TtsIdProMaxThreadLocalHolder {
        int threadId;
        int sequence;
    }
}