package com.github.ixiongdi.id.generator.nano;

import com.github.ixiongdi.id.base.IdGenerator;
import com.github.ixiongdi.id.base.IdType;

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
    
    /**
     * 生成默认长度（21个字符）的NanoId
     * 
     * @return 生成的NanoId字符串
     */
    public String generate() {
        return generate(DEFAULT_SIZE);
    }

    @Override
    public IdType idType() {
        return IdType.NANO_ID;
    }

    /**
     * 生成指定长度的NanoId
     * 
     * @param size NanoId的长度
     * @return 生成的NanoId字符串
     */
    public static String generate(int size) {
        return generate(DEFAULT_RANDOM, DEFAULT_ALPHABET, size);
    }
    
    /**
     * 使用自定义随机数生成器和字符集生成NanoId
     * 
     * @param random 随机数生成器
     * @param alphabet 字符集
     * @param size NanoId的长度
     * @return 生成的NanoId字符串
     */
    public static String generate(Random random, char[] alphabet, int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be greater than zero");
        }
        
        if (alphabet == null || alphabet.length == 0 || alphabet.length >= 256) {
            throw new IllegalArgumentException("Alphabet must contain between 1 and 255 symbols");
        }
        
        if (random == null) {
            throw new IllegalArgumentException("Random cannot be null");
        }
        
        int mask = (2 << (int) Math.floor(Math.log(alphabet.length - 1) / Math.log(2))) - 1;
        int step = (int) Math.ceil(1.6 * mask * size / alphabet.length);
        
        StringBuilder idBuilder = new StringBuilder(size);
        byte[] bytes = new byte[step];
        
        while (true) {
            random.nextBytes(bytes);
            
            for (int i = 0; i < step; i++) {
                int alphabetIndex = bytes[i] & mask;
                
                if (alphabetIndex < alphabet.length) {
                    idBuilder.append(alphabet[alphabetIndex]);
                    if (idBuilder.length() == size) {
                        return idBuilder.toString();
                    }
                }
            }
        }
    }
}