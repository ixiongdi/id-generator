package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.broid.BitUtils;
import icu.congee.id.generator.broid.BroIdGenerator;
import icu.congee.id.generator.broid.BroIdLayout;
import icu.congee.id.generator.broid.BroIdPart;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class BroIdGeneratorCustom extends BroIdGenerator<BroIdCustom> implements IdGenerator {

    // 原子长整型，用于生成全局唯一的线程ID
    private RAtomicLong threadId;

    // 线程本地变量，存储当前线程的ID和序列号信息
    private ThreadLocal<BroIdThreadLocalHolder> threadLocalHolder;


    /**
     * 构造函数
     *
     * @param layout      BroId结构
     * @param constructor T类型的构造器引用
     */
    public BroIdGeneratorCustom(BroIdLayout layout, Function<List<Boolean>, BroIdCustom> constructor) {

        super(layout, constructor);

    }

    public BroIdGeneratorCustom BroIdGeneratorCustom(RedissonClient redisson) {

        // 初始化Redis原子长整型组件，用于生成全局唯一的线程ID
        this.threadId = redisson.getAtomicLong("IdGenerator:BroIdGenerator:threadId");

        // 初始化线程本地变量，为每个线程分配唯一的线程ID和初始序列号
        this.threadLocalHolder = ThreadLocal.withInitial(() -> {
            // 获取并递增全局线程ID计数器
            long currentThreadId = this.threadId.getAndIncrement();
            // 创建新的线程本地持有者，初始序列号为0
            return new BroIdThreadLocalHolder((short) currentThreadId, (short) 0);
        });

        List<BroIdPart> parts = new ArrayList<>();
        // 1. timestamp
        parts.add(new BroIdPart() {
            @Override
            public int getBits() {
                return 41;
            }

            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(System.currentTimeMillis(), this.getBits());
            }
        });
        // 2. threadId
        parts.add(new BroIdPart() {

            /**
             * 获取该部分的位长度
             *
             * @return 位长度
             */
            @Override
            public int getBits() {
                return 10;
            }

            /**
             * 生成下一个值
             *
             * @return 生成的{@code List<Boolean>}对象
             */
            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(threadLocalHolder.get().threadId, this.getBits());
            }
        });
        // 3. sequence
        parts.add(new BroIdPart() {
            /**
             * 获取该部分的位长度
             *
             * @return 位长度
             */
            @Override
            public int getBits() {
                return 12;
            }

            /**
             * 生成下一个值
             *
             * @return 生成的{@code List<Boolean>}对象
             */
            @Override
            public List<Boolean> next() {
                return BitUtils.longToList(threadLocalHolder.get().sequence++, this.getBits());
            }
        });
        BroIdLayout layout = new BroIdLayout(parts);
        return new BroIdGeneratorCustom(layout, BroIdCustom::new);
    }

    @Override
    public String generate() {
        return next().toUUID().toString();
    }

    @Override
    public IdType idType() {
        return IdType.BroId;
    }

    // 内部静态类，用于在线程本地变量中存储线程ID和序列号
    @Data // 自动生成getter、setter、equals、hashCode和toString方法
    @AllArgsConstructor // 自动生成包含所有字段的构造函数
    public static class BroIdThreadLocalHolder {
        // 线程的唯一标识ID
        short threadId;
        // 该线程内部的序列号计数器
        short sequence;
    }
}
