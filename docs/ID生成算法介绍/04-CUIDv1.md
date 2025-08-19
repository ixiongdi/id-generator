# CUIDv1

## 简介

CUID (Collision-resistant Unique Identifiers) 是一种旨在提供安全、抗碰撞且为水平扩展和性能优化的 ID。CUIDv1 是其早期版本，后续发展为 CUID2。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

CUID 的设计目标是解决在大型应用中传统 UUID 和 GUID 可能发生的碰撞问题。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

## 特性

CUIDv1 的特性可以从其后继者 CUID2 的设计目标中推断，可能包括：

- **唯一性 (Collision resistant)**: 极难生成重复的 ID。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **安全性 (Secure)**: 难以猜测下一个 ID 或已存在的有效 ID，并且无法从 ID 中获取有关引用数据的任何信息。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **水平扩展性 (Horizontally scalable)**: 可以在多台机器上生成 ID 而无需协调。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **离线兼容性 (Offline-compatible)**: 无需网络连接即可生成 ID。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **URL 和名称友好 (URL and name-friendly)**: 不包含特殊字符。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **性能**: 旨在快速方便地生成，不会引入用户可察觉的延迟。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **不可猜测性**: 通过结合多种独立的熵源并使用安全的哈希算法来实现。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

CUIDv1 可能不适用于：

- 严格的顺序 ID（K-sortable ID）。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- 高性能的紧密循环，例如渲染循环（如果不需要跨主机唯一 ID 或安全性）。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

## 如何使用

在本项目的 `id-generator-core` 模块中，CUIDv1 的生成可以通过 `uno.xifan.id.generator.cuid.CUIDv1Generator` 类实现。以下是一个如何在 Java 中使用它的示例：

```java
import uno.xifan.id.generator.cuid.CUID;
import uno.xifan.id.generator.cuid.CUIDv1Generator;

public class CuidV1Example {
    public static void main(String[] args) {
        // 方法一：直接使用 CUID 类
        String cuid1_1 = CUID.randomCUID1().toString();
        System.out.println("CUIDv1 (direct): " + cuid1_1);

        // 方法二：使用 CUIDv1Generator (实现了 IdGenerator 接口)
        CUIDv1Generator generator = new CUIDv1Generator();
        String cuid1_2 = generator.generate();
        System.out.println("CUIDv1 (generator): " + cuid1_2);
    }
}
```

上述代码展示了两种生成 CUIDv1 的方式。第一种是直接调用 `CUID.randomCUID1()` 方法，第二种是通过 `CUIDv1Generator` 实例的 `generate()` 方法。两种方式都会返回一个 CUIDv1 格式的字符串。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

## 参考文档

- [GitHub - paralleldrive/cuid2](https://github.com/paralleldrive/cuid2) (CUID2 - CUID 的下一代) <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
