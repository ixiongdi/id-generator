package icu.congee.id.generator.distributed.broid;

// 导入基础ID生成器接口和ID类型枚举
import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

// 导入Spring注解
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

// 导入Lombok注解
import lombok.AllArgsConstructor;
import lombok.Data;

// 导入Redisson相关类
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
// 导入Spring注解
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// 标记为Spring组件
@Component
// 实现IdGenerator接口的枚举单例
public enum BroIdGenerator implements IdGenerator {
    // 单例实例
    INSTANCE;

    // 注入Redis客户端，用于分布式操作
    @Resource
    private RedissonClient redisson;

    // 原子长整型，用于生成全局唯一的线程ID
    private RAtomicLong threadId;

    // 配置符号位数，默认为1位
    @Value("${id.generator.broid.sign-bits:1}")
    private int signBits;

    // 配置线程ID位数，默认为15位
    @Value("${id.generator.broid.thread-id-bits:15}")
    private int threadIdBits;

    // 配置序列号位数，默认为48位
    @Value("${id.generator.broid.sequence-bits:48}")
    private int sequenceBits;

    // 线程本地变量，存储当前线程的ID和序列号信息
    private ThreadLocal<BroIdThreadLocalHolder> threadLocalHolder;

    // 初始化方法，在构造后自动调用
    @PostConstruct
    public void init() {
        // 验证位数配置总和不超过64位（Java long类型的位数）
        if (signBits + threadIdBits + sequenceBits > 64) {
            throw new IllegalArgumentException("Total bits exceeds 64");
        }
        // 初始化Redis原子长整型组件，用于生成全局唯一的线程ID
        this.threadId = redisson.getAtomicLong("IdGenerator:BroIdGenerator:threadId");

        // 初始化线程本地变量，为每个线程分配唯一的线程ID和初始序列号
        threadLocalHolder = ThreadLocal.withInitial(
                () -> {
                    // 获取并递增全局线程ID计数器
                    long currentThreadId = this.threadId.getAndIncrement();
                    // 创建新的线程本地持有者，初始序列号为0
                    return new BroIdThreadLocalHolder(currentThreadId, 0);
                });
    }

    // 实现IdGenerator接口的generate方法，生成新的唯一ID
    @Override
    public Long generate() {
        // 获取当前线程的本地持有者对象
        BroIdThreadLocalHolder broIdThreadLocalHolder = this.threadLocalHolder.get();
        // 通过位运算组合ID：将线程ID左移序列号位数，再与递增的序列号进行按位或运算
        return broIdThreadLocalHolder.threadId << sequenceBits | broIdThreadLocalHolder.sequence++;
    }

    // 实现IdGenerator接口的idType方法，返回当前生成器的ID类型
    @Override
    public IdType idType() {
        // 返回BroId类型
        return IdType.BroId;
    }

    // 内部静态类，用于在线程本地变量中存储线程ID和序列号
    @Data // 自动生成getter、setter、equals、hashCode和toString方法
    @AllArgsConstructor // 自动生成包含所有字段的构造函数
    public static class BroIdThreadLocalHolder {
        // 线程的唯一标识ID
        long threadId;

        // 该线程内部的序列号计数器
        long sequence;
    }
}
