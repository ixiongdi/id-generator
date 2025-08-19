package uno.xifan.id.generator.nano;

import uno.xifan.id.base.IdGenerator;
import uno.xifan.id.base.IdType;

import java.security.SecureRandom;
import java.util.Random;

/**
 * NanoId生成器
 * <p>
 * NanoId是一个小型、安全、URL友好的唯一字符串ID生成器。
 * 它生成的ID比UUID更短，默认为21个字符，同时保持足够的随机性和唯一性。
 * </p>
 * <p>
 * 特点：
 * - 短小：默认21个字符，比UUID的36个字符短
 * - 安全：使用加密安全的随机数生成器
 * - URL友好：使用URL安全的字符集
 * - 可定制：可自定义长度和字符集
 * </p>
 * 
 * @author ixiongdi
 * @version 1.0
 * @since 2024-05-01
 */
public class NanoIdGenerator implements IdGenerator {
    
    /**
     * 默认字符集（URL友好）
     * 包含大小写字母、数字，不包含特殊字符和易混淆字符
     */
    private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    
    /**
     * 默认ID长度
     */
    private static final int DEFAULT_SIZE = 21;
    
    /**
     * 默认随机数生成器
     */
    private static final SecureRandom DEFAULT_RANDOM = new SecureRandom();

    public static String next() {
        byte[] randomBytes = new byte[DEFAULT_SIZE];
        DEFAULT_RANDOM.nextBytes(randomBytes);
        
        char[] buffer = new char[DEFAULT_SIZE];
        for (int i = 0; i < DEFAULT_SIZE; i++) {
            buffer[i] = DEFAULT_ALPHABET[(randomBytes[i] & 0xFF) % DEFAULT_ALPHABET.length];
        }
        return new String(buffer);
    }

    @Override
    public Object generate() {
        return next();
    }
    @Override
    public IdType idType() {
        return IdType.NanoId;
    }
    
    
    }