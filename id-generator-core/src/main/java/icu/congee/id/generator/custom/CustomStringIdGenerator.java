package icu.congee.id.generator.custom;

import icu.congee.id.base.IdType;
import icu.congee.id.generator.custom.encode.Base64Encode;
import icu.congee.id.generator.custom.encode.Encode;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义字符串ID生成器实现类
 * <p>
 * 该类实现了StringIdGenerator接口，提供完全自定义的字符串ID生成功能。
 * 用户可以自定义时间戳、工作节点ID、序列号和随机数的生成方法和格式。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class CustomStringIdGenerator implements StringIdGenerator {
    
    // 配置信息
    private final CustomStringIdConfig config;
    
    // 各部分的值
    private String timestampValue;
    private String workerIdValue;
    private String sequenceValue;
    private String randomValue;
    
    // 默认生成器
    private static final IdPartGenerator<String> DEFAULT_TIMESTAMP_GENERATOR = () -> {
        return String.valueOf(System.currentTimeMillis() - 1645557742L); // 使用默认纪元时间
    };
    
    private static final IdPartGenerator<String> DEFAULT_WORKER_ID_GENERATOR = () -> "0";
    
    private static final AtomicLong sequenceCounter = new AtomicLong(0);
    private static final IdPartGenerator<String> DEFAULT_SEQUENCE_GENERATOR = () -> {
        return String.valueOf(sequenceCounter.getAndIncrement() % 10000); // 最多4位数字
    };
    
    private static final IdPartGenerator<String> DEFAULT_RANDOM_GENERATOR = () -> {
        return UUID.randomUUID().toString().substring(0, 8); // 使用UUID的前8位
    };
    
    // 默认编码器
    private static final Encode DEFAULT_ENCODE = new Base64Encode();
    
    /**
     * 使用默认配置创建自定义字符串ID生成器
     */
    public CustomStringIdGenerator() {
        this(new CustomStringIdConfig());
    }
    
    /**
     * 使用指定配置创建自定义字符串ID生成器
     *
     * @param config 自定义字符串ID配置
     */
    public CustomStringIdGenerator(CustomStringIdConfig config) {
        this.config = config;
        
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
        
        // 生成初始值
        refreshParts();
    }
    
    /**
     * 刷新ID各部分的值
     */
    private void refreshParts() {
        // 只刷新启用的部分
        if (config.isTimestampEnabled()) {
            timestampValue = config.getTimestampGenerator().generate();
        }
        
        if (config.isWorkerIdEnabled()) {
            workerIdValue = config.getWorkerIdGenerator().generate();
        }
        
        if (config.isSequenceEnabled()) {
            sequenceValue = config.getSequenceGenerator().generate();
        }
        
        if (config.isRandomEnabled()) {
            randomValue = config.getRandomGenerator().generate();
        }
    }
    
    /**
     * 生成一个字符串类型的ID
     *
     * @return 生成的字符串ID
     */
    @Override
    public String generate() {
        refreshParts();
        
        // 使用默认格式生成ID
        return generateWithFormat(config.getDefaultFormat());
    }
    
    /**
     * 使用指定的格式生成ID
     *
     * @param format 格式字符串，定义ID各部分的组合方式
     * @return 按照指定格式生成的ID
     */
    @Override
    public String generateWithFormat(String format) {
        refreshParts();
        
        // 解析格式字符串，替换占位符
        String result = format;
        
        // 只替换启用的部分
        if (config.isTimestampEnabled()) {
            result = result.replace("{ts}", timestampValue);
        } else {
            result = result.replace("{ts}", "");
        }
        
        if (config.isWorkerIdEnabled()) {
            result = result.replace("{wid}", workerIdValue);
        } else {
            result = result.replace("{wid}", "");
        }
        
        if (config.isSequenceEnabled()) {
            result = result.replace("{seq}", sequenceValue);
        } else {
            result = result.replace("{seq}", "");
        }
        
        if (config.isRandomEnabled()) {
            result = result.replace("{rnd}", randomValue);
        } else {
            result = result.replace("{rnd}", "");
        }
        
        // 清理多余的分隔符
        result = result.replaceAll("[-_][-_]+", "-");
        result = result.replaceAll("^[-_]+|[-_]+$", "");
        
        return result;
    }
    
    /**
     * 使用指定的编码方式生成ID
     * <p>
     * 该方法将各部分转换为字节数组，然后使用指定的编码方式进行编码。
     *
     * @param encode 编码方式
     * @return 编码后的ID
     */
    @Override
    public String generateWithEncode(Encode encode) {
        refreshParts();
        
        // 使用指定的编码方式（如果为null则使用默认编码）
        Encode encoder = (encode != null) ? encode : DEFAULT_ENCODE;
        
        // 创建结果字符串构建器
        StringBuilder result = new StringBuilder();
        
        // 只添加启用的部分
        if (config.isTimestampEnabled()) {
            result.append(encoder.encode(getTimestampBytes()));
        }
        
        if (config.isWorkerIdEnabled()) {
            if (result.length() > 0) result.append("-");
            result.append(encoder.encode(getWorkerIdBytes()));
        }
        
        if (config.isSequenceEnabled()) {
            if (result.length() > 0) result.append("-");
            result.append(encoder.encode(getSequenceBytes()));
        }
        
        if (config.isRandomEnabled()) {
            if (result.length() > 0) result.append("-");
            result.append(encoder.encode(getRandomBytes()));
        }
        
        return result.toString();
    }
    
    /**
     * 获取当前生成器的时间戳部分
     *
     * @return 时间戳部分的字符串表示
     */
    @Override
    public String getTimestampPart() {
        return timestampValue;
    }
    
    /**
     * 获取当前生成器的工作节点ID部分
     *
     * @return 工作节点ID部分的字符串表示
     */
    @Override
    public String getWorkerIdPart() {
        return workerIdValue;
    }
    
    /**
     * 获取当前生成器的序列号部分
     *
     * @return 序列号部分的字符串表示
     */
    @Override
    public String getSequencePart() {
        return sequenceValue;
    }
    
    /**
     * 获取当前生成器的随机数部分
     *
     * @return 随机数部分的字符串表示
     */
    @Override
    public String getRandomPart() {
        return randomValue;
    }
    
    /**
     * 获取当前生成器的时间戳部分的字节表示
     *
     * @return 时间戳部分的字节数组
     */
    @Override
    public byte[] getTimestampBytes() {
        return timestampValue != null ? timestampValue.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }
    
    /**
     * 获取当前生成器的工作节点ID部分的字节表示
     *
     * @return 工作节点ID部分的字节数组
     */
    @Override
    public byte[] getWorkerIdBytes() {
        return workerIdValue != null ? workerIdValue.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }
    
    /**
     * 获取当前生成器的序列号部分的字节表示
     *
     * @return 序列号部分的字节数组
     */
    @Override
    public byte[] getSequenceBytes() {
        return sequenceValue != null ? sequenceValue.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }
    
    /**
     * 获取当前生成器的随机数部分的字节表示
     *
     * @return 随机数部分的字节数组
     */
    @Override
    public byte[] getRandomBytes() {
        return randomValue != null ? randomValue.getBytes(StandardCharsets.UTF_8) : new byte[0];
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