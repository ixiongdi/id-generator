package icu.congee.id.base;

import java.util.HashMap;
import java.util.Map;

public class CrockfordBase32 {
    private static final String ALPHABET = "0123456789ABCDEFGHJKMNPQRSTVWXYZ";
    private static final int BASE = 32;
    private static final Map<Character, Integer> DECODING_MAP = new HashMap<>();

    static {
        for (int i = 0; i < ALPHABET.length(); i++) {
            char c = ALPHABET.charAt(i);
            DECODING_MAP.put(c, i);
            DECODING_MAP.put(Character.toLowerCase(c), i);
        }
        DECODING_MAP.put('I', 1);
        DECODING_MAP.put('i', 1);
        DECODING_MAP.put('L', 1);
        DECODING_MAP.put('l', 1);
        DECODING_MAP.put('O', 0);
        DECODING_MAP.put('o', 0);
    }

    public static String encode(byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int buffer = 0;
        int bitsLeft = 0;

        for (byte b : data) {
            buffer = (buffer << 8) | (b & 0xff);
            bitsLeft += 8;
            while (bitsLeft >= 5) {
                result.append(ALPHABET.charAt((buffer >> (bitsLeft - 5)) & 0x1f));
                bitsLeft -= 5;
            }
        }

        if (bitsLeft > 0) {
            buffer <<= (5 - bitsLeft);
            result.append(ALPHABET.charAt(buffer & 0x1f));
        }

        return result.toString();
    }

    public static byte[] decode(String encoded) {
        if (encoded == null || encoded.length() == 0) {
            return new byte[0];
        }
        encoded = encoded.replace("-", "");
        int bitLength = encoded.length() * 5;
        int byteLength = (bitLength + 7) / 8;
        byte[] result = new byte[byteLength];
        int buffer = 0;
        int bitsLeft = 0;
        int index = 0;

        for (char c : encoded.toCharArray()) {
            if (!DECODING_MAP.containsKey(c)) {
                throw new IllegalArgumentException("Invalid character in Base32 string: " + c);
            }
            buffer = (buffer << 5) | DECODING_MAP.get(c);
            bitsLeft += 5;
            if (bitsLeft >= 8) {
                result[index++] = (byte) ((buffer >> (bitsLeft - 8)) & 0xff);
                bitsLeft -= 8;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        byte[] originalData = "Hello, World!".getBytes();
        String encoded = encode(originalData);
        byte[] decoded = decode(encoded);
        System.out.println("Original data: " + new String(originalData));
        System.out.println("Encoded data: " + encoded);
        System.out.println("Decoded data: " + new String(decoded));
    }
}