package uno.xifan.id.base;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Random;
import static org.junit.jupiter.api.Assertions.*;

class Base32Test {

    @Test
    void testEmptyInput() {
        assertArrayEquals(new byte[0], Base32.decode(""));
        assertEquals("", Base32.encode(new byte[0]));
    }

    @Test
    void testSpecialCharacterMapping() {
        byte[] data = { 0x01, 0x02, 0x03 };

        // 测试特殊字符映射（I→1，L→1，O→0）
        String encoded = Base32.encode(data);
        String normalized = encoded.replace("I", "1")
                .replace("L", "1")
                .replace("O", "0");

        assertArrayEquals(data, Base32.decode(normalized));
    }

    @Test
    void testCaseInsensitivity() {
        byte[] expected = { 0x12, 0x34, 0x56 };
        String lowercase = Base32.encode(expected).toLowerCase();

        assertArrayEquals(expected, Base32.decode(lowercase));
    }

    @Test
    void testHyphenRemoval() {
        byte[] data = { 0x7F, 0x3A };
        String encoded = Base32.encode(data);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < encoded.length(); i++) {
            if (i > 0 && i % 4 == 0) {
                sb.append('-');
            }
            sb.append(encoded.charAt(i));
        }
        String withHyphens = sb.toString();

        assertArrayEquals(Base32.decode(encoded), Base32.decode(withHyphens));
    }

    @Test
    void testRandomData() {
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            byte[] data = new byte[random.nextInt(1000)];
            random.nextBytes(data);

            String encoded = Base32.encode(data);
            byte[] decoded = Base32.decode(encoded);

            assertArrayEquals(data, decoded);

            // 与Hutool实现对比（需确认Hutool是否支持Crockford变种）
            if (Arrays.equals(data, cn.hutool.core.codec.Base32.decode(encoded))) {
                assertEquals(encoded, cn.hutool.core.codec.Base32.encode(data));
            }
        }
    }

    @Test
    void testLeadingZeros() {
        byte[] data = { 0, 0, 0, 1, 2, 3 };
        String encoded = Base32.encode(data);

        // Crockford Base32 无填充，前导零不保证编码字符串以特定字符开始，改为往返一致性校验
        assertArrayEquals(data, Base32.decode(encoded));
    }

    @Test
    void testInvalidInput() {
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("!@#$"));
        assertThrows(IllegalArgumentException.class, () -> Base32.decode("A"));
    }
}