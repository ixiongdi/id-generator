package uno.xifan.id.generator.wxseq;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 模拟微信序列号生成器（seqsvr）的实现
 */
public class WxSeqGenerator implements IdGenerator {
    // 模拟存储层，保存每个用户的max_seq
    private static final ConcurrentHashMap<Long, AtomicLong> STORE_SVR = new ConcurrentHashMap<>();

    // 预分配步长
    private static final int STEP = 10000;

    // 当前用户的uid，这里用线程变量模拟，实际场景中应从上下文中获取
    private static final ThreadLocal<Long> CURRENT_UID = new ThreadLocal<>();

    @Override
    public Object generate() {
        // 获取当前用户uid
        Long uid = CURRENT_UID.get();
        if (uid == null) {
            throw new IllegalStateException("Current uid not set");
        }

        // 获取或初始化该用户的max_seq
        AtomicLong maxSeqRef = STORE_SVR.computeIfAbsent(uid, k -> new AtomicLong(0));
        long curSeq = maxSeqRef.get();

        // 模拟预分配逻辑
        if (curSeq % STEP == 0) {
            // 更新max_seq并持久化（这里只是模拟，实际应写入存储系统）
            long newMaxSeq = curSeq + STEP;
            maxSeqRef.set(newMaxSeq);
            // 模拟持久化操作
            System.out.println("Persist max_seq for uid " + uid + ": " + newMaxSeq);
        }

        // 生成并返回sequence
        long sequence = curSeq + 1;
        maxSeqRef.incrementAndGet(); // 模拟cur_seq递增
        return sequence;
    }

    @Override
    public IdType idType() {
        return IdType.WxSeq;
    }

    /**
     * 设置当前用户uid（模拟，实际应从上下文中获取）
     */
    public static void setCurrentUid(Long uid) {
        CURRENT_UID.set(uid);
    }

    /**
     * 清除当前用户uid（模拟，实际应在请求结束时清理）
     */
    public static void clearCurrentUid() {
        CURRENT_UID.remove();
    }
}