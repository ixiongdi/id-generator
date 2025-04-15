package icu.congee.id.base.util;

import java.util.Arrays;

public class CrockfordBase32Upper {

    // 使用大写字母的Crockford Base32字母表
    private static final byte[] ENCODING_TABLE = {
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
            'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
    };

    // 解码查找表（ASCII范围）
    private static final byte[] DECODING_TABLE = new byte[128];
    
    static {
        // 初始化解码表（填充-1表示无效字符）
        Arrays.fill(DECODING_TABLE, (byte) -1);
        
        // 填充有效字符（大写字母表）
        for (byte i = 0; i < ENCODING_TABLE.length; i++) {
            byte c = ENCODING_TABLE[i];
            DECODING_TABLE[c] = i;
            // 支持小写字母输入
            if (c >= 'A' && c <= 'Z') {
                DECODING_TABLE[Character.toLowerCase(c)] = i;
            }
        }
        
        // 处理同义字符（根据Crockford规范）
        DECODING_TABLE['o'] = DECODING_TABLE['0'];
        DECODING_TABLE['O'] = DECODING_TABLE['0'];
        DECODING_TABLE['i'] = DECODING_TABLE['1'];
        DECODING_TABLE['I'] = DECODING_TABLE['1'];
        DECODING_TABLE['l'] = DECODING_TABLE['1'];
        DECODING_TABLE['L'] = DECODING_TABLE['1'];
    }

    /**
     * 编码字节数组为Crockford Base32字节数组（大写）
     * @param data 要编码的字节数组
     * @return 编码后的字节数组（大写）
     */
    public static byte[] encodeToBytes(byte[] data) {
        if (data == null || data.length == 0) {
            return new byte[0];
        }

        // 计算输出长度：每5位编码为一个字符
        int outputLength = (data.length * 8 + 4) / 5;
        byte[] result = new byte[outputLength];
        
        int buffer = 0;
        int bitsLeft = 0;
        int pos = 0;
        
        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bitsLeft += 8;
            
            while (bitsLeft >= 5) {
                bitsLeft -= 5;
                int index = (buffer >> bitsLeft) & 0x1F;
                result[pos++] = ENCODING_TABLE[index];
            }
        }
        
        // 处理剩余位
        if (bitsLeft > 0) {
            int index = (buffer << (5 - bitsLeft)) & 0x1F;
            result[pos] = ENCODING_TABLE[index];
        }

        return result;
    }

    /**
     * 编码字节数组为Crockford Base32字符串（大写）
     * @param data 要编码的字节数组
     * @return 编码后的字符串（大写）
     */
    public static String encode(byte[] data) {
        return new String(encodeToBytes(data));
    }

    /**
     * 解码Crockford Base32字节数组为原始字节数组
     * @param encoded 编码后的字节数组（大小写不敏感）
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 如果输入包含无效字符
     */
    public static byte[] decodeFromBytes(byte[] encoded) throws IllegalArgumentException {
        if (encoded == null || encoded.length == 0) {
            return new byte[0];
        }

        // 预处理：移除连字符，统一处理大小写
        int cleanLength = 0;
        byte[] clean = new byte[encoded.length];
        for (byte b : encoded) {
            if (b == '-') continue;
            if (b >= 'a' && b <= 'z') {
                b = (byte) Character.toUpperCase(b);
            }
            clean[cleanLength++] = b;
        }
        
        if (cleanLength == 0) {
            return new byte[0];
        }

        // 计算输出长度：每字符5位
        int outputLength = (cleanLength * 5) / 8;
        byte[] result = new byte[outputLength];
        
        int buffer = 0;
        int bitsLeft = 0;
        int pos = 0;
        
        for (int i = 0; i < cleanLength; i++) {
            byte c = clean[i];
            if (c >= DECODING_TABLE.length) {
                throw new IllegalArgumentException("非法字符: " + (char)c);
            }
            
            byte value = DECODING_TABLE[c];
            if (value == -1) {
                throw new IllegalArgumentException("非法字符: " + (char)c);
            }
            
            buffer = (buffer << 5) | value;
            bitsLeft += 5;
            
            if (bitsLeft >= 8) {
                bitsLeft -= 8;
                result[pos++] = (byte) ((buffer >> bitsLeft) & 0xFF);
            }
        }
        
        return result;
    }

    /**
     * 解码Crockford Base32字符串为原始字节数组
     * @param encoded 编码后的字符串（大小写不敏感）
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 如果输入包含无效字符
     */
    public static byte[] decode(String encoded) throws IllegalArgumentException {
        return decodeFromBytes(encoded.getBytes());
    }

    /**
     * 计算Crockford校验和字节（大写）
     * @param data 要计算校验和的数据
     * @return 校验和字节（大写）
     */
    public static byte checksumByte(byte[] data) {
        if (data == null || data.length == 0) {
            return ENCODING_TABLE[0];
        }
        
        int sum = 0;
        for (byte b : data) {
            sum += b & 0xFF;
        }
        
        return ENCODING_TABLE[sum % 37];
    }

    /**
     * 计算Crockford校验和字符（大写）
     * @param data 要计算校验和的数据
     * @return 校验和字符（大写）
     */
    public static char checksum(byte[] data) {
        return (char) checksumByte(data);
    }

    /**
     * 验证带校验和的Crockford Base32字节数组
     * @param encodedWithChecksum 带校验和的编码字节数组
     * @return 如果校验和有效返回true
     */
    public static boolean validateBytes(byte[] encodedWithChecksum) {
        if (encodedWithChecksum == null || encodedWithChecksum.length < 2) {
            return false;
        }
        
        byte[] dataPart = Arrays.copyOf(encodedWithChecksum, encodedWithChecksum.length - 1);
        byte expectedChecksum = encodedWithChecksum[encodedWithChecksum.length - 1];
        
        try {
            byte[] decoded = decodeFromBytes(dataPart);
            return checksumByte(decoded) == expectedChecksum;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 验证带校验和的Crockford Base32字符串
     * @param encodedWithChecksum 带校验和的编码字符串
     * @return 如果校验和有效返回true
     */
    public static boolean validate(String encodedWithChecksum) {
        return validateBytes(encodedWithChecksum.getBytes());
    }
}