package uno.xifan.id.base;

import java.util.Arrays;

/**
 * 使用Crockford Base32进行编码
 */
public class Base32 {
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";

    private static final byte[] ENCODE_MAP;
    private static final byte[] DECODE_MAP;

    static {
        ENCODE_MAP = ALPHABET.getBytes();
        DECODE_MAP = new byte[128];
        Arrays.fill(DECODE_MAP, (byte) -1);
        for (int i = 0; i < ENCODE_MAP.length; i++) {
            DECODE_MAP[ENCODE_MAP[i]] = (byte) i;
            // 支持小写字母
            if (Character.isLetter((char) ENCODE_MAP[i])) {
                DECODE_MAP[Character.toLowerCase((char) ENCODE_MAP[i])] = (byte) i;
            }
        }
        // 特殊字符映射
        DECODE_MAP['I'] = DECODE_MAP['1'];
        DECODE_MAP['i'] = DECODE_MAP['1'];
        DECODE_MAP['L'] = DECODE_MAP['1'];
        DECODE_MAP['l'] = DECODE_MAP['1'];
        DECODE_MAP['O'] = DECODE_MAP['0'];
        DECODE_MAP['o'] = DECODE_MAP['0'];
    }

    public Base32() {
    }

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : bytes) {
            buffer = (buffer << 8) | (b & 0xff);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                result.append((char) ENCODE_MAP[(buffer >> (bitsLeft - 5)) & 0x1f]);
                bitsLeft -= 5;
            }
        }

        if (bitsLeft > 0) {
            buffer <<= (5 - bitsLeft);
            result.append((char) ENCODE_MAP[buffer & 0x1f]);
        }

        return result.toString();
    }

    public static byte[] decode(String str) {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }
        str = str.replace("-", "");
        int bitLength = str.length() * 5;
        int byteLength = (bitLength + 7) / 8;
        byte[] result = new byte[byteLength];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        for (char c : str.toCharArray()) {
            if (c >= DECODE_MAP.length || DECODE_MAP[c] < 0) {
                throw new IllegalArgumentException("Invalid character in Base32 string: " + c);
            }
            buffer = (buffer << 5) | DECODE_MAP[c];
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }

        return result;
    }
}
