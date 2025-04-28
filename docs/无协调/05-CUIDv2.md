# CUIDv2

## 简介

CUIDv2 (Collision-resistant Unique IDentifier version 2) 是 CUID 的下一代版本，旨在提供安全、抗碰撞且为水平扩展和性能优化的 ID。它被设计为 UUID 和 GUID 的替代品，特别是在大型应用中这些传统 ID 可能发生碰撞的场景。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

CUIDv1 因安全原因已被弃用，推荐迁移到 CUIDv2。 <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference> <mcreference link="https://github.com/visus-io/cuid.net" index="3">3</mcreference>

## 特性

CUIDv2 具备以下主要特性：

- **安全性 (Secure)**: 难以猜测下一个 ID 或已存在的有效 ID，并且无法从 ID 中获取有关引用数据的任何信息。CUIDv2 使用多种独立的熵源，并通过经过安全审计的 NIST 标准加密安全哈希算法 (Sha3) 进行哈希处理。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **抗碰撞性 (Collision resistant)**: 生成相同 ID 的可能性极低。默认情况下，需要生成大约 4,000,000,000,000,000,000 (4e+18) 个 ID 才能达到 50% 的碰撞几率。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **水平扩展性 (Horizontally scalable)**: 可以在多台机器上生成 ID 而无需协调。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **离线兼容性 (Offline-compatible)**: 无需网络连接即可生成 ID。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **URL 和名称友好 (URL and name-friendly)**: 不包含特殊字符。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **快速且方便 (Fast and convenient)**: 生成过程不涉及异步操作，不会引入用户可察觉的延迟。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **不太快 (But not too fast)**: 如果哈希速度过快，可能会导致并行攻击以查找重复项或破坏熵隐藏。对于唯一 ID，最快的生成器在安全竞赛中会失败。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **可变长度**: CUIDv2 的长度可以调整，范围从 4 到 32 个字符，默认为 24 个字符。 <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference> <mcreference link="https://github.com/visus-io/cuid.net" index="3">3</mcreference>

## 与 CUIDv1 的区别

- **安全性**: CUIDv1 已被弃用，主要原因是其安全性不足。可以一定程度上推断出 CUIDv1 的创建时间和地点。 <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference> <mcreference link="https://github.com/visus-io/cuid.net" index="3">3</mcreference> CUIDv2 通过使用更强的加密哈希算法和多种熵源来增强安全性。
- **结构**: CUIDv2 的值遵循与其前身不同的可变结构长度，因此没有预定义的生成后外观模式。 <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference> <mcreference link="https://github.com/visus-io/cuid.net" index="3">3</mcreference>
- **熵源**: CUIDv2 结合了多种熵源，包括：
  - 一个随机选择的前缀字母 (a-z)
  - Unix 时间戳 (毫秒)
  - 包含加密强随机数据的 32 字节数组
  - 来自加密弱随机数生成器的会话计数器值
  - 包含非敏感主机信息并用加密强随机数据填充的 32 字节数组
    然后将这些信息组合起来，计算 SHA-512 (SHA-3 Keccak) 加盐哈希，并编码为 Base36 字符串。 <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference> <mcreference link="https://github.com/visus-io/cuid.net" index="3">3</mcreference>

## 不适用场景

CUIDv2 不适用于：

- **顺序 ID (Sequential ids)**: 如果需要顺序 ID，CUIDv2 不是最佳选择。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>
- **高性能紧密循环 (High performance tight loops)**: 例如渲染循环。如果不需要跨主机唯一 ID 或安全性，可以考虑使用简单的计数器，或者尝试 Ulid 或 NanoId。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference>

## 实现

CUIDv2 有多种语言的实现，例如 JavaScript 和 .NET。 <mcreference link="https://github.com/paralleldrive/cuid2" index="2">2</mcreference> <mcreference link="https://www.nuget.org/packages/cuid.net/" index="1">1</mcreference>

例如，在 Java 中使用本项目的 `id-generator-core` 模块：

```java
import icu.congee.id.generator.cuid.CUID;

// ...

String id = CUID.randomCUID2().toString(); // 例如：'tz4a98xxat96iws9zmbrgj3a'
```

可以查阅 `icu.congee.id.generator.cuid.CUID` 类了解更多关于 CUIDv2 的配置和使用方法。

## 总结

CUIDv2 是一个强大且安全的 ID 生成方案，适用于需要高唯一性、安全性和水平扩展性的现代应用程序。它通过改进 CUIDv1 的设计，提供了更强的保障，是替代传统 UUID/GUID 的一个优秀选择。
