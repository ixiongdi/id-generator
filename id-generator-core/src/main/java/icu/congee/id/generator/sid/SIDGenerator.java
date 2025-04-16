package icu.congee.id.generator.sid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;
import icu.congee.id.util.TimeUtils;

import java.util.Base64;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 基于时间戳和随机数的字符串ID生成器
 * <p>
 * 该生成器通过组合当前时间戳和随机数，生成一个Base64编码的字符串ID。
 * 生成的ID格式为：Base64(timestamp)-Base64(randomNum)
 * </p>
 *
 * @author congee
 * @since 1.0.0
 */
public class SIDGenerator implements IdGenerator {
    private static final Random random = ThreadLocalRandom.current();

    private static byte[] longToBytes(long value) {
        byte[] bytes = new byte[8];
        bytes[0] = (byte) (value >>> 56);
        bytes[1] = (byte) (value >>> 48);
        bytes[2] = (byte) (value >>> 40);
        bytes[3] = (byte) (value >>> 32);
        bytes[4] = (byte) (value >>> 24);
        bytes[5] = (byte) (value >>> 16);
        bytes[6] = (byte) (value >>> 8);
        bytes[7] = (byte) value;
        return bytes;
    }

    /**
     * 生成一个新的字符串ID
     * <p>
     * 该方法将当前时间戳（纳秒级）和一个随机长整数转换为字节数组，
     * 然后对这些字节数组进行Base64编码，并用连字符（-）连接。
     * </p>
     *
     * @return 生成的字符串ID，格式为Base64(timestamp)-Base64(randomNum)
     */
    @Override
    public Object generate() {
        byte[] timestamp = longToBytes(TimeUtils.getCurrentUnixNano());
        byte[] randomNum = longToBytes(random.nextLong());
        return Base64.getEncoder().encodeToString(timestamp) + "-" + Base64.getEncoder().encodeToString(randomNum);
    }

    /**
     * 获取ID生成器类型
     *
     * @return 返回SID类型的枚举值
     */
    @Override
    public IdType idType() {
        return IdType.SID;
    }
}
