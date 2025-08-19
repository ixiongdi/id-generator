package uno.xifan.id.generator.util;

public class CrockfordBase32Encoder {
    // Crockford Base32字符表（RFC 4648变体）
    private static final char[] CROCKFORD_CHARS = {
        '0', '1', '2', '3', '4', '5', '6', '7',
        '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
        'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q',
        'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'
    };

    // 查找表（5位 → Crockford字符）
    private static final char[] LOOKUP_TABLE = new char[32];
    static {
        System.arraycopy(CROCKFORD_CHARS, 0, LOOKUP_TABLE, 0, 32);
    }

    /**
     * 将80位(10字节)数据编码为Crockford Base32字符串
     * @param input 10字节输入数组
     * @return 16字符Base32字符串
     */
    public static String encode80Bit(byte[] input) {
        if (input.length != 10) {
            throw new IllegalArgumentException("输入必须是10字节(80位)");
        }

        char[] output = new char[16];
        
        // 将输入字节转换为位缓冲区（80位）
        int buffer = 0;
        int bufferPos = 40; // 从最高位开始填充
        
        for (byte b : input) {
            // 将字节转换为无符号整数
            int byteValue = b & 0xFF;
            
            // 将字节放入缓冲区的正确位置
            buffer |= byteValue << bufferPos;
            bufferPos -= 8;
        }

        // 处理所有5位组
        for (int i = 0; i < 16; i++) {
            // 提取5位组（从最高有效位开始）
            int index = (buffer >>> (35 - i*5)) & 0x1F;
            
            // 查找对应的Crockford字符
            output[i] = LOOKUP_TABLE[index];
        }

        return new String(output);
    }

    public static void main(String[] args) {
        // 测试用例
        byte[] testData = new byte[10];
        new java.util.Random().nextBytes(testData); // 填充随机数据
        
        String encoded = encode80Bit(testData);
        System.out.println("Encoded: " + encoded);
        System.out.println("Length: " + encoded.length()); // 应始终为16
    }
}