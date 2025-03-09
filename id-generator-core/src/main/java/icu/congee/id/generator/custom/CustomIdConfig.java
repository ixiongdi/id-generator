package icu.congee.id.generator.custom;

/**
 * 自定义ID生成器配置类
 * <p>
 * 该类用于配置自定义ID生成器的各个部分的参数，包括时间戳、工作节点ID、序列号和随机数的位数和生成方法。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class CustomIdConfig {
    // 默认纪元时间：2022-02-22 14:22:22 GMT-05:00
    private static final long DEFAULT_EPOCH = 1645557742L;
    
    // 默认位数分配（总共64位）
    private static final int DEFAULT_TIMESTAMP_BITS = 41; // 时间戳占41位，可使用约69年
    private static final int DEFAULT_WORKER_ID_BITS = 10; // 工作节点ID占10位，最多支持1024个节点
    private static final int DEFAULT_SEQUENCE_BITS = 12; // 序列号占12位，每毫秒最多生成4096个ID
    private static final int DEFAULT_RANDOM_BITS = 1; // 随机数占1位
    
    // 默认部分顺序
    private static final int DEFAULT_TIMESTAMP_ORDER = 0;
    private static final int DEFAULT_WORKER_ID_ORDER = 1;
    private static final int DEFAULT_SEQUENCE_ORDER = 2;
    private static final int DEFAULT_RANDOM_ORDER = 3;
    
    // 时间戳配置
    private long epoch = DEFAULT_EPOCH; // 纪元时间
    private int timestampBits = DEFAULT_TIMESTAMP_BITS; // 时间戳位数
    private IdPartGenerator<Long> timestampGenerator; // 时间戳生成器
    private boolean timestampEnabled = true; // 是否启用时间戳部分
    private int timestampOrder = DEFAULT_TIMESTAMP_ORDER; // 时间戳部分顺序
    
    // 工作节点ID配置
    private int workerIdBits = DEFAULT_WORKER_ID_BITS; // 工作节点ID位数
    private long workerId = 0; // 工作节点ID
    private IdPartGenerator<Long> workerIdGenerator; // 工作节点ID生成器
    private boolean workerIdEnabled = true; // 是否启用工作节点ID部分
    private int workerIdOrder = DEFAULT_WORKER_ID_ORDER; // 工作节点ID部分顺序
    
    // 序列号配置
    private int sequenceBits = DEFAULT_SEQUENCE_BITS; // 序列号位数
    private IdPartGenerator<Long> sequenceGenerator; // 序列号生成器
    private boolean sequenceEnabled = true; // 是否启用序列号部分
    private int sequenceOrder = DEFAULT_SEQUENCE_ORDER; // 序列号部分顺序
    
    // 随机数配置
    private int randomBits = DEFAULT_RANDOM_BITS; // 随机数位数
    private IdPartGenerator<Long> randomGenerator; // 随机数生成器
    private boolean randomEnabled = true; // 是否启用随机数部分
    private int randomOrder = DEFAULT_RANDOM_ORDER; // 随机数部分顺序
    
    /**
     * 默认构造函数
     */
    public CustomIdConfig() {
        // 使用默认配置
    }
    
    /**
     * 获取纪元时间
     *
     * @return 纪元时间
     */
    public long getEpoch() {
        return epoch;
    }
    
    /**
     * 设置纪元时间
     *
     * @param epoch 纪元时间
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setEpoch(long epoch) {
        this.epoch = epoch;
        return this;
    }
    
    /**
     * 获取时间戳位数
     *
     * @return 时间戳位数
     */
    public int getTimestampBits() {
        return timestampBits;
    }
    
    /**
     * 设置时间戳位数
     *
     * @param timestampBits 时间戳位数
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setTimestampBits(int timestampBits) {
        if (timestampBits <= 0 || timestampBits > 60) {
            throw new IllegalArgumentException("时间戳位数必须大于0且不超过60");
        }
        this.timestampBits = timestampBits;
        return this;
    }
    
    /**
     * 获取时间戳生成器
     *
     * @return 时间戳生成器
     */
    public IdPartGenerator<Long> getTimestampGenerator() {
        return timestampGenerator;
    }
    
    /**
     * 设置时间戳生成器
     *
     * @param timestampGenerator 时间戳生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setTimestampGenerator(IdPartGenerator<Long> timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
        return this;
    }
    
    /**
     * 获取工作节点ID位数
     *
     * @return 工作节点ID位数
     */
    public int getWorkerIdBits() {
        return workerIdBits;
    }
    
    /**
     * 设置工作节点ID位数
     *
     * @param workerIdBits 工作节点ID位数
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setWorkerIdBits(int workerIdBits) {
        if (workerIdBits < 0 || workerIdBits > 30) {
            throw new IllegalArgumentException("工作节点ID位数必须不小于0且不超过30");
        }
        this.workerIdBits = workerIdBits;
        return this;
    }
    
    /**
     * 获取工作节点ID
     *
     * @return 工作节点ID
     */
    public long getWorkerId() {
        return workerId;
    }
    
    /**
     * 设置工作节点ID
     *
     * @param workerId 工作节点ID
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setWorkerId(long workerId) {
        long maxWorkerId = ~(-1L << workerIdBits);
        if (workerId < 0 || workerId > maxWorkerId) {
            throw new IllegalArgumentException("工作节点ID必须不小于0且不超过" + maxWorkerId);
        }
        this.workerId = workerId;
        return this;
    }
    
    /**
     * 获取工作节点ID生成器
     *
     * @return 工作节点ID生成器
     */
    public IdPartGenerator<Long> getWorkerIdGenerator() {
        return workerIdGenerator;
    }
    
    /**
     * 设置工作节点ID生成器
     *
     * @param workerIdGenerator 工作节点ID生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setWorkerIdGenerator(IdPartGenerator<Long> workerIdGenerator) {
        this.workerIdGenerator = workerIdGenerator;
        return this;
    }
    
    /**
     * 获取序列号位数
     *
     * @return 序列号位数
     */
    public int getSequenceBits() {
        return sequenceBits;
    }
    
    /**
     * 设置序列号位数
     *
     * @param sequenceBits 序列号位数
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setSequenceBits(int sequenceBits) {
        if (sequenceBits < 0 || sequenceBits > 30) {
            throw new IllegalArgumentException("序列号位数必须不小于0且不超过30");
        }
        this.sequenceBits = sequenceBits;
        return this;
    }
    
    /**
     * 获取序列号生成器
     *
     * @return 序列号生成器
     */
    public IdPartGenerator<Long> getSequenceGenerator() {
        return sequenceGenerator;
    }
    
    /**
     * 设置序列号生成器
     *
     * @param sequenceGenerator 序列号生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setSequenceGenerator(IdPartGenerator<Long> sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
        return this;
    }
    
    /**
     * 获取随机数位数
     *
     * @return 随机数位数
     */
    public int getRandomBits() {
        return randomBits;
    }
    
    /**
     * 设置随机数位数
     *
     * @param randomBits 随机数位数
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setRandomBits(int randomBits) {
        if (randomBits < 0 || randomBits > 30) {
            throw new IllegalArgumentException("随机数位数必须不小于0且不超过30");
        }
        this.randomBits = randomBits;
        return this;
    }
    
    /**
     * 获取随机数生成器
     *
     * @return 随机数生成器
     */
    public IdPartGenerator<Long> getRandomGenerator() {
        return randomGenerator;
    }
    
    /**
     * 设置随机数生成器
     *
     * @param randomGenerator 随机数生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setRandomGenerator(IdPartGenerator<Long> randomGenerator) {
        this.randomGenerator = randomGenerator;
        return this;
    }
    
    /**
     * 获取时间戳部分是否启用
     *
     * @return 时间戳部分是否启用
     */
    public boolean isTimestampEnabled() {
        return timestampEnabled;
    }
    
    /**
     * 设置时间戳部分是否启用
     *
     * @param timestampEnabled 时间戳部分是否启用
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setTimestampEnabled(boolean timestampEnabled) {
        this.timestampEnabled = timestampEnabled;
        return this;
    }
    
    /**
     * 获取时间戳部分顺序
     *
     * @return 时间戳部分顺序
     */
    public int getTimestampOrder() {
        return timestampOrder;
    }
    
    /**
     * 设置时间戳部分顺序
     *
     * @param timestampOrder 时间戳部分顺序
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setTimestampOrder(int timestampOrder) {
        this.timestampOrder = timestampOrder;
        return this;
    }
    
    /**
     * 获取工作节点ID部分是否启用
     *
     * @return 工作节点ID部分是否启用
     */
    public boolean isWorkerIdEnabled() {
        return workerIdEnabled;
    }
    
    /**
     * 设置工作节点ID部分是否启用
     *
     * @param workerIdEnabled 工作节点ID部分是否启用
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setWorkerIdEnabled(boolean workerIdEnabled) {
        this.workerIdEnabled = workerIdEnabled;
        return this;
    }
    
    /**
     * 获取工作节点ID部分顺序
     *
     * @return 工作节点ID部分顺序
     */
    public int getWorkerIdOrder() {
        return workerIdOrder;
    }
    
    /**
     * 设置工作节点ID部分顺序
     *
     * @param workerIdOrder 工作节点ID部分顺序
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setWorkerIdOrder(int workerIdOrder) {
        this.workerIdOrder = workerIdOrder;
        return this;
    }
    
    /**
     * 获取序列号部分是否启用
     *
     * @return 序列号部分是否启用
     */
    public boolean isSequenceEnabled() {
        return sequenceEnabled;
    }
    
    /**
     * 设置序列号部分是否启用
     *
     * @param sequenceEnabled 序列号部分是否启用
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setSequenceEnabled(boolean sequenceEnabled) {
        this.sequenceEnabled = sequenceEnabled;
        return this;
    }
    
    /**
     * 获取序列号部分顺序
     *
     * @return 序列号部分顺序
     */
    public int getSequenceOrder() {
        return sequenceOrder;
    }
    
    /**
     * 设置序列号部分顺序
     *
     * @param sequenceOrder 序列号部分顺序
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setSequenceOrder(int sequenceOrder) {
        this.sequenceOrder = sequenceOrder;
        return this;
    }
    
    /**
     * 获取随机数部分是否启用
     *
     * @return 随机数部分是否启用
     */
    public boolean isRandomEnabled() {
        return randomEnabled;
    }
    
    /**
     * 设置随机数部分是否启用
     *
     * @param randomEnabled 随机数部分是否启用
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setRandomEnabled(boolean randomEnabled) {
        this.randomEnabled = randomEnabled;
        return this;
    }
    
    /**
     * 获取随机数部分顺序
     *
     * @return 随机数部分顺序
     */
    public int getRandomOrder() {
        return randomOrder;
    }
    
    /**
     * 设置随机数部分顺序
     *
     * @param randomOrder 随机数部分顺序
     * @return 当前配置对象，用于链式调用
     */
    public CustomIdConfig setRandomOrder(int randomOrder) {
        this.randomOrder = randomOrder;
        return this;
    }
    
    /**
     * 验证配置是否有效
     * 
     * @return 当前配置对象，用于链式调用
     * @throws IllegalStateException 如果配置无效
     */
    public CustomIdConfig validate() {
        // 验证位数总和不超过64位
        int totalBits = 0;
        if (timestampEnabled) totalBits += timestampBits;
        if (workerIdEnabled) totalBits += workerIdBits;
        if (sequenceEnabled) totalBits += sequenceBits;
        if (randomEnabled) totalBits += randomBits;
        
        if (totalBits > 64) {
            throw new IllegalStateException("位数总和不能超过64位，当前配置总位数为" + totalBits + "位");
        }
        
        // 至少启用一个部分
        if (!timestampEnabled && !workerIdEnabled && !sequenceEnabled && !randomEnabled) {
            throw new IllegalStateException("至少需要启用一个ID部分");
        }
        
        return this;
    }
}