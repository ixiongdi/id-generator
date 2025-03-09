package icu.congee.id.generator.custom;

import icu.congee.id.base.IdGenerator;

/**
 * 长整型ID生成器接口
 * <p>
 * 该接口定义了生成64位长整型ID的方法，支持自定义各部分的生成方式和位数分配。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public interface LongIdGenerator extends IdGenerator {
    
    /**
     * 生成一个64位的长整型ID
     *
     * @return 生成的长整型ID
     */
    @Override
    Long generate();
    
    /**
     * 使用指定的格式生成ID
     * <p>
     * 格式字符串中可以包含以下占位符：
     * <ul>
     *   <li>{ts} - 时间戳部分</li>
     *   <li>{wid} - 工作节点ID部分</li>
     *   <li>{seq} - 序列号部分</li>
     *   <li>{rnd} - 随机数部分</li>
     * </ul>
     *
     * @param format 格式字符串，定义ID各部分的组合方式
     * @return 按照指定格式生成的ID
     */
    Long generateWithFormat(String format);
    
    /**
     * 获取当前生成器的时间戳部分
     *
     * @return 时间戳部分的值
     */
    long getTimestampPart();
    
    /**
     * 获取当前生成器的工作节点ID部分
     *
     * @return 工作节点ID部分的值
     */
    long getWorkerIdPart();
    
    /**
     * 获取当前生成器的序列号部分
     *
     * @return 序列号部分的值
     */
    long getSequencePart();
    
    /**
     * 获取当前生成器的随机数部分
     *
     * @return 随机数部分的值
     */
    long getRandomPart();
}