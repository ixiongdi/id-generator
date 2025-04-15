package icu.congee.id.base;

import java.util.Arrays;

public class Base16 {
    private static final char[] UPPER_HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };
    
    private static final char[] LOWER_HEX = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
    };
    
    private static final int[] DECODE = new int[128];
    
    static {
        Arrays.fill(DECODE, -1);
        for (int i = 0; i < 10; i++) {
            DECODE['0' + i] = i;
        }
        for (int i = 0; i < 6; i++) {
            DECODE['A' + i] = 10 + i;
            DECODE['a' + i] = 10 + i;
        }
    }

    /**
     * 将字节数组编码为Base16(十六进制)字符串(大写)
     * @param data 要编码的字节数组
     * @return 编码后的十六进制字符串
     */
    public static String encodeUpper(byte[] data) {
        return encode(data, UPPER_HEX);
    }
    
    /**
     * 将字节数组编码为Base16(十六进制)字符串(小写)
     * @param data 要编码的字节数组
     * @return 编码后的十六进制字符串
     */
    public static String encodeLower(byte[] data) {
        return encode(data, LOWER_HEX);
    }
    
    private static String encode(byte[] data, char[] hex) {
        if (data == null) {
            return null;
        }
        
        char[] out = new char[data.length << 1];
        for (int i = 0, j = 0; i < data.length; i++) {
            out[j++] = hex[(0xF0 & data[i]) >>> 4];
            out[j++] = hex[0x0F & data[i]];
        }
        return new String(out);
    }
    
    /**
     * 将Base16(十六进制)字符串解码为字节数组
     * @param hex 要解码的十六进制字符串
     * @return 解码后的字节数组
     * @throws IllegalArgumentException 如果输入不是有效的十六进制字符串
     */
    public static byte[] decode(String hex) {
        if (hex == null) {
            return null;
        }
        
        if ((hex.length() & 1) != 0) {
            throw new IllegalArgumentException("Invalid hex string length: " + hex.length());
        }
        
        char[] data = hex.toCharArray();
        byte[] out = new byte[data.length >> 1];
        
        for (int i = 0, j = 0; j < data.length; i++) {
            int high = charToDigit(data[j++]) << 4;
            int low = charToDigit(data[j++]);
            out[i] = (byte) (high | low);
        }
        
        return out;
    }
    
    private static int charToDigit(char ch) {
        if (ch >= 128) {
            throw new IllegalArgumentException("Invalid hex character: " + ch);
        }
        int digit = DECODE[ch];
        if (digit == -1) {
            throw new IllegalArgumentException("Invalid hex character: " + ch);
        }
        return digit;
    }
}