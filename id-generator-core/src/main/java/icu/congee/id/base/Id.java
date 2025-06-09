package icu.congee.id.base;



import java.math.BigInteger;
import java.nio.ByteBuffer;

public interface Id  {

    byte[] toBytes();

    long toLong();

    String toString();

    default byte[] long2bytes(long value) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte) (value & 0xFF);
            value >>= 8;
        }
        return result;
    }

    default byte[] longToBytes(long value) {
        return ByteBuffer.allocate(8).putLong(value).array();
    }

    default String toBase64() {
        return Base64.encode(toBytes());
    }



    default String toBase62() {
        return Base62.encode(toBytes());
    }

    default String toBase36() {
        return Base36.encode(toBytes());
    }

    default String toBase32() {
        return Base32.encode(toBytes());
    }

    default String toBase16() {
        return Base16.encode(toBytes());
    }

    default String toBase10() {
        return new BigInteger(toBase16(), 16).toString();
    }


}
