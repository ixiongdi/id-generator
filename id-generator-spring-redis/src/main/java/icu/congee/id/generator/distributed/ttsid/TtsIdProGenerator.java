package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class TtsIdProGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdProThreadLocalHolder> threadLocalHolder;

    public TtsIdProGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdProGenerator:threadId");

        threadLocalHolder =
                ThreadLocal.withInitial(
                        () -> new TtsIdProThreadLocalHolder((int) threadId.getAndIncrement()));
    }

    @Override
    public TtsIdPro generate() {
        TtsIdProThreadLocalHolder holder = threadLocalHolder.get();
        return new TtsIdPro(TtsIdPro.currentTimestamp(), holder.threadId, holder.sequence++);
    }

    @Override
    public IdType idType() {
        return IdType.TtsId; // 使用现有的TtsId类型，如果需要可以在IdType中添加TtsIdPro类型
    }

    private static class TtsIdProThreadLocalHolder {
        private final int threadId;
        private short sequence;

        private TtsIdProThreadLocalHolder(int threadId) {
            this.threadId = threadId;
        }
    }
}
