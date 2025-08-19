package uno.xifan.id.generator.uuid;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UUIDv3Generator的单元测试类
 * 
 * 测试覆盖以下方面：
 * 1. UUID格式正确性和版本标识验证
 * 2. 确定性生成验证（相同输入产生相同输出）
 * 3. 预定义名称空间的正确性
 * 4. 自定义名称空间的使用
 * 5. 边界条件处理
 */
public class UUIDv3GeneratorTest {

    /**
     * 测试生成的UUID格式正确性
     */
    @Test
    public void testUUIDFormat() {
        UUID uuid = UUIDv3Generator.fromDNS("example.com");

        // 验证版本号为3
        assertEquals(3, uuid.version());

        // 验证变体号符合RFC 4122
        assertEquals(2, uuid.variant());

        // 验证字符串格式
        String uuidStr = uuid.toString();
        assertTrue(uuidStr.matches("^[0-9a-f]{8}-[0-9a-f]{4}-3[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"));
    }

    /**
     * 测试确定性生成（相同输入产生相同输出）
     */
    @Test
    public void testDeterministicGeneration() {
        String name = "test.example.com";

        // 使用相同的名称空间和名称生成两个UUID
        UUID first = UUIDv3Generator.fromDNS(name);
        UUID second = UUIDv3Generator.fromDNS(name);

        // 验证两个UUID相同
        assertEquals(first, second, "UUIDs generated from the same namespace and name should be identical");

        // 使用不同的名称生成UUID
        UUID third = UUIDv3Generator.fromDNS("different.example.com");

        // 验证不同名称生成的UUID不同
        assertNotEquals(first, third, "UUIDs generated from different names should be different");
    }

    /**
     * 测试预定义名称空间的正确性
     */
    @Test
    public void testPredefinedNamespaces() {
        // 验证预定义名称空间的值符合RFC 4122规范
        assertEquals("6ba7b810-9dad-11d1-80b4-00c04fd430c8", UUIDv3Generator.NAMESPACE_DNS.toString());
        assertEquals("6ba7b811-9dad-11d1-80b4-00c04fd430c8", UUIDv3Generator.NAMESPACE_URL.toString());
        assertEquals("6ba7b812-9dad-11d1-80b4-00c04fd430c8", UUIDv3Generator.NAMESPACE_OID.toString());
        assertEquals("6ba7b814-9dad-11d1-80b4-00c04fd430c8", UUIDv3Generator.NAMESPACE_X500.toString());

        // 测试使用不同预定义名称空间生成UUID
        String name = "test";
        UUID dnsUuid = UUIDv3Generator.fromDNS(name);
        UUID urlUuid = UUIDv3Generator.fromURL(name);
        UUID oidUuid = UUIDv3Generator.fromOID(name);
        UUID x500Uuid = UUIDv3Generator.fromX500(name);

        // 验证不同名称空间生成的UUID不同
        Set<UUID> uuids = new HashSet<>();
        uuids.add(dnsUuid);
        uuids.add(urlUuid);
        uuids.add(oidUuid);
        uuids.add(x500Uuid);

        assertEquals(4, uuids.size(), "UUIDs generated from different namespaces should be different");
    }

    /**
     * 测试自定义名称空间的使用
     */
    @Test
    public void testCustomNamespace() {
        // 创建自定义名称空间
        UUID customNamespace = UUID.randomUUID();
        String name = "test";

        // 使用自定义名称空间生成UUID
        UUID uuid1 = UUIDv3Generator.fromNamespaceAndName(customNamespace, name);
        UUID uuid2 = UUIDv3Generator.fromNamespaceAndName(customNamespace, name);

        // 验证版本号为3
        assertEquals(3, uuid1.version());

        // 验证相同输入产生相同输出
        assertEquals(uuid1, uuid2, "UUIDs generated from the same custom namespace and name should be identical");

        // 验证自定义名称空间生成的UUID与预定义名称空间生成的不同
        UUID dnsUuid = UUIDv3Generator.fromDNS(name);
        assertNotEquals(uuid1, dnsUuid,
                "UUID generated from custom namespace should be different from predefined namespace");
    }

    /**
     * 测试边界条件处理
     */
    @Test
    public void testEdgeCases() {
        // 测试空字符串作为名称
        UUID emptyNameUuid = UUIDv3Generator.fromDNS("");
        assertNotNull(emptyNameUuid, "UUID should be generated even with empty name");
        assertEquals(3, emptyNameUuid.version(), "UUID version should be 3");

        // 测试非常长的名称字符串
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longName.append("a");
        }

        UUID longNameUuid = UUIDv3Generator.fromDNS(longName.toString());
        assertNotNull(longNameUuid, "UUID should be generated even with very long name");
        assertEquals(3, longNameUuid.version(), "UUID version should be 3");

        // 测试特殊字符
        UUID specialCharsUuid = UUIDv3Generator.fromDNS("!@#$%^&*()_+{}[]|\\:;\"'<>,.?/");
        assertNotNull(specialCharsUuid, "UUID should be generated with special characters");
        assertEquals(3, specialCharsUuid.version(), "UUID version should be 3");

        // 测试Unicode字符
        UUID unicodeUuid = UUIDv3Generator.fromDNS("你好世界");
        assertNotNull(unicodeUuid, "UUID should be generated with Unicode characters");
        assertEquals(3, unicodeUuid.version(), "UUID version should be 3");
    }

/**
     * 测试不同名称空间和相同名称的组合
     */
    @Test
    public void testNamespaceCombinations() {
        String name = "test.example.com";
        
        // 使用不同名称空间生成UUID
        UUID uuid1 = UUIDv3Generator.fromNamespaceAndName(UUIDv3Generator.NAMESPACE_DNS, name);
        UUID uuid2 = UUIDv3Generator.fromNamespaceAndName(UUIDv3Generator.NAMESPACE_URL, name);
        UUID uuid3 = UUIDv3Generator.fromNamespaceAndName(UUIDv3Generator.NAMESPACE_OID, name);
        UUID uuid4 = UUIDv3Generator.fromNamespaceAndName(UUIDv3Generator.NAMESPACE_X500, name);
        
        // 验证不同名称空间生成的UUID不同
        assertNotEquals(uuid1, uuid2, "UUIDs from different namespaces should be different");
        assertNotEquals(uuid1, uuid3, "UUIDs from different namespaces should be different");
        assertNotEquals(uuid1, uuid4, "UUIDs from different namespaces should be different");
        assertNotEquals(uuid2, uuid3, "UUIDs from different namespaces should be different");
        assertNotEquals(uuid2, uuid4, "UUIDs from different namespaces should be different");
        assertNotEquals(uuid3, uuid4, "UUIDs from different namespaces should be different");
    }}