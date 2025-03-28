# CombGuid Generator

## 简介

CombGuid 是一种基于 RT.Comb 实现的可排序 UUID 变体，它通过将时间戳信息编码到 UUID 中，解决了标准 UUID 不可排序的问题。CombGuid 保持了 UUID 的唯一性和随机性，同时提供了基于时间的排序能力。

## 实现原理

CombGuid 通过重新排列标准 UUID 的字节结构来实现时间排序：

- 前 6 字节：Unix 时间戳（精确到毫秒）
- 后 10 字节：随机 UUID 数据

这种结构设计确保了：

1. 保持 UUID 的 128 位长度不变
2. 在保留唯一性的同时支持时间排序
3. 向后兼容标准 UUID 格式

### 技术细节

- 使用`ThreadLocalRandom`生成随机位，确保高性能和线程安全
- 遵循 RFC 4122 规范，设置正确的版本号（4）和变体位
- 通过位运算将时间戳编码到 UUID 的前 6 字节，保证排序效果

## 特性

1. **可排序性**：基于时间戳的前缀支持时间排序
2. **高性能**：生成过程简单，无需额外排序
3. **分布式友好**：无需中心化协调
4. **兼容性**：与标准 UUID 格式兼容
5. **时间可追溯**：可从 ID 提取生成时间

## 使用示例

```java
// 创建CombGuid生成器实例
CombGuidGenerator generator = new CombGuidGenerator();

// 生成一个新的CombGuid
String id = generator.generate();

// 直接使用静态方法生成UUID实例
UUID uuid = CombGuidGenerator.next();
```

## 性能考虑

- 使用`ThreadLocalRandom`代替`Random`，避免多线程竞争
- 位运算操作高效，几乎不增加额外开销
- 无需数据库操作，适合高并发场景

## 局限性

1. 时间戳精度限制：仅支持毫秒级精度
2. 非标准实现：某些场景可能需要特殊处理

## 许可证

本项目采用 Apache 2.0 许可证。详细信息请参见 LICENSE 文件。
