package uno.xifan.id.generator.mist;

import uno.xifan.id.base.IdType;

import java.security.SecureRandom;

/**
 * 薄雾算法安全实现
 * 使用synchronized和SecureRandom实现，确保线程安全和随机数安全性
 */
public class SecureMistGenerator implements MistGenerator {
    private static final int SALT_BIT = 8; // 随机因子二进制位数
    private static final int SALT_SHIFT = 8; // 随机因子移位数
    private static final int INCREAS_SHIFT = SALT_BIT + SALT_SHIFT; // 自增数移位数
    private static final int MAX_SALT_VALUE = 255; // 随机因子最大值

    private long increas = 1; // 自增数
    private final SecureRandom random = new SecureRandom(); // 安全随机数生成器

    /**
     * 生成唯一编号
     * 
     * @return 生成的唯一ID
     */
    @Override
    public synchronized Long generate() {
        random.ints();
        // 自增
        long increasValue = ++increas;

        // 获取随机因子数值
        long saltA = random.nextInt(MAX_SALT_VALUE + 1);
        long saltB = random.nextInt(MAX_SALT_VALUE + 1);

        // 通过位运算实现自动占位
        return (increasValue << INCREAS_SHIFT) | (saltA << SALT_SHIFT) | saltB;
    }

    /**
     * 获取单例实例
     */
    private static class SingletonHolder {
        private static final SecureMistGenerator INSTANCE = new SecureMistGenerator();
    }

    /**
     * 获取SecureMistGenerator的单例实例
     * 
     * @return SecureMistGenerator实例
     */
    public static SecureMistGenerator getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public IdType idType() {
        return IdType.MIST_ID;
    }
}