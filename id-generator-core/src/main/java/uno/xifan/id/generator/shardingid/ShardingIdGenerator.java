package uno.xifan.id.generator.shardingid;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

/**
 * 分片ID生成器实现
 *
 * <p>
 * 基于Instagram的ID生成算法实现的分布式ID生成器。
 * 该生成器通过分片机制来保证ID的唯一性和有序性。
 *
 * <p>
 * 特点：
 * 1. 分布式友好：支持多节点部署
 * 2. 高性能：通过分片机制提高ID生成效率
 * 3. 有序性：生成的ID保持时间顺序
 *
 * @author congee
 */
public class ShardingIdGenerator implements IdGenerator {

    /**
     * Instagram ID生成器实例，使用分片ID 0
     */
    private static final InstagramIdGenerator idGenerator = new InstagramIdGenerator(0);

    @Override
    public Long generate() {
        return idGenerator.generateId();
    }

    @Override
    public IdType idType() {
        return IdType.ShardingID;
    }
}
