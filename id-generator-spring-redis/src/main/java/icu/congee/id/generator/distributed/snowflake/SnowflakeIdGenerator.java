package icu.congee.id.generator.distributed.snowflake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.generator.distributor.MachineIdDistributor;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 雪花算法ID生成器
 *
 * <p>64位ID (42位时间戳 + 10位机器ID + 12位序列号)
 *
 * <p>整体结构： - 符号位：1位，固定为0 - 时间戳：41位，精确到毫秒，可用69年 - 机器ID：10位，最多支持1024个节点 - 序列号：12位，同一毫秒内最多生成4096个ID
 */
@Component
public class SnowflakeIdGenerator implements IdGenerator {

    /** 起始时间戳 (2022-02-23) */
    @Value("${id.generator.snowflake.epoch:1645557742000}")
    private long epoch;

    /** 时间戳占用位数 */
    @Value("${id.generator.snowflake.timestamp:41}")
    private long timestampBits;

    /** 机器ID占用位数 */
    @Value("${id.generator.snowflake.machine:10}")
    private long machineIdBits;

    /** 序列号占用位数 */
    @Value("${id.generator.snowflake.sequence:12}")
    private long sequenceBits;

    /** 上次生成ID的时间戳 */
    private long lastTimestamp = -1L;



    /** 机器ID服务，负责获取和维护当前节点的机器ID */
    private final MachineIdDistributor machineIdDistributor;

    /** 当前毫秒内的序列号 */
    private long sequence;

    SnowflakeIdGenerator(RedissonClient redisson) {
        machineIdDistributor = new MachineIdDistributor(redisson, IdType.Snowflake.getName());
    }

    /** 初始化方法，创建机器ID服务实例 */
    @PostConstruct
    public void init() {

    }

    /**
     * 获取当前时间戳
     *
     * @return 当前时间戳（毫秒）
     */
    private long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一个毫秒 当前毫秒内序列号用尽时，阻塞到下一个毫秒
     *
     * @param lastTimestamp 上次生成ID的时间戳
     * @return 下一个毫秒的时间戳
     */
    private long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 生成下一个ID
     *
     * @return 生成的ID
     * @throws RuntimeException 当发生时钟回拨时抛出异常
     */
    @Override
    public synchronized Long generate() {
        long timestamp = timeGen();

        // 检查时钟回拨，如果发生回拨则抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID");
        }

        // 如果是同一毫秒内，增加序列号
        if (timestamp == lastTimestamp) {
            // 通过位运算计算序列号，确保不超过最大值
            sequence = (sequence + 1) & (~(-1L << sequenceBits));
            // 当前毫秒内序列号用尽，等待下一毫秒
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳变化，重置序列号
            sequence = 0;
        }

        lastTimestamp = timestamp;
        // 通过位运算拼接最终的ID
        // 1. (timestamp - epoch) << (machineIdBits + sequenceBits)：时间戳部分左移机器ID位数和序列号位数
        // 2. machineIdService.get() << sequenceBits：机器ID左移序列号位数
        // 3. sequence：序列号部分
        return (timestamp - epoch) << (machineIdBits + sequenceBits)
                | machineIdDistributor.get() << sequenceBits
                | sequence;
    }

    /**
     * 获取ID生成器类型
     *
     * @return Snowflake类型
     */
    @Override
    public IdType idType() {
        return IdType.Snowflake;
    }
}
