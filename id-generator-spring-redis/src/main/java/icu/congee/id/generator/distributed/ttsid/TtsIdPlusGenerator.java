package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class TtsIdPlusGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdPlusThreadLocalHolder> threadLocalHolder;

    public TtsIdPlusGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdPlusGenerator:threadId");

        threadLocalHolder = ThreadLocal.withInitial(() -> new TtsIdPlusThreadLocalHolder((int) threadId.getAndIncrement(), (short) 0));
    }

    @Override
    public TtsIdPlus generate() {
        TtsIdPlusThreadLocalHolder holder = threadLocalHolder.get();
        return new TtsIdPlus(TtsIdPlus.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId; // 使用现有的TtsId类型，如果需要可以在IdType中添加新的类型
    }

    @AllArgsConstructor
    private static class TtsIdPlusThreadLocalHolder {
        int threadId;
        short sequence;
    }
}