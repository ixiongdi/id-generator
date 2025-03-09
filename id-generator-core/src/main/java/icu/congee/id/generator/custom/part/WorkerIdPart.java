package icu.congee.id.generator.custom.part;

import icu.congee.id.generator.custom.IdPartGenerator;

/**
 * 工作节点ID部分实现类
 * <p>
 * 该类继承自IdPart抽象类，实现了工作节点ID部分的生成逻辑。
 * 支持固定值和自定义生成器两种方式。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class WorkerIdPart extends IdPart {
    
    // 工作节点ID生成器
    private final IdPartGenerator<Long> generator;
    
    /**
     * 使用固定值创建工作节点ID部分
     *
     * @param bits 工作节点ID占用的位数
     * @param workerId 固定的工作节点ID值
     */
    public WorkerIdPart(int bits, long workerId) {
        this(bits, () -> workerId);
        
        // 验证工作节点ID是否在有效范围内
        if (workerId < 0 || workerId > maxValue) {
            throw new IllegalArgumentException("工作节点ID必须不小于0且不超过" + maxValue);
        }
    }
    
    /**
     * 使用自定义生成器创建工作节点ID部分
     *
     * @param bits 工作节点ID占用的位数
     * @param generator 自定义工作节点ID生成器
     */
    public WorkerIdPart(int bits, IdPartGenerator<Long> generator) {
        super(bits);
        this.generator = generator;
    }
    
    /**
     * 生成工作节点ID值
     *
     * @return 生成的工作节点ID值
     */
    @Override
    public long generate() {
        return generator.generate();
    }
}