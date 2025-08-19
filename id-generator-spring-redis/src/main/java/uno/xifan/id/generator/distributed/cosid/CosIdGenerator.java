package uno.xifan.id.generator.distributed.cosid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;
import uno.xifan.id.generator.distributor.MachineIdDistributor;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CosIdGenerator implements IdGenerator {

    private final long maxSequence;
    private final MachineIdDistributor machineIdDistributor;
    private final int timestampBits;
    private final int machineBits;
    private final int sequenceBits;
    private final long epoch;
    private long currentSequence = 0L;
    private long lastTimestamp = -1L;

    public CosIdGenerator(RedissonClient redisson,
            @Value("${id.generator.cosid.timestamp.bits:44}") int timestampBits,
            @Value("${id.generator.cosid.machine.bits:20}") int machineBits,
            @Value("${id.generator.cosid.sequence.bits:16}") int sequenceBits,
            @Value("${id.generator.cosid.epoch:1672502400000}") long epoch) {
        this.timestampBits = timestampBits;
        this.machineBits = machineBits;
        this.sequenceBits = sequenceBits;
        this.epoch = epoch;
        // 验证位数分配是否合法
        if (timestampBits + machineBits + sequenceBits != 80) {
            throw new IllegalArgumentException(
                    String.format("位数分配总和必须为80位，当前配置：timestamp=%d, machine=%d, sequence=%d, total=%d", timestampBits,
                            machineBits, sequenceBits, timestampBits + machineBits + sequenceBits));
        }

        // 初始化最大序列号
        maxSequence = (1L << sequenceBits) - 1;

        machineIdDistributor = new MachineIdDistributor(redisson, IdType.CosId.getName(), machineBits);
    }

    /**
     * 等待下一个毫秒
     */
    private void waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
    }

    /**
     * 获取当前时间戳（相对于纪元时间）
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis() - epoch;
    }

    @Override
    public synchronized CosId generate() {
        long timestamp = getCurrentTimestamp();

        // 检查时钟回拨
        if (timestamp < lastTimestamp) {
            long backwardMillis = lastTimestamp - timestamp;
            String errorMessage = String.format("时钟回拨，拒绝生成ID，回拨时间：%d毫秒", backwardMillis);
            throw new RuntimeException(errorMessage);
        }

        // 如果是同一毫秒
        if (timestamp == lastTimestamp) {
            // 同一毫秒内序列号达到最大值
            if (currentSequence >= maxSequence) {
                waitNextMillis(lastTimestamp);
                timestamp = getCurrentTimestamp();
                currentSequence = 0L;
            }
        } else {
            // 时间戳变化，重置序列号
            currentSequence = 0L;
        }

        lastTimestamp = timestamp;
        return new CosId(timestamp, machineIdDistributor.get(), currentSequence++, timestampBits, machineBits,
                sequenceBits);
    }

    @Override
    public IdType idType() {
        return IdType.CosId;
    }
}
