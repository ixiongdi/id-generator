# BroId 分布式 ID 生成器

## 设计原理

BroId 是一个自定义的分布式 ID 生成系统，具有以下特点：

- 基于布尔值列表存储 ID 的二进制表示
- 支持多种编码格式转换（Base64、Long、UUID 等）
- 实现了 Comparable 接口支持 ID 比较
- 零依赖，仅需 Java 8+

## 核心功能

1. **多种编码格式转换**

   - `toString()`: 转换为 Base64 编码
   - `toLong()`: 转换为长整型数值
   - `toUUID()`: 转换为标准 UUID
   - `toUUID(IdType)`: 转换为指定类型的 UUID（目前支持 UUIDv8）

2. **比较与相等性**

   - 实现了`compareTo()`方法，支持 ID 比较
   - 重写了`equals()`和`hashCode()`方法

3. **生成器支持**
   - 通过`BroIdGenerator`类生成 ID
   - 可自定义 ID 布局（时间戳、计数器、机器标识等）

## 使用示例

### 1. 创建雪花算法生成器

```java
BroIdGenerator<BroId> generator = BroIdExample.createSnowflakeGenerator();
BroId id = generator.next();
System.out.println("Base64编码: " + id);
System.out.println("Long值: " + id.toLong());
```

### 2. 创建 UUIDv8 生成器

```java
BroIdGenerator<BroId> generator = BroIdExample.createUUIDv8Generator();
BroId id = generator.next();
System.out.println("Base64编码: " + id);
System.out.println("UUID: " + id.toUUID());
System.out.println("UUIDv8: " + id.toUUID(IdType.UUIDv8));
```

## 性能特点

- 高性能：满足高并发要求
- 灵活：可自定义 ID 组成部分和位数
- 兼容：支持转换为标准 UUID 格式

## 适用场景

- 分布式系统需要全局唯一 ID
- 需要兼容 UUID 标准的场景
- 需要高性能 ID 生成的系统
