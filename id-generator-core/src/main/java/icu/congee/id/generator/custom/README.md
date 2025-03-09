# 自定义ID生成器

自定义ID生成器提供了完全可定制的ID生成功能，支持长整型（64位）和字符串两种类型的ID。用户可以自定义时间戳、工作节点ID、序列号和随机数的生成方法和位数分配，并通过占位符格式自由定义ID各部分的顺序和格式化方式。

## 功能特点

1. 支持长整型（64位）和字符串两种类型的ID
2. 完全自定义各部分的生成方法和位数分配
3. 支持通过占位符格式自由定义ID各部分的顺序和格式化方式
4. 提供默认实现，开箱即用
5. 支持链式调用配置

## 使用方法

### 长整型ID生成器

#### 基本用法

```java
// 创建默认配置的长整型ID生成器
LongIdGenerator idGenerator = new CustomLongIdGenerator();

// 生成ID
Long id = idGenerator.generate();
```

#### 自定义配置

```java
// 创建自定义配置
CustomIdConfig config = new CustomIdConfig()
    .setTimestampBits(40)  // 设置时间戳占40位
    .setWorkerIdBits(8)     // 设置工作节点ID占8位
    .setSequenceBits(15)    // 设置序列号占15位
    .setRandomBits(1)       // 设置随机数占1位
    .setEpoch(1640995200000L);  // 设置自定义纪元时间（2022-01-01 00:00:00）

// 创建自定义长整型ID生成器
LongIdGenerator idGenerator = new CustomLongIdGenerator(config);

// 生成ID
Long id = idGenerator.generate();
```

#### 自定义部分顺序和可选性

```java
// 创建自定义配置
CustomIdConfig config = new CustomIdConfig()
    // 设置各部分的位数
    .setTimestampBits(40)  // 设置时间戳占40位
    .setWorkerIdBits(8)     // 设置工作节点ID占8位
    .setSequenceBits(15)    // 设置序列号占15位
    .setRandomBits(1)       // 设置随机数占1位
    
    // 自定义部分顺序（从0开始，数字越小越靠前）
    .setTimestampOrder(2)   // 时间戳放在第三位
    .setWorkerIdOrder(0)    // 工作节点ID放在第一位
    .setSequenceOrder(1)    // 序列号放在第二位
    .setRandomOrder(3)      // 随机数放在第四位
    
    // 设置部分的可选性
    .setTimestampEnabled(true)   // 启用时间戳部分
    .setWorkerIdEnabled(true)    // 启用工作节点ID部分
    .setSequenceEnabled(true)    // 启用序列号部分
    .setRandomEnabled(false);    // 禁用随机数部分

// 创建自定义长整型ID生成器
LongIdGenerator idGenerator = new CustomLongIdGenerator(config);

// 生成ID
Long id = idGenerator.generate();
```

#### 自定义生成方法

```java
// 创建自定义配置
CustomIdConfig config = new CustomIdConfig();

// 自定义时间戳生成方法
config.setTimestampGenerator(() -> {
    // 使用当前时间的秒级时间戳
    return System.currentTimeMillis() / 1000;
});

// 自定义工作节点ID生成方法
config.setWorkerIdGenerator(() -> {
    // 使用固定的工作节点ID
    return 5L;
});

// 自定义序列号生成方法（使用原子计数器）
AtomicLong counter = new AtomicLong(0);
config.setSequenceGenerator(() -> {
    return counter.getAndIncrement() & 0xFFF; // 限制为12位
});

// 自定义随机数生成方法
config.setRandomGenerator(() -> {
    return ThreadLocalRandom.current().nextLong(2); // 1位随机数
});

// 创建自定义长整型ID生成器
LongIdGenerator idGenerator = new CustomLongIdGenerator(config);

// 生成ID
Long id = idGenerator.generate();
```

#### 使用格式化生成ID

```java
// 创建默认配置的长整型ID生成器
LongIdGenerator idGenerator = new CustomLongIdGenerator();

// 使用自定义格式生成ID
// 格式中的占位符会被替换为对应的值
Long id = idGenerator.generateWithFormat("{ts}{wid}{seq}{rnd}");

// 获取各部分的值
long timestampPart = idGenerator.getTimestampPart();
long workerIdPart = idGenerator.getWorkerIdPart();
long sequencePart = idGenerator.getSequencePart();
long randomPart = idGenerator.getRandomPart();
```

