package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 分布式ID生成器实现类（增强版）
 * 采用枚举单例模式实现，基于时间戳+线程ID+序列号的组合方式生成唯一ID
 */
@Component
public enum BroIdGeneratorUltra implements IdGenerator {

    // 单例实例
    INSTANCE;

    // Redisson客户端，用于分布式环境下的原子操作
    @Resource
    private RedissonClient redisson;

    // 分布式原子计数器，用于生成全局唯一的线程ID
    private RAtomicLong threadId;

    // 线程本地变量，存储每个线程特有的ID生成状态
    private ThreadLocal<BroIdThreadLocalHolder> threadLocalHolder;

    /**
     * 初始化方法，在Bean创建后自动执行
     * 1. 初始化分布式线程ID计数器
     * 2. 设置线程本地变量初始化逻辑
     */
    @PostConstruct
    public void init() {
        // 从Redis获取原子长整型计数器，用于分配全局唯一的线程ID
        this.threadId = redisson.getAtomicLong("IdGenerator:BroIdGenerator:threadId");

        // 初始化线程本地变量，每个线程首次访问时会执行初始化逻辑
        threadLocalHolder = ThreadLocal.withInitial(() -> {
            // 获取并递增全局线程ID（保证分布式环境下线程ID唯一）
            long currentThreadId = this.threadId.getAndIncrement();
            // 创建线程本地数据持有对象，包含线程ID和初始序列号
            return new BroIdThreadLocalHolder((int) currentThreadId, 0);
        });
    }

    /**
     * 生成唯一ID
     * @return 返回包含时间戳、线程ID和序列号的BroIdUltra对象
     */
    @Override
    public BroIdUltra generate() {
        // 获取当前线程的ID状态持有对象
        BroIdThreadLocalHolder holder = this.threadLocalHolder.get();
        // 构造并返回新ID：当前纳秒时间戳 + 线程ID + 自增序列号
        return new BroIdUltra(
                BroIdUltra.currentNanoTimestamp(),  // 高精度时间戳
                holder.threadId,                    // 线程唯一标识
                holder.sequence++                  // 线程内自增序列号
        );
    }

    /**
     * 获取ID生成器类型
     * @return 返回BroId类型标识
     */
    @Override
    public IdType idType() {
        return IdType.BroId;
    }

    /**
     * 线程本地数据持有类
     * 存储每个线程特有的ID生成状态信息
     */
    @AllArgsConstructor
    public static class BroIdThreadLocalHolder {
        // 线程唯一标识（由分布式计数器分配）
        int threadId;
        // 当前线程内已生成的序列号（线程安全的自增）
        int sequence;
    }
}