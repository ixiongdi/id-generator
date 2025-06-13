package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class TtsIdProMaxGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdProMaxThreadLocalHolder> threadLocalHolder;

    public TtsIdProMaxGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdProMaxGenerator:threadId");
        threadLocalHolder =
                ThreadLocal.withInitial(
                        () -> new TtsIdProMaxThreadLocalHolder((int) threadId.getAndIncrement()));
    }

    @Override
    public TtsIdProMax generate() {
        TtsIdProMaxThreadLocalHolder holder = threadLocalHolder.get();
        return new TtsIdProMax(TtsIdProMax.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId;
    }

    private static class TtsIdProMaxThreadLocalHolder {
        private final int threadId;
        private int sequence;

        private TtsIdProMaxThreadLocalHolder(int threadId) {
            this.threadId = threadId;
        }
    }
}