### 字符串ID生成器

#### 基本用法

```java
// 创建默认配置的字符串ID生成器
StringIdGenerator idGenerator = new CustomStringIdGenerator();

// 生成ID
String id = idGenerator.generate(); // 例如："1620000000-0-123-a1b2c3d4"
```

#### 自定义配置

```java
// 创建自定义配置
CustomStringIdConfig config = new CustomStringIdConfig()
    .setDefaultFormat("{ts}_{wid}_{seq}_{rnd}") // 设置默认格式
    .setEpoch(1640995200000L);  // 设置自定义纪元时间（2022-01-01 00:00:00）

// 创建自定义字符串ID生成器
StringIdGenerator idGenerator = new CustomStringIdGenerator(config);

// 生成ID
String id = idGenerator.generate(); // 例如："1620000000_0_123_a1b2c3d4"
```

#### 自定义部分顺序和可选性

```java
// 创建自定义配置
CustomStringIdConfig config = new CustomStringIdConfig()
    // 设置默认格式
    .setDefaultFormat("{wid}-{seq}-{ts}-{rnd}") // 自定义格式，改变了各部分的顺序
    
    // 自定义部分顺序（从0开始，数字越小越靠前）
    .setTimestampOrder(2)   // 时间戳放在第三位
    .setWorkerIdOrder(0)    // 工作节点ID放在第一位
    .setSequenceOrder(1)    // 序列号放在第二位
    .setRandomOrder(3)      // 随机数放在第四位
    
    // 设置部分的可选性
    .setTimestampEnabled(true)   // 启用时间戳部分
    .setWorkerIdEnabled(true)    // 启用工作节点ID部分
    .setSequenceEnabled(true)    // 启用序列号部分
    .setRandomEnabled(false);    // 禁用随机数部分

// 创建自定义字符串ID生成器
StringIdGenerator idGenerator = new CustomStringIdGenerator(config);

// 生成ID
String id = idGenerator.generate(); // 例如："node01-0123-1620000000"
```
```

#### 自定义生成方法

```java
// 创建自定义配置
CustomStringIdConfig config = new CustomStringIdConfig();

// 自定义时间戳生成方法
config.setTimestampGenerator(() -> {
    // 使用当前时间的格式化字符串
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
});

// 自定义工作节点ID生成方法
config.setWorkerIdGenerator(() -> {
    // 使用固定的工作节点ID
    return "node01";
});

// 自定义序列号生成方法（使用原子计数器）
AtomicLong counter = new AtomicLong(0);
config.setSequenceGenerator(() -> {
    return String.format("%04d", counter.getAndIncrement() % 10000);
});

// 自定义随机数生成方法
config.setRandomGenerator(() -> {
    return UUID.randomUUID().toString().substring(0, 8);
});

// 创建自定义字符串ID生成器
StringIdGenerator idGenerator = new CustomStringIdGenerator(config);

// 生成ID
String id = idGenerator.generate();
```

#### 使用格式化生成ID

```java
// 创建默认配置的字符串ID生成器
StringIdGenerator idGenerator = new CustomStringIdGenerator();

// 使用自定义格式生成ID
// 格式中的占位符会被替换为对应的值
String id = idGenerator.generateWithFormat("prefix-{ts}-{wid}-{seq}-{rnd}-suffix");

// 获取各部分的值
String timestampPart = idGenerator.getTimestampPart();
String workerIdPart = idGenerator.getWorkerIdPart();
String sequencePart = idGenerator.getSequencePart();
String randomPart = idGenerator.getRandomPart();
```

## 应用场景

1. 分布式系统的唯一标识生成
2. 数据库主键生成
3. 业务流水号生成
4. 会话ID生成
5. 需要自定义格式的ID生成场景

## 注意事项

1. 长整型ID生成器的各部分位数总和不能超过64位
2. 使用格式化方法生成长整型ID时，确保格式化后的结果是有效的长整型数值
3. 自定义生成方法时，注意确保生成的值不超过配置的位数限制
4. 对于高并发场景，建议使用线程安全的生成方法，如AtomicLong或ThreadLocalRandom

