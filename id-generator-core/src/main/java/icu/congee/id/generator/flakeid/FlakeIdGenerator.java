package icu.congee.id.generator.flakeid;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.base.IdType;

import java.nio.ByteBuffer;

/**
 * Flake ID生成器的Java实现
 *
 * <p>Flake ID是一种分布式ID生成算法，其结构如下：
 * - 时间戳（42位）
 * - 生成器标识符（10位）
 * - 序列号（12位）
 *
 * <p>特点：
 * 1. 时间戳占42位，以毫秒为单位，可使用约139年
 * 2. 生成器标识符占10位，最多支持1024个节点
 * 3. 序列号占12位，每毫秒最多生成4096个ID
 *
 * @author ixiongdi
 */
public class FlakeIdGenerator implements IdGenerator {

    private static final FlakeId.Options options = new FlakeId.Options();
    
    private static final FlakeId flakeId;
    
    static {
        flakeId = new FlakeId(options);
    }
    
    @Override
    public Long generate() {
        try {
            byte[] id = flakeId.next();
            return ByteBuffer.wrap(id).getLong();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate Flake ID", e);
        }
    }
    
    @Override
    public IdType idType() {
        return IdType.FlakeID;
    }
}