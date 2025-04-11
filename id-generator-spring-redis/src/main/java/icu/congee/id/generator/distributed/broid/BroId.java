package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.ByteBuffer;
import java.time.LocalDate;

/**
 * BroId类 - 分布式ID生成器的实现
 * <p>
 * 该类实现了Id接口，提供了一种分布式环境下的唯一标识符生成方案。
 * BroId由三部分组成：
 * 1. timestamp(16位): 使用当前日期的纪元日作为时间戳
 * 2. threadId(16位): 线程标识符
 * 3. sequence(32位): 序列号
 * <p>
 * 总共64位(8字节)，可以表示为一个long类型的整数。
 */
@Data
@AllArgsConstructor
public class BroId implements Id {
    /**
     * 时间戳部分 - 16位
     * 使用LocalDate.toEpochDay()方法获取，表示从1970-01-01至今的天数
     */
    private short timestamp;

    /**
     * 线程ID部分 - 16位
     * 用于在多线程环境下区分不同的线程
     */
    private short threadId;

    /**
     * 序列号部分 - 32位
     * 用于在同一时间戳和同一线程ID下生成不同的ID
     */
    private int sequence;

    /**
     * 获取当前时间戳
     * <p>
     * 使用LocalDate.now().toEpochDay()获取当前日期距离1970-01-01的天数，
     * 并将结果转换为short类型。这种方式可以支持约179年的时间范围。
     * 
     * @return 当前日期的纪元日值(从1970-01-01至今的天数)，以short类型表示
     */
    public static short currentTimestamp() {
        return (short) LocalDate.now().toEpochDay();
    }

    /**
     * 将ID转换为字节数组
     * <p>
     * 使用ByteBuffer将ID的三个组成部分(timestamp, threadId, sequence)按顺序写入缓冲区，
     * 然后转换为字节数组返回。总共占用8个字节(64位)。
     * 
     * @return 8字节的字节数组，包含ID的完整信息
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(8).putShort(timestamp).putShort(threadId).putInt(sequence).array();
    }

    /**
     * 将ID转换为长整型(long)表示
     * <p>
     * 使用位运算将三个部分组合成一个64位的长整型：
     * - timestamp(16位)左移32位，占据高16位
     * - threadId(16位)左移16位，占据中间16位
     * - sequence(32位)保持不变，占据低32位
     * 
     * @return 表示完整ID的长整型值
     */
    @Override
    public long toLong() {
        return (long) timestamp << 32 | (long) threadId << 16 | sequence;
    }
}