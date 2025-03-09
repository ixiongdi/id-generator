package icu.congee.id.generator.custom;

import icu.congee.id.base.IdType;
import icu.congee.id.generator.custom.part.IdPart;
import icu.congee.id.generator.custom.part.TimestampPart;
import icu.congee.id.generator.custom.part.WorkerIdPart;
import icu.congee.id.generator.custom.part.SequencePart;
import icu.congee.id.generator.custom.part.RandomPart;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义长整型ID生成器实现类
 * <p>
 * 该类实现了LongIdGenerator接口，提供完全自定义的64位长整型ID生成功能。
 * 用户可以自定义时间戳、工作节点ID、序列号和随机数的生成方法和位数分配。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class CustomLongIdGenerator implements LongIdGenerator {
    
    // 配置信息
    private final CustomIdConfig config;
    
    // 各部分对象
    private final TimestampPart timestampPart;
    private final WorkerIdPart workerIdPart;
    private final SequencePart sequencePart;
    private final RandomPart randomPart;
    
    // 默认生成器
    private static final IdPartGenerator<Long> DEFAULT_TIMESTAMP_GENERATOR = () -> {
        return System.currentTimeMillis() - 1645557742L; // 使用默认纪元时间
    };
    
    private static final IdPartGenerator<Long> DEFAULT_WORKER_ID_GENERATOR = () -> 0L;
    
    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    private static final IdPartGenerator<Long> DEFAULT_SEQUENCE_GENERATOR = () -> {
        return sequenceCounter.getAndIncrement() & 0xFFF; // 12位序列号
    };
    
    private static final IdPartGenerator<Long> DEFAULT_RANDOM_GENERATOR = () -> {
        return ThreadLocalRandom.current().nextLong(2); // 1位随机数
    };
    
    /**
     * 使用默认配置创建自定义ID生成器
     */
    public CustomLongIdGenerator() {
        this(new CustomIdConfig());
    }
    
    /**
     * 使用指定配置创建自定义ID生成器
     *
     * @param config 自定义ID配置
     */
    public CustomLongIdGenerator(CustomIdConfig config) {
        this.config = config.validate();
        
        // 初始化默认生成器（如果未指定）
        if (config.getTimestampGenerator() == null) {
            config.setTimestampGenerator(DEFAULT_TIMESTAMP_GENERATOR);
        }
        
        if (config.getWorkerIdGenerator() == null) {
            config.setWorkerIdGenerator(DEFAULT_WORKER_ID_GENERATOR);
        }
        
        if (config.getSequenceGenerator() == null) {
            config.setSequenceGenerator(DEFAULT_SEQUENCE_GENERATOR);
        }
        
        if (config.getRandomGenerator() == null) {
            config.setRandomGenerator(DEFAULT_RANDOM_GENERATOR);
        }
        
        // 初始化各部分对象
        timestampPart = new TimestampPart(
            config.getTimestampBits(), 
            config.getEpoch(), 
            config.getTimestampGenerator()
        );
        
        workerIdPart = new WorkerIdPart(
            config.getWorkerIdBits(), 
            config.getWorkerIdGenerator()
        );
        
        sequencePart = new SequencePart(
            config.getSequenceBits(), 
            config.getSequenceGenerator()
        );
        
        randomPart = new RandomPart(
            config.getRandomBits(), 
            config.getRandomGenerator()
        );
        
        // 生成初始值
        refreshParts();
    }
    
    /**
     * 刷新ID各部分的值
     */
    private void refreshParts() {
        timestampPart.refreshAndGet();
        workerIdPart.refreshAndGet();
        sequencePart.refreshAndGet();
        randomPart.refreshAndGet();
    }
    
    /**
     * 生成一个64位的长整型ID
     *
     * @return 生成的长整型ID
     */
    @Override
    public Long generate() {
        refreshParts();
        
        // 创建ID部分数组，按照顺序排列
        IdPartInfo[] parts = new IdPartInfo[4];
        
        // 只添加启用的部分
        if (config.isTimestampEnabled()) {
            parts[config.getTimestampOrder()] = new IdPartInfo(timestampPart, "timestamp");
        }
        
        if (config.isWorkerIdEnabled()) {
            parts[config.getWorkerIdOrder()] = new IdPartInfo(workerIdPart, "workerId");
        }
        
        if (config.isSequenceEnabled()) {
            parts[config.getSequenceOrder()] = new IdPartInfo(sequencePart, "sequence");
        }
        
        if (config.isRandomEnabled()) {
            parts[config.getRandomOrder()] = new IdPartInfo(randomPart, "random");
        }
        
        // 计算各部分的位移并组合生成最终的ID
        long id = 0L;
        int currentShift = 0;
        
        for (int i = parts.length - 1; i >= 0; i--) {
            if (parts[i] != null) {
                IdPart part = parts[i].getPart();
                id |= (part.getValue() << currentShift);
                currentShift += part.getBits();
            }
        }
        
        return id;
    }
    
    /**
     * 使用指定的格式生成ID
     *
     * @param format 格式字符串，定义ID各部分的组合方式
     * @return 按照指定格式生成的ID
     */
    @Override
    public Long generateWithFormat(String format) {
        refreshParts();
        
        // 解析格式字符串，替换占位符
        String result = format;
        result = result.replace("{ts}", String.valueOf(timestampPart.getValue()));
        result = result.replace("{wid}", String.valueOf(workerIdPart.getValue()));
        result = result.replace("{seq}", String.valueOf(sequencePart.getValue()));
        result = result.replace("{rnd}", String.valueOf(randomPart.getValue()));
        
        try {
            return Long.parseLong(result);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("格式化后的ID不是有效的长整型: " + result, e);
        }
    }
    
    /**
     * 获取当前生成器的时间戳部分
     *
     * @return 时间戳部分的值
     */
    @Override
    public long getTimestampPart() {
        return timestampPart.getValue();
    }
    
    /**
     * 获取当前生成器的工作节点ID部分
     *
     * @return 工作节点ID部分的值
     */
    @Override
    public long getWorkerIdPart() {
        return workerIdPart.getValue();
    }
    
    /**
     * 获取当前生成器的序列号部分
     *
     * @return 序列号部分的值
     */
    @Override
    public long getSequencePart() {
        return sequencePart.getValue();
    }
    
    /**
     * 获取当前生成器的随机数部分
     *
     * @return 随机数部分的值
     */
    @Override
    public long getRandomPart() {
        return randomPart.getValue();
    }
    
    /**
     * 获取ID类型
     *
     * @return ID类型
     */
    @Override
    public IdType idType() {
        return IdType.CustomTimeBasedRandomId; // 使用现有的类型
    }
}