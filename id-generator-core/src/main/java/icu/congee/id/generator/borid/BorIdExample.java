package icu.congee.id.generator.borid;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * BorId示例类
 * 展示如何使用BorId生成器创建不同类型的ID
 */
public class BorIdExample {

    /**
     * 创建一个标准的BorId生成器
     * 结构：48位时间戳 + 16位计数器 + 48位机器标识 + 16位随机数
     * 
     * @return BorId生成器
     */
    public static BorIdGenerator createStandardGenerator() {
        List<BorIdPart> parts = new ArrayList<>();

        AtomicInteger i = new AtomicInteger(0);

        // 添加48位时间戳部分
        parts.add(new InstantBorIdPart(48, b -> longToBitSet(Instant.now().toEpochMilli(), 48)));

        // 添加4位版本
        parts.add(new InstantBorIdPart(4, b -> longToBitSet(8, 4)));

        // 添加12位的亚秒
        parts.add(new InstantBorIdPart(12, b -> longToBitSet(Instant.now().getNano(), 12)));

        // 添加2位变体
        parts.add(new EigenvalueBorIdPart(2, b -> longToBitSet(2, 2)));

        // 添加14位node
        parts.add(new EigenvalueBorIdPart(14, b -> longToBitSet(Math.round(16384L), 14)));

        // 添加48位全局计数器
        parts.add(new CounterBorIdPart(48, b -> longToBitSet(i.getAndIncrement(), 48)));

        // 创建布局和生成器
        BorIdLayout layout = new BorIdLayout(parts);
        return new BorIdGenerator(layout);
    }

    public static BitSet longToBitSet(long value, int bits) {
        if (bits < 0 || bits > 64) {
            throw new IllegalArgumentException("bits must be between 0 and 64");
        }

        BitSet fullBits = BitSet.valueOf(new long[] { value });
        BitSet result = new BitSet(bits);

        int start = 64 - bits; // 起始索引为64-bits
        for (int i = 0; i < bits; i++) {
            if (fullBits.get(start + i)) {
                result.set(i);
            }
        }
        return result;
    }

    /**
     * 主方法，展示不同生成器的使用
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 创建标准生成器
        BorIdGenerator standardGenerator = createStandardGenerator();
        for (int i = 0; i < 10; i++) {
            System.out.println("标准ID: " + standardGenerator.next());
        }
    }
}