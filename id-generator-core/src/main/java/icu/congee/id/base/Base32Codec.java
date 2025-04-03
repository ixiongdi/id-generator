package icu.congee.id.base;

import java.util.Arrays;

public class Base32Codec {
    private static final char[] ENCODE_TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567".toCharArray();
    private static final byte[] DECODE_TABLE = new byte[128];

    static {
        Arrays.fill(DECODE_TABLE, (byte) -1);
        for (int i = 0; i < ENCODE_TABLE.length; i++) {
            DECODE_TABLE[ENCODE_TABLE[i]] = (byte) i;
            // Support lowercase letters
            if (Character.isLetter(ENCODE_TABLE[i])) {
                DECODE_TABLE[Character.toLowerCase(ENCODE_TABLE[i])] = (byte) i;
            }
        }
    }

    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bufferLength = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xFF);
            bufferLength += 8;

            while (bufferLength >= 5) {
                bufferLength -= 5;
                int index = (buffer >> bufferLength) & 0x1F;
                result.append(ENCODE_TABLE[index]);
            }
        }

        if (bufferLength > 0) {
            int index = (buffer << (5 - bufferLength)) & 0x1F;
            result.append(ENCODE_TABLE[index]);
        }

        return result.toString();
    }

    public static byte[] decode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return new byte[0];
        }

        encoded = encoded.trim();
        int length = encoded.length();
        int buffer = 0;
        int bufferLength = 0;
        byte[] result = new byte[length * 5 / 8];
        int resultIndex = 0;

        for (int i = 0; i < length; i++) {
            char c = encoded.charAt(i);
            if (c >= DECODE_TABLE.length || DECODE_TABLE[c] < 0) {
                throw new IllegalArgumentException("Invalid character in Base32 string: " + c);
            }

            buffer = (buffer << 5) | DECODE_TABLE[c];
            bufferLength += 5;

            if (bufferLength >= 8) {
                bufferLength -= 8;
                result[resultIndex++] = (byte) ((buffer >> bufferLength) & 0xFF);
            }
        }

        return result;
    }
}