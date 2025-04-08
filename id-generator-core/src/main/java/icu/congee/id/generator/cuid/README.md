# CUID ID 生成器

## 简介

CUID（Collision-resistant Unique IDentifier）是一个用于生成唯一标识符的解决方案，提供了 CUIDv1 和 CUIDv2 两个版本的实现。这两个版本各有特点，适用于不同的应用场景。

## 特性对比

### CUIDv1

- 水平可扩展：无需协调即可在多台机器上生成 ID
- 时序性：包含时间戳组件，支持基本的时间排序
- URL 友好：不包含特殊字符
- 高性能：同步操作，无需网络连接
- 适用于高频生成场景

### CUIDv2

- 增强的安全性：针对并行攻击和熵隐藏提供更好的保护
- 更短的 ID 长度：相比 v1 版本生成更紧凑的 ID
- 保持了 v1 的主要优点：水平扩展、URL 友好等
- 适度的生成速度：通过控制生成速度来提升安全性
- 更好的跨平台兼容性：不依赖特定的操作系统服务

## 实现原理

### CUIDv1 生成器

```java
public class CUIDv1Generator implements IdGenerator {
    @Override
    public String generate() {
        return CUID.randomCUID1().toString();
    }

    @Override
    public IdType idType() {
        return IdType.CUIDv1;
    }
}
```

### CUIDv2 生成器

```java
public class CUIDv2Generator implements IdGenerator {
    @Override
    public String generate() {
        return CUID.randomCUID2().toString();
    }

    @Override
    public IdType idType() {
        return IdType.CUIDv2;
    }
}
```

## 使用场景

### CUIDv1 适用于

- 需要基本唯一性保证的场景
- 对性能要求较高的场景
- 需要时序性的场景
- 高频 ID 生成场景（如渲染循环）

### CUIDv2 适用于

- 需要更高安全性的场景
- 对 ID 长度敏感的场景
- 跨平台应用场景
- 不建议用于：
  - 需要严格顺序的场景
  - 高性能循环场景

## 安全性考虑

- CUIDv1：提供基本的唯一性保证
- CUIDv2：
  - 通过降低生成速度来防止并行攻击
  - 提供更好的熵隐藏机制
  - 更适合安全敏感的应用

## 性能特点

- CUIDv1：
  - 生成速度快
  - 适合高频生成场景
  - 同步操作，无需等待
- CUIDv2：
  - 生成速度适中（为安全性考虑）
  - 不适合高频生成场景
  - 保持同步操作特性

## 最佳实践

1. 版本选择：

   - 普通应用场景：使用 CUIDv1
   - 安全敏感场景：使用 CUIDv2
   - 跨平台场景：优先考虑 CUIDv2

2. 性能优化：
   - 避免在高频循环中使用 CUIDv2
   - 考虑批量生成并缓存
   - 根据实际需求选择合适版本

## 注意事项

- 两个版本生成的 ID 格式不同，不要在同一应用中混用
- CUIDv2 虽然生成速度较慢，但这是出于安全考虑的设计选择
- 在选择版本时，需要在性能和安全性之间做出权衡
