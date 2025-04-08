# JavaScript 安全 ID 生成器

## 简介

`JavaScriptSafetyIdGenerator`是一个专门设计的 ID 生成器，用于生成在 JavaScript 环境中安全使用的唯一标识符。它生成 53 位的整数 ID，这是 JavaScript 中 Number 类型能够安全处理的最大整数位数。

## 特性

- **JavaScript 安全性**：生成的 ID 不会超过 JavaScript 的 Number.MAX_SAFE_INTEGER 限制
- **时间相关**：包含时间戳信息，便于排序和追踪
- **高性能**：支持批量生成，适合高并发场景
- **可定制**：支持自定义纪元时间和随机数生成器

## ID 结构

生成的 53 位 ID 由两部分组成：

- **高位 37 位**：时间戳部分

  - 将当前时间戳除以 16（右移 4 位）存储
  - 这种压缩方式可以延长时间戳的使用年限
  - 避免在短时间内出现溢出

- **低位 16 位**：随机数部分
  - 用于确保同一毫秒内生成的 ID 的唯一性
  - 提供 65536 个不同的随机值

## 使用方法

### 基本使用

```java
// 创建生成器实例
JavaScriptSafetyIdGenerator generator = new JavaScriptSafetyIdGenerator();

// 生成单个ID
long id = (long) generator.generate();
```

### 批量生成

```java
// 批量生成10个ID
long[] ids = JavaScriptSafetyIdGenerator.next(10);
```

### 自定义配置

```java
// 使用自定义Random对象
Random customRandom = new SecureRandom();
JavaScriptSafetyIdGenerator generator = new JavaScriptSafetyIdGenerator(customRandom);

// 使用自定义纪元时间
long customEpoch = 1640995200000L; // 2022-01-01 00:00:00
JavaScriptSafetyIdGenerator generator = new JavaScriptSafetyIdGenerator(customRandom, customEpoch);
```

## 性能考虑

- 使用 ThreadLocalRandom 作为默认随机数生成器，保证线程安全性的同时提供良好的性能
- 批量生成方法优化了时间戳的获取，减少系统调用
- 限制单次批量生成的最大数量为 1024，防止过度消耗系统资源

## 注意事项

1. 生成的 ID 在 JavaScript 中使用时，需要注意数据类型的转换
2. 批量生成时，count 参数必须大于 0 且不超过 1024
3. 自定义纪元时间时，建议选择一个合理的时间点，避免时间戳溢出

## 应用场景

- 前后端交互时需要大数值 ID 的场景
- 需要按时间排序的唯一标识符
- 高并发系统的唯一 ID 生成
- 分布式系统中的事件 ID 生成
