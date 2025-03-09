package icu.congee.id.generator.custom.encode;

import java.util.Base64;

/**
 * Base64编码实现类
 * <p>
 * 该类实现了Encode接口，提供将字节数组编码为Base64字符串的功能。
 * 作为自定义ID生成器的默认编码实现。
 *
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class Base64Encode implements Encode {
    
    /**
     * 将字节数组编码为Base64字符串
     *
     * @param bytes 要编码的字节数组
     * @return 编码后的Base64字符串
     */
    @Override
    public String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        return Base64.getEncoder().encodeToString(bytes);
    }
}