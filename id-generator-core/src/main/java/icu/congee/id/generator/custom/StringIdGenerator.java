package icu.congee.id.generator.custom;

import icu.congee.id.base.IdGenerator;
import icu.congee.id.generator.custom.encode.Encode;

/**
 * 字符串ID生成器接口
 * <p>
 * 该接口定义了生成字符串类型ID的方法，支持自定义各部分的生成方式和格式。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public interface StringIdGenerator extends IdGenerator {
    
    /**
     * 生成一个字符串类型的ID
     *
     * @return 生成的字符串ID
     */
    @Override
    String generate();
    
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
     * 例如："{ts}-{wid}-{seq}-{rnd}" 将生成类似 "1620000000-1-1-123456" 的ID
     *
     * @param format 格式字符串，定义ID各部分的组合方式
     * @return 按照指定格式生成的ID
     */
    String generateWithFormat(String format);
    
    /**
     * 使用指定的编码方式生成ID
     * <p>
     * 该方法将各部分转换为字节数组，然后使用指定的编码方式进行编码。
     *
     * @param encode 编码方式
     * @return 编码后的ID
     */
    String generateWithEncode(Encode encode);
    
    /**
     * 获取当前生成器的时间戳部分
     *
     * @return 时间戳部分的字符串表示
     */
    String getTimestampPart();
    
    /**
     * 获取当前生成器的工作节点ID部分
     *
     * @return 工作节点ID部分的字符串表示
     */
    String getWorkerIdPart();
    
    /**
     * 获取当前生成器的序列号部分
     *
     * @return 序列号部分的字符串表示
     */
    String getSequencePart();
    
    /**
     * 获取当前生成器的随机数部分
     *
     * @return 随机数部分的字符串表示
     */
    String getRandomPart();
    
    /**
     * 获取当前生成器的时间戳部分的字节表示
     *
     * @return 时间戳部分的字节数组
     */
    byte[] getTimestampBytes();
    
    /**
     * 获取当前生成器的工作节点ID部分的字节表示
     *
     * @return 工作节点ID部分的字节数组
     */
    byte[] getWorkerIdBytes();
    
    /**
     * 获取当前生成器的序列号部分的字节表示
     *
     * @return 序列号部分的字节数组
     */
    byte[] getSequenceBytes();
    
    /**
     * 获取当前生成器的随机数部分的字节表示
     *
     * @return 随机数部分的字节数组
     */
    byte[] getRandomBytes();
}