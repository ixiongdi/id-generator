package icu.congee.id.generator.combguid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.time.Instant;
import java.util.UUID;

/**
 * CombGuid生成器
 * <p>
 * 基于RT.Comb的实现，将时间信息编码到UUID中，使其可按时间排序。
 * CombGuid通过重新排列标准UUID的字节，将时间戳信息放在开头，从而实现按时间排序的功能。
 * </p>
 *
 * <p>CombGuid结构：
 * <ul>
 *   <li>前6字节：Unix时间戳（精确到毫秒）</li>
 *   <li>后10字节：随机UUID数据</li>
 * </ul>
 * </p>
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class CombGuidGenerator implements IdGenerator {

    /**
     * 生成一个新的CombGuid
     * <p>
     * 该方法首先生成一个随机UUID，然后将当前时间戳编码到其中，
     * 确保生成的ID既保持唯一性又具有时间顺序性。
     * </p>
     *
     * @return 新生成的CombGuid字符串
     */
    public static UUID next() {
        UUID uuid = UUID.randomUUID();
        long timestamp = Instant.now().toEpochMilli();
        
        // 获取UUID的most significant bits和least significant bits
        long msb = uuid.getMostSignificantBits();
        long lsb = uuid.getLeastSignificantBits();
        
        // 将时间戳编码到UUID的前6字节中
        // 保留UUID版本（4）和变体位
        long newMsb = ((timestamp & 0x0000FFFFFFFFFFFFL) << 16) |
                     (msb & 0x000000000000FFFFL);
        
        return new UUID(newMsb, lsb);
    }

    @Override
    public String generate() {
        return next().toString();
    }

    @Override
    public IdType idType() {
        return IdType.COMBGUID;
    }
}