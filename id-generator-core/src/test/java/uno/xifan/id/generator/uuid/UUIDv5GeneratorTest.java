package uno.xifan.id.generator.uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv5生成器的测试类
 * 
 * 测试内容包括：
 * 1. UUID格式正确性
 * 2. 版本号和变体标识的正确性
 * 3. 名称空间UUID生成的确定性
 * 4. 预定义名称空间的正确性
 */
@DisplayName("UUIDv5Generator 测试")
class UUIDv5GeneratorTest {

    private static final String TEST_NAME = "test.example.com";
    private static final String TEST_URL = "https://example.com/test";
    private static final String TEST_OID = "1.3.6.1.4.1.343";
    private static final String TEST_X500 = "CN=Test,O=Example,C=US";

    @Test
    @DisplayName("测试生成的UUID格式正确性")
    void testUUIDFormat() {
        UUID uuid = UUIDv5Generator.fromDNS(TEST_NAME);
        assertNotNull(uuid, "生成的UUID不应为null");
        String uuidString = uuid.toString();
        assertTrue(uuidString.matches("^[0-9a-f]{8}-[0-9a-f]{4}-5[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"),
                "UUID格式应符合UUIDv5标准格式");
    }

    @Test
    @DisplayName("测试UUID版本和变体标识")
    void testVersionAndVariant() {
        UUID uuid = UUIDv5Generator.fromDNS(TEST_NAME);
        assertEquals(5, uuid.version(), "UUID版本应为5");
        assertEquals(2, uuid.variant(), "UUID变体应为2（RFC 4122）");
    }

    @Test
    @DisplayName("测试名称空间UUID生成的确定性")
    void testDeterministicGeneration() {
        UUID uuid1 = UUIDv5Generator.fromDNS(TEST_NAME);
        UUID uuid2 = UUIDv5Generator.fromDNS(TEST_NAME);
        assertEquals(uuid1, uuid2, "相同名称空间和名称应生成相同的UUID");

        UUID uuid3 = UUIDv5Generator.fromDNS(TEST_NAME + "different");
        assertNotEquals(uuid1, uuid3, "不同名称应生成不同的UUID");
    }

    @Test
    @DisplayName("测试预定义DNS名称空间")
    void testDNSNamespace() {
        UUID uuid = UUIDv5Generator.fromDNS(TEST_NAME);
        assertNotNull(uuid, "使用DNS名称空间生成的UUID不应为null");
    }

    @Test
    @DisplayName("测试预定义URL名称空间")
    void testURLNamespace() {
        UUID uuid = UUIDv5Generator.fromURL(TEST_URL);
        assertNotNull(uuid, "使用URL名称空间生成的UUID不应为null");
    }

    @Test
    @DisplayName("测试预定义OID名称空间")
    void testOIDNamespace() {
        UUID uuid = UUIDv5Generator.fromOID(TEST_OID);
        assertNotNull(uuid, "使用OID名称空间生成的UUID不应为null");
    }

    @Test
    @DisplayName("测试预定义X500名称空间")
    void testX500Namespace() {
        UUID uuid = UUIDv5Generator.fromX500(TEST_X500);
        assertNotNull(uuid, "使用X500名称空间生成的UUID不应为null");
    }

    @Test
    @DisplayName("测试自定义名称空间")
    void testCustomNamespace() {
        UUID customNamespace = UUID.randomUUID();
        String name = "custom-name";
        UUID uuid1 = UUIDv5Generator.fromNamespaceAndName(customNamespace, name);
        UUID uuid2 = UUIDv5Generator.fromNamespaceAndName(customNamespace, name);

        assertNotNull(uuid1, "使用自定义名称空间生成的UUID不应为null");
        assertEquals(uuid1, uuid2, "相同的自定义名称空间和名称应生成相同的UUID");
    }
}