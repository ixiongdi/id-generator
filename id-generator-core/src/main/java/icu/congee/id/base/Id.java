package icu.congee.id.base;

import cn.hutool.core.codec.Base16Codec;
import cn.hutool.core.codec.Base32;
import cn.hutool.core.codec.Base62;
import cn.hutool.core.util.HexUtil;
import icu.congee.id.base.util.CrockfordBase32Upper;
import icu.congee.id.base.util.DictionaryBase64;
import icu.congee.id.base.util.LexicalBase64;

import java.math.BigInteger;
import java.util.Base64;

public interface Id {

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

    default String toBase64() {
        return Base64.getUrlEncoder().encodeToString(toBytes());
    }

    /**
     * 将ID转换为词法排序的Base64字符串表示
     * 使用LexicalBase64编码，确保编码后的字符串保持词法排序特性
     *
     * @return 词法排序的Base64编码字符串
     */
    default String toLexicalBase64() {
        return LexicalBase64.encode(toBytes());
    }

    default String toBase62() {
        return Base62.encode(toBytes());
    }

    default String toBase36() {
        return Base36Codec.encode(toBytes());
    }

    default String toBase32() {
        return Base32.encode(toBytes());
    }

    default String toCrockfordBase32() {
        return CrockfordBase32Upper.encode(toBytes());
    }

    default String toHexString() {
        return Base16.encodeLower(toBytes());
    }

    default BigInteger toBigInteger() {
        return new BigInteger(toHexString(), 16);
    }


}
