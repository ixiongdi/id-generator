package icu.congee.id.generator.distributed.broid;

import icu.congee.id.base.Id;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.nio.ByteBuffer;
import java.time.Instant;

@Data
@AllArgsConstructor
public class BroIdUltra implements Id {
    // ID的组成部分：
    private long timestamp;  // 64位时间戳（系统默认纪元1970-01-01开始的纳秒数）
    private int threadId;    // 32位线程标识符，用于区分不同线程生成的ID
    private int sequence;    // 32位序列号，用于同一时间戳和线程下的序列生成

    /**
     * 获取当前时间的纳秒级时间戳
     * 使用系统默认纪元（1970-01-01 00:00:00 UTC）
     *
     * @return 当前时间的纳秒表示（自1970-01-01以来的纳秒数）
     */
    public static long currentNanoTimestamp() {
        Instant now = Instant.now();
        // 将当前时间转换为纳秒表示（秒部分*10^9 + 纳秒部分）
        return now.getEpochSecond() * 1_000_000_000L + now.getNano();
    }

    /**
     * 将BroIdUltra对象转换为字节数组
     * 字节数组结构：8字节时间戳 + 4字节线程ID + 4字节序列号 = 总共16字节
     *
     * @return 包含ID所有组成部分的字节数组
     */
    @Override
    public byte[] toBytes() {
        return ByteBuffer.allocate(16)  // 分配16字节缓冲区
                .putLong(timestamp)     // 写入8字节时间戳
                .putInt(threadId)       // 写入4字节线程ID
                .putInt(sequence)       // 写入4字节序列号
                .array();              // 返回字节数组
    }

    /**
     * 尝试将ID转换为long类型
     * 由于BroIdUltra使用128位(16字节)存储，超过long类型的64位容量，因此不支持此操作
     *
     * @throws UnsupportedOperationException 总是抛出此异常，因为转换会丢失数据
     */
    @Override
    public long toLong() {
        throw new UnsupportedOperationException("BroIdUltra使用128位(16字节)存储，超过了long类型的64位容量");
    }
}