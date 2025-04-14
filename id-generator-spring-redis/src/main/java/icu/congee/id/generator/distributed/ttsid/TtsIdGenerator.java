package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class TtsIdGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdThreadLocalHolder> threadLocalHolder;

    public TtsIdGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdGenerator:threadId");

        threadLocalHolder = ThreadLocal.withInitial(() -> {
            long currentThreadId = threadId.getAndIncrement();
            return new TtsIdThreadLocalHolder((short) currentThreadId, (short) 0);
        });
    }


    @Override
    public TtsId generate() {
        TtsIdThreadLocalHolder holder = threadLocalHolder.get();
        return new TtsId(TtsId.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId;
    }

    @AllArgsConstructor
    private static class TtsIdThreadLocalHolder {
        short threadId;
        short sequence;
    }
}
