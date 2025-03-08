package icu.congee.id.generator.js;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成适用于 JavaScript 的安全 ID 的生成器类。
 * 生成的 ID 为 53 位，这种长度的 ID 可以在 JavaScript 中安全使用，因为 JavaScript 中 Number 类型的整数安全范围是 53 位。
 *
 * ID 结构：
 * - 高位 37 位：时间戳部分，将当前时间戳除以 16 后存储，这样做可以延长时间戳的使用年限，避免在短时间内出现溢出。
 * - 低位 16 位：随机数部分，用于增加 ID 的唯一性。
 *
 * 生成算法：
 * 1. 获取当前时间戳（以毫秒为单位）。
 * 2. 将时间戳右移 4 位，相当于除以 16。
 * 3. 将处理后的时间戳左移 16 位，为随机数部分腾出空间。
 * 4. 生成一个 16 位的随机数。
 * 5. 将处理后的时间戳和随机数进行按位或操作，组合成最终的 53 位 ID。
 */
public class JavaScriptSafetyIdGenerator implements IdGenerator {
    private Random random = ThreadLocalRandom.current();
    private long epoch = 1645557742L;


    public JavaScriptSafetyIdGenerator() {
    }

    public JavaScriptSafetyIdGenerator(long epoch) {
        this.epoch = epoch;
    }

    /**
     * 构造函数，传入一个 Random 对象用于生成随机数。
     * @param random 随机数生成器对象
     */
    /**
     * 构造函数，使用默认纪元时间初始化生成器
     * @param random 随机数生成器对象
     */
    public JavaScriptSafetyIdGenerator(Random random) {
        this.random = random;
    }

    /**
     * 构造函数，使用指定的纪元时间初始化生成器
     * @param random 随机数生成器对象
     * @param epoch 自定义的纪元时间戳（毫秒）
     */
    public JavaScriptSafetyIdGenerator(Random random, long epoch) {
        this.random = random;
        this.epoch = epoch;
    }

    /**
     * 静态方法，用于生成一个 53 位的 JavaScript 安全 ID。
     * @return 生成的 53 位 ID
     */
    public static long next() {
        return next(1)[0];
    }

    private static final AtomicLong atomicLong = new AtomicLong(System.currentTimeMillis() << 2);
    public static long monotonic() {
        return atomicLong.getAndIncrement();
    }

    /**
     * 批量生成指定数量的 53 位 JavaScript 安全 ID。
     * @param count 需要生成的 ID 数量
     * @return 生成的 53 位 ID 数组
     * @throws IllegalArgumentException 如果 count 小于等于 0
     */
    public static long[] next(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("生成数量必须大于0");
        }
        if (count > 1024) {
            throw new IllegalArgumentException("生成数量不能超过1024");
        }

        long[] ids = new long[count];
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // 步骤 1：获取当前时间戳（相对于纪元时间）
        long currentTime = System.currentTimeMillis() - 1645557742L;
        // 步骤 2：将时间戳右移 4 位，相当于除以 16
        long timestampPart = currentTime >> 4;
        // 步骤 3：将处理后的时间戳左移 16 位，为随机数部分腾出空间
        timestampPart = timestampPart << 16;

        // 步骤 4：批量生成随机数部分并组合成最终的 ID
        for (int i = 0; i < count; i++) {
            int randomPart = random.nextInt() & 0xFFFF;
            ids[i] = timestampPart | randomPart;
        }

        return ids;
    }

    /**
     * 实现 IdGenerator 接口的方法，用于生成一个 53 位的 JavaScript 安全 ID。
     * @return 生成的 53 位 ID
     */
    @Override
    public Object generate() {
        // 步骤 1：获取当前时间戳（相对于纪元时间）
        long currentTime = System.currentTimeMillis() - epoch;
        // 步骤 2：将时间戳右移 4 位，相当于除以 16
        long timestampPart = currentTime >> 4;
        // 步骤 3：将处理后的时间戳左移 16 位，为随机数部分腾出空间
        timestampPart = timestampPart << 16;
        // 步骤 4：使用传入的 Random 对象生成一个 16 位的随机数
        int randomPart = random.nextInt() & 0xFFFF;
        // 步骤 5：将处理后的时间戳和随机数进行按位或操作，组合成最终的 53 位 ID
        return timestampPart | randomPart;
    }

    /**
     * 获取生成的 ID 类型。
     * @return ID 类型为 JavaScriptSafetyID
     */
    @Override
    public IdType idType() {
        return IdType.JavaScriptSafetyID;
    }
}