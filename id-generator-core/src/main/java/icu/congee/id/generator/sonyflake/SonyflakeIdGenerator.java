package icu.congee.id.generator.sonyflake;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

/**
 * Sonyflake ID生成器的Java实现
 *
 * <p>Sonyflake是Sony公司开源的分布式ID生成算法，其结构如下： - 39位时间戳（精确到10ms，以自定义纪元为基准） - 8位工作机器ID - 8位序列号 -
 * 8位备用位（默认为0）
 *
 * <p>特点： 1. 时间戳占39位，以10ms为单位，可使用174年 2. 工作机器ID占8位，最多支持256个节点 3. 序列号占8位，每10ms最多生成256个ID 4.
 * 预留8位备用位，可用于业务扩展
 *
 * @author ixiongdi
 */
public class SonyflakeIdGenerator implements IdGenerator {

    private static final Sonyflake.Settings settings = new Sonyflake.Settings();

    private static final Sonyflake sonyflake;

    static {
        try {
            sonyflake = Sonyflake.newInstance(settings);
        } catch (Sonyflake.StartTimeAheadException e) {
            throw new RuntimeException(e);
        } catch (Sonyflake.NoPrivateAddressException e) {
            throw new RuntimeException(e);
        } catch (Sonyflake.InvalidMachineIDException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long generate() {
        try {
            return sonyflake.nextID();
        } catch (Sonyflake.OverTimeLimitException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public IdType idType() {
        return IdType.Sonyflake;
    }
}
