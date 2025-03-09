package icu.congee.id.generator.custom;

/**
 * 自定义字符串ID生成器配置类
 * <p>
 * 该类用于配置自定义字符串ID生成器的各个部分的参数，包括时间戳、工作节点ID、序列号和随机数的生成方法和格式。
 * 支持自定义各部分的顺序和可选性。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */


public class CustomStringIdConfig {
    // 默认纪元时间：2022-02-22 14:22:22 GMT-05:00
    private static final long DEFAULT_EPOCH = 1645557742L;
    
    // 默认格式
    private static final String DEFAULT_FORMAT = "{ts}-{wid}-{seq}-{rnd}";
    
    // 默认部分顺序
    private static final int DEFAULT_TIMESTAMP_ORDER = 0;
    private static final int DEFAULT_WORKER_ID_ORDER = 1;
    private static final int DEFAULT_SEQUENCE_ORDER = 2;
    private static final int DEFAULT_RANDOM_ORDER = 3;
    
    // 时间戳配置
    private long epoch = DEFAULT_EPOCH; // 纪元时间
    private IdPartGenerator<String> timestampGenerator; // 时间戳生成器
    private boolean timestampEnabled = true; // 是否启用时间戳部分
    private int timestampOrder = DEFAULT_TIMESTAMP_ORDER; // 时间戳部分顺序
    
    // 工作节点ID配置
    private IdPartGenerator<String> workerIdGenerator; // 工作节点ID生成器
    private boolean workerIdEnabled = true; // 是否启用工作节点ID部分
    private int workerIdOrder = DEFAULT_WORKER_ID_ORDER; // 工作节点ID部分顺序
    
    // 序列号配置
    private IdPartGenerator<String> sequenceGenerator; // 序列号生成器
    private boolean sequenceEnabled = true; // 是否启用序列号部分
    private int sequenceOrder = DEFAULT_SEQUENCE_ORDER; // 序列号部分顺序
    
    // 随机数配置
    private IdPartGenerator<String> randomGenerator; // 随机数生成器
    private boolean randomEnabled = true; // 是否启用随机数部分
    private int randomOrder = DEFAULT_RANDOM_ORDER; // 随机数部分顺序
    
    // 格式配置
    private String defaultFormat = DEFAULT_FORMAT; // 默认格式
    
    /**
     * 默认构造函数
     */
    public CustomStringIdConfig() {
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
    public CustomStringIdConfig setEpoch(long epoch) {
        this.epoch = epoch;
        return this;
    }
    
    /**
     * 获取时间戳生成器
     *
     * @return 时间戳生成器
     */
    public IdPartGenerator<String> getTimestampGenerator() {
        return timestampGenerator;
    }
    
    /**
     * 设置时间戳生成器
     *
     * @param timestampGenerator 时间戳生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomStringIdConfig setTimestampGenerator(IdPartGenerator<String> timestampGenerator) {
        this.timestampGenerator = timestampGenerator;
        return this;
    }
    
    /**
     * 获取工作节点ID生成器
     *
     * @return 工作节点ID生成器
     */
    public IdPartGenerator<String> getWorkerIdGenerator() {
        return workerIdGenerator;
    }
    
    /**
     * 设置工作节点ID生成器
     *
     * @param workerIdGenerator 工作节点ID生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomStringIdConfig setWorkerIdGenerator(IdPartGenerator<String> workerIdGenerator) {
        this.workerIdGenerator = workerIdGenerator;
        return this;
    }
    
    /**
     * 获取序列号生成器
     *
     * @return 序列号生成器
     */
    public IdPartGenerator<String> getSequenceGenerator() {
        return sequenceGenerator;
    }
    
    /**
     * 设置序列号生成器
     *
     * @param sequenceGenerator 序列号生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomStringIdConfig setSequenceGenerator(IdPartGenerator<String> sequenceGenerator) {
        this.sequenceGenerator = sequenceGenerator;
        return this;
    }
    
    /**
     * 获取随机数生成器
     *
     * @return 随机数生成器
     */
    public IdPartGenerator<String> getRandomGenerator() {
        return randomGenerator;
    }
    
    /**
     * 设置随机数生成器
     *
     * @param randomGenerator 随机数生成器
     * @return 当前配置对象，用于链式调用
     */
    public CustomStringIdConfig setRandomGenerator(IdPartGenerator<String> randomGenerator) {
        this.randomGenerator = randomGenerator;
        return this;
    }
    
    /**
     * 获取默认格式
     *
     * @return 默认格式
     */
    public String getDefaultFormat() {
        return defaultFormat;
    }
    
    /**
     * 设置默认格式
     *
     * @param defaultFormat 默认格式
     * @return 当前配置对象，用于链式调用
     */
    public CustomStringIdConfig setDefaultFormat(String defaultFormat) {
        if (defaultFormat == null || defaultFormat.isEmpty()) {
            throw new IllegalArgumentException("默认格式不能为空");
        }
        this.defaultFormat = defaultFormat;
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
    public CustomStringIdConfig setTimestampEnabled(boolean timestampEnabled) {
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
    public CustomStringIdConfig setTimestampOrder(int timestampOrder) {
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
    public CustomStringIdConfig setWorkerIdEnabled(boolean workerIdEnabled) {
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
    public CustomStringIdConfig setWorkerIdOrder(int workerIdOrder) {
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
    public CustomStringIdConfig setSequenceEnabled(boolean sequenceEnabled) {
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
    public CustomStringIdConfig setSequenceOrder(int sequenceOrder) {
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
    public CustomStringIdConfig setRandomEnabled(boolean randomEnabled) {
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
    public CustomStringIdConfig setRandomOrder(int randomOrder) {
        this.randomOrder = randomOrder;
        return this;
    }
    
    /**
     * 验证配置是否有效
     * 
     * @return 当前配置对象，用于链式调用
     * @throws IllegalStateException 如果配置无效
     */
    public CustomStringIdConfig validate() {
        // 至少启用一个部分
        if (!timestampEnabled && !workerIdEnabled && !sequenceEnabled && !randomEnabled) {
            throw new IllegalStateException("至少需要启用一个ID部分");
        }
        
        return this;
    }
}