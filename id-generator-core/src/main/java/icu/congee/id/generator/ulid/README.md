# ULID 生成器

ULID（Universally Unique Lexicographically Sortable Identifier）是一种基于时间戳的、可排序的唯一标识符。本实现提供了一个高性能、线程安全的 ULID 生成器。

## 特性

- **时间排序**：ULID 基于时间戳生成，天然支持按时间排序
- **高性能**：使用 SipHash 算法和优化的随机数生成
- **线程安全**：支持并发环境下的唯一性保证
- **Crockford 的 Base32 编码**：使用可读性好、不易混淆的字符集
- **128 位兼容 UUID**：提供与 UUID 相同的唯一性保证
- **无特殊字符**：生成的 ID 仅包含数字和大写字母，适合各种系统使用

## 结构

ULID 由 26 个字符组成，包含两个部分：

```
01AN4Z07BY      79KA1307SR9X4MV3
|----------|    |----------------|
  时间戳           随机数
  48bits          80bits
```

- **时间戳部分**（10 字符）

  - 48 位整数
  - 精确到毫秒的 UNIX 时间戳
  - 支持到 10889 年（远超实际需求）

- **随机数部分**（16 字符）
  - 80 位随机数
  - 使用加密安全的随机源
  - 保证同一毫秒内的唯一性

## 使用方法

### 基本使用

```java
// 创建ULID生成器实例
ULIDGenerator generator = new ULIDGenerator();

// 生成ULID
String id = generator.create();

// 生成单调递增的ULID
String monotonic = generator.next();
```

### 工具方法

```java
// 验证ULID是否有效
boolean isValid = ULIDGenerator.isValid("01AN4Z07BY79KA1307SR9X4MV3");

// 从ULID中提取时间戳
long timestamp = ULIDGenerator.unixTime("01AN4Z07BY79KA1307SR9X4MV3");
```

## 性能考虑

- 使用`SecureRandom`保证随机性
- 实现单调递增特性，避免时间回拨问题
- 使用 SipHash 算法优化随机数生成
- 同一毫秒内通过递增计数器保证唯一性

## 应用场景

- 分布式系统中的唯一标识符
- 需要按时间排序的数据记录
- 日志系统的事件 ID
- 分布式追踪的跟踪 ID
- 数据库主键

## 优势

相比传统 UUID：

- 可排序：支持时间顺序排序
- 更紧凑：26 个字符 vs 36 个字符
- 更易读：使用 Crockford 的 Base32 编码
- 性能更好：生成速度更快

## 注意事项

- 时间戳上限为+10889-08-02T05:31:50.655Z
- 建议在分布式系统中使用时确保服务器时间同步
- 随机部分使用加密安全的随机源，确保安全性

## 许可证

本项目采用 MIT 许可证。详见[LICENSE](LICENSE)文件。
