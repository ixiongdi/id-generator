package uno.xifan.id.generator.broid.part;

import uno.xifan.id.generator.broid.BitUtils;
import uno.xifan.id.generator.broid.BroIdPart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
/**
 * BroId的时钟序列部分实现类。
 * 负责生成BroId中的时间戳和序列号部分，用于确保在同一时间戳下生成的ID的唯一性。
 * 通过原子计数器维护时间戳和序列号，保证线程安全。
 */
public class ClockSeqBroIdPart implements BroIdPart {

    /**
     * 时间戳原子计数器
     */
    private final AtomicLong timestamp;

    /**
     * 序列号原子计数器
     */
    private final AtomicLong sequence;

    /**
     * 序列号使用的位数
     */
    private final int bits;

    /**
     * 生成下一个序列号的二进制表示。
     * 
     * @return 由bits位布尔值组成的列表，表示序列号的二进制形式
     */
    @Override
    public List<Boolean> next() {
        return BitUtils.longToList(sequence.getAndIncrement(), getBits());
    }
}
