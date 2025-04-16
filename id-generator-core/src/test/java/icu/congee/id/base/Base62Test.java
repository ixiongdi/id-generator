package icu.congee.id.base;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class Base62Test {

    @Test
    void testEmptyInput() {
        // 测试空输入
        assertEquals("", icu.congee.id.base.Base62.encode(new byte[0]));
        assertEquals("", cn.hutool.core.codec.Base62.encode(new byte[0]));

        assertArrayEquals(new byte[0], icu.congee.id.base.Base62.decode(""));
        assertArrayEquals(new byte[0], cn.hutool.core.codec.Base62.decode(""));
    }

    @Test
    void testSimpleString() {
        // 测试简单字符串
        String input = "Hello, World!";
        byte[] inputBytes = input.getBytes(StandardCharsets.UTF_8);

        String ourEncoded = icu.congee.id.base.Base62.encode(inputBytes);
        String hutoolEncoded = cn.hutool.core.codec.Base62.encode(inputBytes);

        // 验证编码结果是否只包含Base62字符
        assertTrue(ourEncoded.matches("^[0-9A-Za-z]+$"));
        assertTrue(hutoolEncoded.matches("^[0-9A-Za-z]+$"));

        // 验证解码后是否能还原原始数据
        assertArrayEquals(inputBytes, icu.congee.id.base.Base62.decode(ourEncoded));
        assertArrayEquals(inputBytes, cn.hutool.core.codec.Base62.decode(hutoolEncoded));
    }

    @Test
    void testRandomData() {
        // 测试随机数据
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            byte[] randomBytes = new byte[random.nextInt(1000)];
            random.nextBytes(randomBytes);

            String ourEncoded = icu.congee.id.base.Base62.encode(randomBytes);
            String hutoolEncoded = cn.hutool.core.codec.Base62.encode(randomBytes);

            // 验证编码结果是否只包含Base62字符
            assertTrue(ourEncoded.matches("^[0-9A-Za-z]+$"));
            assertTrue(hutoolEncoded.matches("^[0-9A-Za-z]+$"));

            // 验证解码后是否能还原原始数据
            assertArrayEquals(randomBytes, icu.congee.id.base.Base62.decode(ourEncoded));
            assertArrayEquals(randomBytes, cn.hutool.core.codec.Base62.decode(hutoolEncoded));
        }
    }

    @Test
    void testZeroBytes() {
        // 测试前导零字节
        byte[] input = new byte[]{0, 0, 1, 2, 3};

        String ourEncoded = icu.congee.id.base.Base62.encode(input);
        String hutoolEncoded = cn.hutool.core.codec.Base62.encode(input);

        assertArrayEquals(input, icu.congee.id.base.Base62.decode(ourEncoded));
        assertArrayEquals(input, cn.hutool.core.codec.Base62.decode(hutoolEncoded));
    }
}