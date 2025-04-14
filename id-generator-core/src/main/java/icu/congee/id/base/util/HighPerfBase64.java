package icu.congee.id.base.util;

import java.util.Arrays;

public class HighPerfBase64 {
    private static final byte[] ENCODE_TABLE_STD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes();
    private static final byte[] ENCODE_TABLE_URL = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".getBytes();
    private static final byte[] DECODE_TABLE = new byte[128];
    
    static {
        Arrays.fill(DECODE_TABLE, (byte) -1);
        for (int i = 0; i < ENCODE_TABLE_STD.length; i++) {
            DECODE_TABLE[ENCODE_TABLE_STD[i]] = (byte) i;
        }
        DECODE_TABLE['-'] = 62;
        DECODE_TABLE['_'] = 63;
    }

    // 编码方法
    public static String encode(byte[] data, Mode mode) {
        if (data == null) throw new IllegalArgumentException("Input data cannot be null");
        
        byte[] table = (mode == Mode.URL_SAFE) ? ENCODE_TABLE_URL : ENCODE_TABLE_STD;
        int inputLen = data.length;
        int outputLen = ((inputLen + 2) / 3) * 4;
        byte[] encoded = new byte[outputLen];
        
        int inPos = 0, outPos = 0;
        int remaining;
        while (inPos + 3 <= inputLen) {
            int b1 = data[inPos++] & 0xFF;
            int b2 = data[inPos++] & 0xFF;
            int b3 = data[inPos++] & 0xFF;
            
            encoded[outPos++] = table[(b1 >>> 2) & 0x3F];
            encoded[outPos++] = table[((b1 << 4) & 0x30) | ((b2 >>> 4) & 0x0F)];
            encoded[outPos++] = table[((b2 << 2) & 0x3C) | ((b3 >>> 6) & 0x03)];
            encoded[outPos++] = table[b3 & 0x3F];
        }
        
        // 处理剩余字节
        remaining = inputLen - inPos;
        if (remaining > 0) {
            int b1 = data[inPos++] & 0xFF;
            int b2 = (remaining == 2) ? data[inPos++] & 0xFF : 0;
            
            encoded[outPos++] = table[(b1 >>> 2) & 0x3F];
            encoded[outPos++] = table[((b1 << 4) & 0x30) | ((b2 >>> 4) & 0x0F)];
            encoded[outPos++] = (byte) ((remaining == 2) ? table[((b2 << 2) & 0x3C)] : '=');
            encoded[outPos++] = '=';
        }
        
        return new String(encoded);
    }

    // 解码方法
    public static byte[] decode(String base64) {
        if (base64 == null) throw new IllegalArgumentException("Input string cannot be null");
        
        byte[] data = base64.getBytes();
        int inputLen = data.length;
        if (inputLen % 4 != 0) throw new IllegalArgumentException("Invalid Base64 length");
        
        int padding = 0;
        if (inputLen > 0) {
            if (data[inputLen - 1] == '=') padding++;
            if (data[inputLen - 2] == '=') padding++;
        }
        int outputLen = (inputLen * 3) / 4 - padding;
        byte[] decoded = new byte[outputLen];
        
        int inPos = 0, outPos = 0;
        int block;
        for (int i = 0; i < inputLen; i += 4) {
            block = 0;
            int segmentCount = Math.min(4, inputLen - i);
            
            for (int j = 0; j < segmentCount; j++) {
                byte val = DECODE_TABLE[data[inPos + j]];
                if (val < 0) throw new IllegalArgumentException("Invalid Base64 character: " + (char)data[inPos + j]);
                block |= (val & 0x3F) << (18 - j * 6);
            }
            
            decoded[outPos++] = (byte) ((block >>> 16) & 0xFF);
            if (segmentCount > 2)
                decoded[outPos++] = (byte) ((block >>> 8) & 0xFF);
            if (segmentCount > 3)
                decoded[outPos++] = (byte) (block & 0xFF);
            inPos += 4;
        }
        
        return decoded;
    }

    public enum Mode { STANDARD, URL_SAFE }

    // 测试用例
    public static void main(String[] args) {
        // 标准编码测试
        String test1 = encode("Hello".getBytes(), Mode.STANDARD);
        System.out.println("Standard: " + test1);  // SGVsbG8=
        
        // URL安全编码测试
        String test2 = encode("Java>8".getBytes(), Mode.URL_SAFE);
        System.out.println("URL Safe: " + test2);  // SmF2YT4y
        
        // 解码测试
        byte[] decoded = decode("SGVsbG8=");
        System.out.println("Decoded: " + new String(decoded));  // Hello
        
        // 异常测试
        try {
            decode("Invalid@base64");
        } catch (IllegalArgumentException e) {
            System.out.println("Caught: " + e.getMessage());
        }
    }
}