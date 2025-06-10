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
        RAtomicLong atomicLong =
                redisson.getAtomicLong("IdGenerator:TtsIdProMaxGenerator:threadId");
        if (!atomicLong.isExists()) {
            atomicLong.set(0);
        }
        threadLocalHolder =
                ThreadLocal.withInitial(
                        () -> {
                            long andIncrement = atomicLong.getAndIncrement();
                            if (andIncrement >= Integer.MAX_VALUE) {
                                atomicLong.set(0);
                            }
                            return new TtsIdProMaxThreadLocalHolder((int) andIncrement, 0);
                        });
    }

    @Override
    public TtsIdProMax generate() {
        TtsIdProMaxThreadLocalHolder holder = threadLocalHolder.get();
        int sequence = holder.sequence++;
        if (sequence == Integer.MAX_VALUE) {
            holder.sequence = 0;
        }
        return new TtsIdProMax(TtsIdProMax.currentTimestamp(), holder.threadId, sequence);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId;
    }

    @AllArgsConstructor
    private static class TtsIdProMaxThreadLocalHolder {
        final int threadId;
        int sequence;
    }
}
