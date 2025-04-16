package icu.congee.id.base;

import cn.hutool.core.codec.Base16Codec;
import cn.hutool.core.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Base16Test {


    @Test
    void testSimpleString() {
        String input = "Hello, Base16!";
        byte[] bytes = input.getBytes(StandardCharsets.UTF_8);

        String ourEncoded = icu.congee.id.base.Base16.encode(bytes);
        String hutoolEncoded = HexUtil.encodeHexStr(bytes);

        // 验证编码格式和大写一致性
        assertEquals(ourEncoded.toUpperCase(), ourEncoded);
        assertEquals(hutoolEncoded.toUpperCase(), hutoolEncoded);

        // 验证解码正确性
        assertArrayEquals(bytes, icu.congee.id.base.Base16.decode(ourEncoded));
        assertArrayEquals(bytes, HexUtil.decodeHex(hutoolEncoded));
    }

    @Test
    void testLowercaseDecoding() {
        byte[] expected = { 0x12, 0x34, 0x56, 0x78 };
        String lowercaseHex = "12345678".toLowerCase();

        // 验证自定义实现支持小写解码
        assertArrayEquals(expected, icu.congee.id.base.Base16.decode(lowercaseHex));

        // Hutool实现预期不支持小写
        assertThrows(IllegalArgumentException.class,
                () -> Base16Codec.CODEC_LOWER.decode(lowercaseHex));
    }

    @Test
    void testRandomData() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[random.nextInt(1000)];
            random.nextBytes(data);

            String ourEncoded = icu.congee.id.base.Base16.encode(data);
            String hutoolEncoded = HexUtil.encodeHexStr(data);

            // 验证编码一致性
            assertEquals(hutoolEncoded, ourEncoded);

            // 验证解码正确性
            assertArrayEquals(data, icu.congee.id.base.Base16.decode(ourEncoded));
            assertArrayEquals(data, HexUtil.decodeHex(hutoolEncoded));
        }
    }

    @Test
    void testLeadingZeros() {
        byte[] data = { 0, 0, 0, 1, 2, 3 };
        String encoded = icu.congee.id.base.Base16.encode(data);

        assertEquals("000000010203", encoded);
        assertArrayEquals(data, icu.congee.id.base.Base16.decode(encoded));
    }

    @Test
    void testInvalidInput() {
        // 非法字符测试
        assertThrows(IllegalArgumentException.class,
                () -> icu.congee.id.base.Base16.decode("GHIJKL"));

        // 奇数长度测试
        assertThrows(IllegalArgumentException.class,
                () -> icu.congee.id.base.Base16.decode("123"));
    }
}