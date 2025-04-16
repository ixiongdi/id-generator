package icu.congee.id.base;

/**
 * 使用数字、大写字母实现Base36编解码
 */
public class Base36 {

    private static final byte[] ENCODE_MAP = new byte[36];
    private static final byte[] DECODE_MAP = new byte[128];

    static {
        // 初始化编码映射表（0-9, A-Z）
        int index = 0;
        for (byte i = '0'; i <= '9'; i++) {
            ENCODE_MAP[index++] = i;
        }
        for (byte i = 'A'; i <= 'Z'; i++) {
            ENCODE_MAP[index++] = i;
        }

        // 初始化解码映射表
        for (int i = 0; i < ENCODE_MAP.length; i++) {
            DECODE_MAP[ENCODE_MAP[i]] = (byte) i;
            // 支持小写字母解码
            if (Character.isLetter((char) ENCODE_MAP[i])) {
                DECODE_MAP[Character.toLowerCase((char) ENCODE_MAP[i])] = (byte) i;
            }
        }
    }

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return String.valueOf((char) ENCODE_MAP[0]);
        }

        StringBuilder result = new StringBuilder();
        int value = 0;
        int bits = 0;

        for (byte b : bytes) {
            value = (value << 8) | (b & 0xFF);
            bits += 8;

            while (bits >= 6) { // 每6位可以表示36个字符
                bits -= 6;
                int index = (value >> bits) & 0x3F;
                if (index >= 36) {
                    index = 35; // 处理溢出情况
                }
                result.append((char) ENCODE_MAP[index]);
                value &= ((1 << bits) - 1);
            }
        }

        // 处理剩余的位
        if (bits > 0) {
            int index = (value << (6 - bits)) & 0x3F;
            if (index >= 36) {
                index = 35;
            }
            result.append((char) ENCODE_MAP[index]);
        }

        return result.length() > 0 ? result.toString() : String.valueOf((char) ENCODE_MAP[0]);
    }

    public static byte[] decode(String base36) {
        if (base36 == null || base36.isEmpty()) {
            throw new IllegalArgumentException("Input string cannot be null or empty");
        }

        // 计算所需的字节数组大小
        int bitsPerChar = 6; // 每个Base36字符最多表示6位
        int totalBits = base36.length() * bitsPerChar;
        int byteCount = (totalBits + 7) / 8; // 向上取整
        byte[] bytes = new byte[byteCount];

        int bitBuffer = 0;
        int bitsInBuffer = 0;
        int byteIndex = 0;

        for (int i = 0; i < base36.length(); i++) {
            char c = base36.charAt(i);
            if (c >= DECODE_MAP.length || DECODE_MAP[c] < 0) {
                throw new IllegalArgumentException("Invalid character in Base36 string: " + c);
            }

            bitBuffer = (bitBuffer << 6) | DECODE_MAP[c];
            bitsInBuffer += 6;

            while (bitsInBuffer >= 8 && byteIndex < bytes.length) {
                bitsInBuffer -= 8;
                bytes[byteIndex++] = (byte) ((bitBuffer >> bitsInBuffer) & 0xFF);
            }
        }

        // 处理剩余的位
        if (bitsInBuffer > 0 && byteIndex < bytes.length) {
            bytes[byteIndex] = (byte) ((bitBuffer << (8 - bitsInBuffer)) & 0xFF);
        }

        return bytes;
    }
}
