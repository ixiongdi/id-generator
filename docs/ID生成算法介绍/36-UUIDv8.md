# UUIDv8

## 算法概述

UUIDv8 是 UUID（通用唯一标识符）规范的第 8 个版本，它是一种为自定义或供应商特定用途保留的 UUID 格式。在 RFC 草案中，UUIDv8 被设计为一个灵活的框架，允许开发者根据特定需求自定义 UUID 的内部结构，同时保持与标准 UUID 格式的兼容性。

本项目中的 UUIDv8 实现是一种高性能的自定义 UUID 生成器，它结合了时间戳、线程本地序列和随机数，以确保生成的 ID 既有时间顺序性又保持全局唯一性。这种实现特别适合需要时间排序和高性能的分布式系统。

## ID 结构

UUIDv8 的 128 位结构在本项目中的实现如下：

- **最高有效位(MSB)**：

  - 48 位：Unix 时间戳（10纳秒级的时间戳的高位）
  - 4 位：版本号（固定为 8）
  - 12 位：Unix 时间戳（10纳秒级的时间戳的高位）

- **最低有效位(LSB)**：
  - 2 位：变体标识（固定为 RFC 4122 规范的值）
  - 14 位：内部循环计数器
  - 48 位：分布式节点ID

标准格式表示为：`xxxxxxxx-xxxx-8xxx-yxxx-xxxxxxxxxxxx`，其中：

- `8` 表示版本号
- `y` 表示变体（通常为 8、9、A 或 B）


## 优缺点

### 优点

1. **时间顺序性**：通过将时间戳放在最高有效位，使生成的 ID 具有时间顺序性，便于数据库索引和排序
2. **高性能**：采用无锁化设计
3. **全局唯一性**：结合时间戳、序列号和机器ID，确保在分布式环境中的唯一性
4. **标准兼容**：符合 UUID 标准格式，可以与现有系统无缝集成
5. **自定义灵活性**：作为 UUIDv8，允许根据特定需求调整内部结构

### 缺点

1. **时钟依赖**：依赖系统时钟，如果系统时间发生回拨，可能影响唯一性（虽然高精度时间戳和序列号机制可以缓解这个问题）
2. **空间效率**：与一些紧凑的 ID 格式相比，128 位的 UUID 占用更多存储空间

## 适用场景

1. **分布式系统**：适用于需要在多个节点上生成唯一 ID 的分布式系统
2. **时间排序需求**：适用于需要按时间顺序检索和处理数据的应用
3. **高并发环境**：特别适合高并发环境下的 ID 生成
4. **自定义 ID 需求**：当需要在保持 UUID 兼容性的同时，根据特定业务需求自定义 ID 结构时
5. **数据库主键**：可作为数据库主键，特别是在分布式数据库系统中

## 代码示例

### 基本使用

```java
import icu.congee.id.generator.uuid.UUIDv8Generator;
import java.util.UUID;

public class UUIDv8Example {
    public static void main(String[] args) {
        // 使用静态方法生成 UUIDv8
        UUID uuid = UUIDv8Generator.next();
        System.out.println("UUIDv8: " + uuid);

        // 使用 IdGenerator 接口
        UUIDv8Generator generator = new UUIDv8Generator();
        String uuidStr = generator.generate();
        System.out.println("UUIDv8 String: " + uuidStr);
    }
}
```

### 在 Spring Boot 中使用（分布式环境）

```java
import icu.congee.id.generator.distributed.uuid.UUIDv8Generator;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdService {
    private final UUIDv8Generator uuidGenerator;

    @Autowired
    public IdService(RedissonClient redisson) {
        this.uuidGenerator = new UUIDv8Generator(redisson);
    }

    public UUID generateId() {
        return uuidGenerator.generate().toUUID();
    }
}
```

## 参考资料

- [RFC 9562 - UUID 最新规范](https://www.rfc-editor.org/rfc/rfc9562.html)
- [RFC 4122 - UUID 规范](https://tools.ietf.org/html/rfc4122)
