package icu.congee.id.generator.distributed.ttsid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

@Component
public class TtsIdPlusGenerator implements IdGenerator {

    private final ThreadLocal<TtsIdPlusThreadLocalHolder> threadLocalHolder;
    private final ExecutorService executorService;

    public TtsIdPlusGenerator(RedissonClient redisson) {
        RAtomicLong threadId = redisson.getAtomicLong("IdGenerator:TtsIdPlusGenerator:threadId");

        threadLocalHolder =
                ThreadLocal.withInitial(
                        () -> new TtsIdPlusThreadLocalHolder((int) threadId.getAndIncrement()));
        
        // 初始化线程池，可根据需要调整线程数量
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
    }

    @Override
    public TtsIdPlus generate() {
            try {
                return executorService.submit(() -> {
                    TtsIdPlusThreadLocalHolder holder = threadLocalHolder.get();
                    return new TtsIdPlus(TtsIdPlus.currentTimestamp(), holder.threadId, holder.sequence++);
                }).get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
    }

    @Override
    public IdType idType() {
        return IdType.TtsId; // 使用现有的TtsId类型，如果需要可以在IdType中添加新的类型
    }

    private static class TtsIdPlusThreadLocalHolder {
        private final int threadId;
        private short sequence;

        private TtsIdPlusThreadLocalHolder(int threadId) {
            this.threadId = threadId;
        }
    }
}
