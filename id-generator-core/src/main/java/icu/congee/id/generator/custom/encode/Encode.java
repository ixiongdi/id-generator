package icu.congee.id.generator.custom.encode;

/**
 * 编码接口
 * <p>
 * 该接口定义了将字节数组编码为字符串的方法。
 * 可以用于自定义ID生成器中对ID各部分进行编码处理。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public interface Encode {
    
    /**
     * 将字节数组编码为字符串
     *
     * @param bytes 要编码的字节数组
     * @return 编码后的字符串
     */
    String encode(byte[] bytes);
}