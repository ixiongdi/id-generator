package uno.xifan.id.generator.ordereduuid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.time.Instant;
import java.util.UUID;

/**
 * OrderedUuid生成器
 * <p>
 * 该生成器创建基于时间戳的有序UUID，通过将时间戳编码到UUID的前字节来实现排序功能。
 * 生成的UUID结构如下：
 * - 前6字节：Unix时间戳（精确到毫秒）
 * - 后10字节：随机UUID数据
 * </p>
 */
public class OrderedUuidGenerator implements IdGenerator {

    /**
     * 生成一个新的OrderedUuid
     * <p>
     * 该方法首先生成一个随机UUID，然后将当前时间戳编码到其中，
     * 确保生成的ID既保持唯一性又具有时间顺序性。
     * </p>
     *
     * @return 新生成的OrderedUuid
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
        return IdType.OrderedUuid;
    }
}