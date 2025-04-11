package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * BroIdPro类 - 分布式ID生成器的高级实现
 * 实现了Id接口，提供96位(12字节)的唯一标识符
 * 由三个32位整数组成：时间戳、线程ID和序列号
 */
@Data
@AllArgsConstructor
public class BroIdPro implements Id {

    /**
     * 纪元时间常量：2025年4月1日 00:00:00 UTC
     * 用作时间戳计算的起始点，减少存储空间需求
     */
    private static final int EPOCH = (int) ZonedDateTime.of(2025, 4, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toInstant().getEpochSecond();

    /**
     * 32位时间戳（自纪元时间以来的秒数）
     * 支持从纪元时间起约69年的时间范围
     */
    private int timestamp;

    /**
     * 32位线程标识符
     * 用于区分不同线程生成的ID，确保分布式环境下的唯一性
     */
    private int threadId;

    /**
     * 32位序列号
     * 用于同一时间戳和线程下的序列生成，避免冲突
     */
    private int sequence;

    /**
     * 获取当前时间相对于定义的纪元时间（2025年4月1日）的秒数
     * 用于生成ID中的时间戳部分
     *
     * @return 当前时间的秒数表示（自定义纪元以来的秒数）
     * @throws IllegalStateException 当时间戳超出支持的最大范围时抛出异常
     */
    public static int currentTimestamp() {
        int currentEpochSecond = (int) Instant.now().getEpochSecond();
        return currentEpochSecond - EPOCH;
    }

    /**
     * 将BroIdPro对象转换为字节数组
     * 字节数组结构：4字节时间戳 + 4字节线程ID + 4字节序列号 = 总共12字节
     *
     * @return 包含ID所有组成部分的字节数组
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(12) // 分配12字节缓冲区
                .putInt(timestamp) // 写入4字节时间戳
                .putInt(threadId) // 写入4字节线程ID
                .putInt(sequence) // 写入4字节序列号
                .array(); // 返回字节数组
    }

    /**
     * 尝试将ID转换为long类型
     * 由于BroIdPro使用96位(12字节)存储，超过long类型的64位容量，因此不支持此操作
     *
     * @throws UnsupportedOperationException 总是抛出此异常，因为转换会丢失数据
     */
    @Override
    public long toLong() {
        throw new UnsupportedOperationException("BroIdPro使用96位(12字节)存储，超过了long类型的64位容量");
    }
}