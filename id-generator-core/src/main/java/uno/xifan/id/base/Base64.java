package uno.xifan.id.base;

import java.util.Arrays;

/**
 * 使用URL Safe字符集并使用ASCII排序
 */
public class Base64 {

    private static final char[] URL_SAFE_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
            'V', 'W', 'X', 'Y', 'Z',
            '_',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u',
            'v', 'w', 'x', 'y', 'z',
            '~' };

    private static final byte[] DECODE_TABLE = new byte[128];

    private static final byte[] ENCODE_MAP;
    private static final byte[] DECODE_MAP;

    static {
        Arrays.fill(DECODE_TABLE, (byte) -1);
        for (int i = 0; i < URL_SAFE_CHARS.length; i++) {
            DECODE_TABLE[URL_SAFE_CHARS[i]] = (byte) i;
        }

        ENCODE_MAP = new byte[64];
        for (int i = 0; i < 64; i++) {
            ENCODE_MAP[i] = (byte) URL_SAFE_CHARS[i];
        }

        DECODE_MAP = DECODE_TABLE;
    }

    public static String encode(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "";
        }

        int len = bytes.length;
        StringBuilder result = new StringBuilder((len * 8 + 5) / 6);

        int buffer = 0;
        int bufferBits = 0;
        int index = 0;

        while (index < len) {
            buffer = (buffer << 8) | (bytes[index++] & 0xFF);
            bufferBits += 8;

            while (bufferBits >= 6) {
                bufferBits -= 6;
                result.append((char) ENCODE_MAP[(buffer >> bufferBits) & 0x3F]);
            }
        }

        if (bufferBits > 0) {
            buffer <<= (6 - bufferBits);
            result.append((char) ENCODE_MAP[buffer & 0x3F]);
        }

        return result.toString();
    }

    public static byte[] decode(String str) {
        if (str == null || str.isEmpty()) {
            return new byte[0];
        }

        int len = str.length();
        byte[] result = new byte[len * 6 / 8];
        int resultIndex = 0;

        int buffer = 0;
        int bufferBits = 0;

        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c >= 128 || DECODE_MAP[c] == -1) {
                throw new IllegalArgumentException("Invalid Base64 character: " + c);
            }

            buffer = (buffer << 6) | DECODE_MAP[c];
            bufferBits += 6;

            if (bufferBits >= 8) {
                bufferBits -= 8;
                result[resultIndex++] = (byte) ((buffer >> bufferBits) & 0xFF);
            }
        }

        return Arrays.copyOf(result, resultIndex);
    }
}
